package org.project.service;

import org.project.common.NaryTree;
import org.project.common.NaryTreeNode;
import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.model.ProductionElement;
import org.project.exceptions.ProductException;
import org.project.repository.ProductRepository;
import org.project.repository.ProductionTreeRepository;
import org.project.repository.Repositories;

import java.util.*;

public class ProductionTreeService {

    private DatabaseConnection connection;
    private ProductRepository productRepository;
    private ProductionTreeRepository productionTreeRepository;

    public ProductionTreeService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        productRepository = repositories.getProductRepository();
        productionTreeRepository = repositories.getProductionTreeRepository();
    }

    public void addTree (String productID, HashMap<ProductionElement, List<Integer>> elementNextOperations) throws ProductException {
        if (!productRepository.getProductExists(connection, productID)) {
            throw new ProductException("Product with ID " + productID + " does not exist.");
        }

        HashMap<ProductionElement, Integer> map = buildTree(productID, elementNextOperations);

        boolean found = false;
        for (Map.Entry<ProductionElement, Integer> entry : map.entrySet()) {
            ProductionElement element = entry.getKey();
            if (element.getPart().getId().equals(productID)) {
                found = true;
                if (entry.getValue() == null) {
                    throw new ProductException("Product with ID " + productID + " ...");
                }
                break;
            }
        }
        if (!found) {
            throw new ProductException("Product with ID " + productID + " does not exist in csv file.");
        }

        for (Map.Entry<ProductionElement, Integer> entry : map.entrySet()) {
            // verificações nos outros repositorios
        }

        boolean success = productionTreeRepository.saveProductionTree(connection, productID, map);
        if (!success) {
            throw new ProductException("Product with ID " + productID + " could not be saved.");
        }

    }

    public HashMap<ProductionElement, Integer> buildTree(String productID, HashMap<ProductionElement, List<Integer>> elementNextOperations) throws ProductException {

        HashMap<ProductionElement, Integer> elementParentOperationMap = new HashMap<>();

        for (Map.Entry<ProductionElement, List<Integer>> entry : elementNextOperations.entrySet()) {
            ProductionElement element = entry.getKey();
            List<Integer> nextOperations = entry.getValue();

            for (Integer operation : nextOperations) {
                ProductionElement childElement = findElementByOperation(operation, elementNextOperations.keySet());
                if (childElement == null) {
                    throw new ProductException("Operation " + operation + " not found in the production elements.");
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


    public NaryTree<ProductionElement> getTree (String productID) throws ProductException {
        if (!productRepository.getProductExists(connection, productID)) {
            throw new ProductException("Product with ID " + productID + " does not exist.");
        }

        LinkedHashMap<ProductionElement, Integer> elementParentOperationMap = productionTreeRepository.getProductionHierarchy(connection, productID);

        LinkedHashMap<ProductionElement, List<Integer>> map = buildReverseTree(elementParentOperationMap);

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

        buildSubTree2(map, operationElementMap, tree.getRoot());

        return tree;
    }

    private void buildSubTree2(HashMap<ProductionElement, List<Integer>> map, Map<Integer, ProductionElement> operationElementMap, NaryTreeNode<ProductionElement> node) {
        ProductionElement element = node.getElement();
        List<Integer> children = map.get(element);

        if (children != null) {
            for (Integer childOperation : children) {
                ProductionElement childElement = operationElementMap.get(childOperation);
                NaryTreeNode<ProductionElement> childNode = node.addChild(childElement);
                buildSubTree2(map, operationElementMap, childNode);
            }
        }
    }

    public LinkedHashMap<ProductionElement, List<Integer>> buildReverseTree(LinkedHashMap<ProductionElement, Integer> elementParentOperationMap) {
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

}
