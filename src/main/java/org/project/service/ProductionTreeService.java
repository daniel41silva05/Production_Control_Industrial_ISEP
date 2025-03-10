package org.project.service;

import org.project.common.NaryTree;
import org.project.common.NaryTreeNode;
import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.dto.ProductionElementDTO;
import org.project.exceptions.OperationException;
import org.project.model.*;
import org.project.exceptions.ProductException;
import org.project.repository.*;

import java.util.*;

public class ProductionTreeService {

    private DatabaseConnection connection;
    private ProductRepository productRepository;
    private ComponentRepository componentRepository;
    private RawMaterialRepository rawMaterialRepository;
    private ProductionTreeRepository productionTreeRepository;
    private OperationRepository operationRepository;

    public ProductionTreeService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        productRepository = repositories.getProductRepository();
        componentRepository = repositories.getComponentRepository();
        rawMaterialRepository = repositories.getRawMaterialRepository();
        productionTreeRepository = repositories.getProductionTreeRepository();
        operationRepository = repositories.getOperationRepository();
    }

    public void createProductionTree (String productID, HashMap<ProductionElementDTO, List<Integer>> elementNextOperationsDto) throws ProductException, OperationException {
        if (!productRepository.getProductExists(connection, productID)) {
            throw ProductException.productNotFound(productID);
        }

        HashMap<ProductionElement, List<Integer>> elementNextOperations = new HashMap<>();

        boolean productFound = false;
        for (Map.Entry<ProductionElementDTO, List<Integer>> entry : elementNextOperationsDto.entrySet()) {
            ProductionElementDTO productionElementDTO = entry.getKey();
            String partID = productionElementDTO.getPartId();
            int operationID = productionElementDTO.getOperationId();
            double quantity = productionElementDTO.getQuantity();

            if (!operationRepository.getOperationExists(connection, operationID)) {
                throw new OperationException("Operation with ID " + operationID + " does not exist.");
            }
            Operation operation = operationRepository.getById(connection, operationID);

            if (!productRepository.getPartExists(connection, partID)) {
                throw new ProductException("Part with ID " + partID + " does not exist.");
            }
            ProductionElement element;
            if (entry.getValue().isEmpty()) {
                if (!rawMaterialRepository.getRawMaterialExists(connection, partID)) {
                    throw new ProductException("Part ID " + partID + " - Only the raw materials have nothing to form them in the production tree.");
                }
                RawMaterial rawMaterial = rawMaterialRepository.getRawMaterialByID(connection, partID);
                element = new ProductionElement(rawMaterial, operation, quantity);
            } else {
                if (componentRepository.getComponentExists(connection, partID)) {
                    Component component = componentRepository.getComponentByID(connection, partID);
                    element = new ProductionElement(component, operation, quantity);
                } else if (productRepository.getProductExists(connection, partID)) {
                    if (partID.equals(productID)) {
                        productFound = true;
                    } else if (!productionTreeRepository.getProductionTreeExists(connection, partID)) {
                        throw new ProductException("Product with ID " + partID + " does not have any production tree in the system.");
                    }
                    Product product = productRepository.getProductByID(connection, partID);
                    element = new ProductionElement(product, operation, quantity);
                } else {
                    throw new ProductException("Part ID " + partID + " - All raw materials have nothing to form them in the production tree.");
                }
            }

            elementNextOperations.put(element, entry.getValue());
        }
        if (!productFound) {
            throw new ProductException("Product with ID " + productID + " does not exist in csv file.");
        }

        HashMap<ProductionElement, Integer> map = buildTree(productID, elementNextOperations);

        productionTreeRepository.saveProductionTree(connection, productID, map);

    }

    public HashMap<ProductionElement, Integer> buildTree(String productID, HashMap<ProductionElement, List<Integer>> elementNextOperations) throws OperationException {

        HashMap<ProductionElement, Integer> elementParentOperationMap = new HashMap<>();

        for (Map.Entry<ProductionElement, List<Integer>> entry : elementNextOperations.entrySet()) {
            ProductionElement element = entry.getKey();
            List<Integer> nextOperations = entry.getValue();

            for (Integer operation : nextOperations) {
                ProductionElement childElement = findElementByOperation(operation, elementNextOperations.keySet());
                if (childElement == null) {
                    throw new OperationException("Operation " + operation + " not found in the production elements.");
                }
                elementParentOperationMap.put(childElement, element.getOperation().getId());
            }

            if (element.getPart().getId().equals(productID)) {
                elementParentOperationMap.put(element, null);
            }
        }

        return elementParentOperationMap;
    }

    private ProductionElement findElementByOperation(Integer operation, Set<ProductionElement> elements) {
        for (ProductionElement element : elements) {
            if (element.getOperation().getId() == operation) {
                return element;
            }
        }
        return null;
    }

    public NaryTree<ProductionElement> getProductionTree (String productID) {
        if (!productRepository.getProductExists(connection, productID)) {
            throw ProductException.productNotFound(productID);
        }

        LinkedHashMap<ProductionElement, Integer> elementParentOperationMap = productionTreeRepository.getProductionHierarchy(connection, productID);

        LinkedHashMap<ProductionElement, List<Integer>> map = invertParentChildRelationships(elementParentOperationMap);

        ProductionElement elementRoot = null;
        for (ProductionElement element : map.keySet()) {
            if (element.getPart().getId().equals(productID)) {
                elementRoot = element;
                break;
            }
        }
        NaryTree<ProductionElement> tree = new NaryTree<>(elementRoot);

        Map<Integer, ProductionElement> operationElementMap = new HashMap<>();
        for (Map.Entry<ProductionElement, List<Integer>> entry : map.entrySet()) {
            ProductionElement element = entry.getKey();
            List<Integer> operations = entry.getValue();
            for (Integer operationId : operations) {
                operationElementMap.put(operationId, element);
            }
        }

        constructSubTree(map, operationElementMap, tree.getRoot());

        return tree;
    }

    private void constructSubTree(HashMap<ProductionElement, List<Integer>> map, Map<Integer, ProductionElement> operationElementMap, NaryTreeNode<ProductionElement> node) {
        ProductionElement element = node.getElement();
        List<Integer> children = map.get(element);

        if (children != null) {
            for (Integer childOperation : children) {
                ProductionElement childElement = operationElementMap.get(childOperation);
                NaryTreeNode<ProductionElement> childNode = node.addChild(childElement);
                constructSubTree(map, operationElementMap, childNode);
            }
        }
    }

    public LinkedHashMap<ProductionElement, List<Integer>> invertParentChildRelationships(LinkedHashMap<ProductionElement, Integer> elementParentOperationMap) {
        LinkedHashMap<ProductionElement, List<Integer>> elementNextOperations = new LinkedHashMap<>();

        for (Map.Entry<ProductionElement, Integer> entry : elementParentOperationMap.entrySet()) {
            ProductionElement childElement = entry.getKey();
            Integer parentOperationID = entry.getValue();

            ProductionElement parentElement = findElementByOperation(parentOperationID, elementParentOperationMap.keySet());

            if (parentElement != null) {
                elementNextOperations.computeIfAbsent(parentElement, k -> new ArrayList<>()).add(childElement.getOperation().getId());
            }
        }

        return elementNextOperations;
    }

    public void discountRawMaterialStock (Order order) {
        if (order == null) {
            return;
        }

        Map<Product, Integer> productQuantity = order.getProductQuantity();
        for (Map.Entry<Product, Integer> productEntry : productQuantity.entrySet()) {
            Product product = productEntry.getKey();
            int quantity = productEntry.getValue();

            if (!productionTreeRepository.getProductionTreeExists(connection, product.getId())) {
                throw ProductException.productionTreeNotFound(product.getId());
            }
            NaryTree<ProductionElement> tree = getProductionTree(product.getId());

            applyStockDeduction(tree.getRoot(), quantity);
        }

    }

    public void applyStockDeduction(NaryTreeNode<ProductionElement> node, int quantity) {
        if (node == null) {
            return;
        }

        Part part = node.getElement().getPart();
        if (part instanceof RawMaterial) {
            int currentStock = ((RawMaterial) part).getCurrentStock();
            int newCurrentStock = (int) (currentStock - node.getElement().getQuantity() * quantity);
            if (newCurrentStock < 0) {
                throw ProductException.notEnoughStock(part.getId());
            }
            ((RawMaterial) part).setCurrentStock(newCurrentStock);
            rawMaterialRepository.updateRawMaterial(connection, (RawMaterial) part);
        }

        for (NaryTreeNode<ProductionElement> child : node.getChildren()) {
            applyStockDeduction(child, quantity);
        }
    }

}
