package org.project.controller;

import org.project.common.NaryTree;
import org.project.model.Order;
import org.project.model.Product;
import org.project.model.ProductionElement;
import org.project.model.RawMaterial;
import org.project.service.OrderService;
import org.project.service.ProductService;
import org.project.service.ProductionTreeService;

import java.util.List;

public class ProductionController {

    private ProductionTreeService productionTreeService;
    private ProductService productService;
    private OrderService orderService;

    public ProductionController() {
        productionTreeService = new ProductionTreeService();
        productService = new ProductService();
        orderService = new OrderService();
    }

    public NaryTree<ProductionElement> getProductionTree(String productID) {
        return productionTreeService.getProductionTree(productID);
    }

    public void createProductionTree(String productID, String filePath) {
        productionTreeService.createProductionTree(productID, filePath);
    }

    public List<RawMaterial> getInsufficientRawMaterialStockOrder(int orderID) {
        Order order = orderService.getOrderByID(orderID);
        return productionTreeService.getInsufficientRawMaterialStockOrder(order);
    }

    public List<RawMaterial> getInsufficientRawMaterialStockForProduct(String productID) {
        Product product = productService.getProductByID(productID);
        return productionTreeService.getInsufficientRawMaterialStockForProduct(product);
    }

}