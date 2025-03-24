package org.project.ui;

import org.project.controller.ProductionController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.OperationException;
import org.project.exceptions.ProductException;
import org.project.ui.utils.Utils;

public class LoadProductionTreeCsvUI implements Runnable {

    private final ProductionController controller;

    public LoadProductionTreeCsvUI() {
        this.controller = new ProductionController();
    }

    public void run() {

        try {

        String productID = Utils.readLineFromConsole("Enter Product ID: ");

        String file = Utils.readLineFromConsole("Enter file name (ex. boo.csv): ");

        controller.createProductionTree(productID, "files\\" + file);

        System.out.println("\nProduction tree upload completed successfully.");

        } catch (ProductException | OperationException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

}
