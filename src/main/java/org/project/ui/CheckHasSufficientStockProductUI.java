package org.project.ui;

import org.project.controller.ProductController;
import org.project.controller.ProductionController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.OperationException;
import org.project.exceptions.ProductException;
import org.project.model.Product;
import org.project.model.RawMaterial;
import org.project.ui.utils.Utils;

import java.util.List;

public class CheckHasSufficientStockProductUI implements Runnable {

    private final ProductionController controller;
    private final ProductController productController;

    public CheckHasSufficientStockProductUI() {
        this.controller = new ProductionController();
        this.productController = new ProductController();
    }

    public void run() {

        try {

        for (Product product : productController.getProducts()) {
            showProduct(product);
        }

        String productID = Utils.readLineFromConsole("Enter Product ID: ");

        List<RawMaterial> insufficientStock = controller.getInsufficientRawMaterialStockForProduct(productID);

        if (insufficientStock.isEmpty()) {
            System.out.println("\nAll raw materials have sufficient stock.");
        } else {
            System.out.println("\nInsufficient stock for the following raw materials:");
            for (RawMaterial rawMaterial : insufficientStock) {
                System.out.println(" - Raw Material ID: " + rawMaterial.getId() + " | Name: " + rawMaterial.getName());
            }
        }

        } catch (ProductException | OperationException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showProduct(Product product) {
        System.out.println("Product ID: " + product.getId());
        System.out.println(" - Name: " + product.getName());
        System.out.println(" - Description: " + product.getDescription());
        System.out.println(" - Category ID: " + product.getCategory().getId());
        System.out.println(" - Category Name: " + product.getCategory().getName());
        System.out.println(" - Size: " + product.getSize());
        System.out.println(" - Capacity: " + product.getCapacity());
        System.out.println(" - Color: " + product.getColor());
        System.out.println(" - Price: " + product.getPrice() + "$\n");
    }

}
