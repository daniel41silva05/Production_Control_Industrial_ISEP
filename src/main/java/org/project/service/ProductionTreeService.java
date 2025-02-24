package org.project.service;

import org.project.common.NaryTree;
import org.project.common.NaryTreeNode;
import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.domain.Product;
import org.project.domain.ProductionElement;
import org.project.exceptions.ProductException;
import org.project.repository.ProductRepository;
import org.project.repository.Repositories;

import java.util.*;

public class ProductionTreeService {

    private DatabaseConnection connection;
    private ProductRepository productRepository;

    public ProductionTreeService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        productRepository = repositories.getProductRepository();
    }

    public NaryTree<ProductionElement> buildTree(String productID, HashMap<ProductionElement, List<Integer>> map) throws ProductException {
        if (!productRepository.getProductExists(connection, productID)) {
            throw new ProductException("Product with ID " + productID + " does not exist.");
        }

        Product product = productRepository.getByID(connection, productID);

        ProductionElement elementRoot = null;
        for (ProductionElement element : map.keySet()) {
            if (element.getPart().equals(product)) {
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

        buildSubTree(map, operationElementMap, tree.getRoot());

        return tree;
    }

    private void buildSubTree(HashMap<ProductionElement, List<Integer>> map, Map<Integer, ProductionElement> operationElementMap, NaryTreeNode<ProductionElement> node) {
        ProductionElement element = node.getElement();
        List<Integer> children = map.get(element);

        if (children != null) {
            for (Integer childOperation : children) {
                ProductionElement childElement = operationElementMap.get(childOperation);
                NaryTreeNode<ProductionElement> childNode = node.addChild(childElement);
                buildSubTree(map, operationElementMap, childNode);
            }
        }
    }

}
