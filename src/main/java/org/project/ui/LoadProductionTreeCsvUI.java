package org.project.ui;

import org.project.controller.ProductController;
import org.project.controller.ProductionController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.OperationException;
import org.project.exceptions.ProductException;
import org.project.model.Product;
import org.project.ui.utils.Utils;

public class LoadProductionTreeCsvUI implements Runnable {

    private final ProductionController controller;
    private final ProductController productController;

    public LoadProductionTreeCsvUI() {
        this.controller = new ProductionController();
        this.productController = new ProductController();
    }

    public void run() {

        try {

        for (Product product : productController.getProducts()) {
            showProduct(product);
        }

        String productID = Utils.readLineFromConsole("Enter Product ID: ");

        String file = Utils.readLineFromConsole("Enter file name (ex. boo.csv): ");

        controller.createProductionTree(productID, "files\\" + file);

        System.out.println("\nProduction tree upload completed successfully.");

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
