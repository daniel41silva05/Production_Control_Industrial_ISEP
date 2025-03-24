package org.project.service;

import org.project.common.NaryTree;
import org.project.common.NaryTreeNode;
import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.dto.ProductionElementDTO;
import org.project.exceptions.OperationException;
import org.project.io.CsvReader;
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

    public void createProductionTree (String productID, String filePath) {
        HashMap<ProductionElementDTO, List<Integer>> elementNextOperationsDto = CsvReader.loadBOO(filePath);

        if (productionTreeRepository.getProductionTreeExists(connection, productID)) {
            throw ProductException.productionTreeAlreadyExists(productID);
        }

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

            Operation operation = operationRepository.getById(connection, operationID);
            if (operation == null) {
                throw OperationException.operationNotFound(operationID);
            }

            if (!productRepository.getPartExists(connection, partID)) {
                throw ProductException.partNotFound(partID);
            }
            ProductionElement element;
            if (entry.getValue().isEmpty()) {
                if (!rawMaterialRepository.getRawMaterialExists(connection, partID)) {
                    throw ProductException.rawMaterialNoProductionTree(partID);
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
                        throw ProductException.productNoProductionTree(partID);
                    }
                    Product product = productRepository.getProductByID(connection, partID);
                    element = new ProductionElement(product, operation, quantity);
                } else {
                    throw ProductException.allRawMaterialsNoProductionTree(partID);
                }
            }

            elementNextOperations.put(element, entry.getValue());
        }
        if (!productFound) {
            throw ProductException.productNotInCsv(productID);
        }

        HashMap<ProductionElement, Integer> map = buildTree(productID, elementNextOperations);

        productionTreeRepository.saveProductionTree(connection, productID, map);

    }

    public HashMap<ProductionElement, Integer> buildTree(String productID, HashMap<ProductionElement, List<Integer>> elementNextOperations) {

        HashMap<ProductionElement, Integer> elementParentOperationMap = new HashMap<>();

        for (Map.Entry<ProductionElement, List<Integer>> entry : elementNextOperations.entrySet()) {
            ProductionElement element = entry.getKey();
            List<Integer> nextOperations = entry.getValue();

            for (Integer operation : nextOperations) {
                ProductionElement childElement = findElementByOperation(operation, elementNextOperations.keySet());
                if (childElement == null) {
                    throw OperationException.operationNotFoundInProduction(operation);
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
        if (operation == null) {
            return null;
        }

        for (ProductionElement element : elements) {
            if (element.getOperation().getId() == operation) {
                return element;
            }
        }

        return null;
    }

    public NaryTree<ProductionElement> getProductionTree(String productID) {
        if (!productRepository.getProductExists(connection, productID)) {
            throw ProductException.productNotFound(productID);
        }

        HashMap<ProductionElement, Integer> elementParentOperationMap =
                productionTreeRepository.getProductionHierarchy(connection, productID);

        LinkedHashMap<ProductionElement, List<ProductionElement>> parentToChildren =
                invertParentChildRelationships(elementParentOperationMap);

        ProductionElement rootElement = null;
        for (ProductionElement element : elementParentOperationMap.keySet()) {
            if (elementParentOperationMap.get(element) == null && element.getPart().getId().equals(productID)) {
                rootElement = element;
                break;
            }
        }

        NaryTree<ProductionElement> tree = new NaryTree<>(rootElement);
        constructSubTree(parentToChildren, tree.getRoot());
        return tree;
    }

    private void constructSubTree(Map<ProductionElement, List<ProductionElement>> parentToChildren, NaryTreeNode<ProductionElement> node) {
        ProductionElement currentElement = node.getElement();
        List<ProductionElement> children = parentToChildren.get(currentElement);

        if (children != null) {
            for (ProductionElement childElement : children) {
                NaryTreeNode<ProductionElement> childNode = node.addChild(childElement);
                constructSubTree(parentToChildren, childNode);
            }
        }
    }

    public LinkedHashMap<ProductionElement, List<ProductionElement>> invertParentChildRelationships(
            HashMap<ProductionElement, Integer> elementParentOperationMap
    ) {
        LinkedHashMap<ProductionElement, List<ProductionElement>> parentToChildren = new LinkedHashMap<>();

        Map<Integer, ProductionElement> operationToElement = new HashMap<>();
        for (ProductionElement element : elementParentOperationMap.keySet()) {
            operationToElement.put(element.getOperation().getId(), element);
        }

        for (Map.Entry<ProductionElement, Integer> entry : elementParentOperationMap.entrySet()) {
            ProductionElement childElement = entry.getKey();
            Integer parentOperationId = entry.getValue();

            if (parentOperationId != null) {
                ProductionElement parentElement = operationToElement.get(parentOperationId);
                if (parentElement != null) {
                    parentToChildren.computeIfAbsent(parentElement, k -> new ArrayList<>()).add(childElement);
                }
            }
        }

        return parentToChildren;
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
