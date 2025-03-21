package org.project.controller;

import org.project.common.NaryTree;
import org.project.model.ProductionElement;
import org.project.service.ProductionTreeService;

public class ProductionController {

    private ProductionTreeService productionTreeService;

    public ProductionController() {
        productionTreeService = new ProductionTreeService();
    }

    public NaryTree<ProductionElement> getProductionTree(String productID) {
        return productionTreeService.getProductionTree(productID);
    }

    public void createProductionTree(String productID, String filePath) {
        productionTreeService.createProductionTree(productID, filePath);
    }

}