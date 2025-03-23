package org.project.ui;

import org.project.controller.SupplierController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.SupplierException;
import org.project.model.Supplier;
import org.project.ui.utils.Utils;

import java.util.List;

public class DeleteSupplierUI implements Runnable {

    private final SupplierController controller;

    public DeleteSupplierUI() {
        this.controller = new SupplierController();
    }

    public void run() {
        try {
            showSuppliers(controller.getSuppliers());

            boolean delete = Utils.confirm("Do you want to delete a supplier?");
            if (!delete) {
                return;
            }

            int supplierID = Utils.readIntegerFromConsole("Enter Supplier ID: ");

            Supplier supplier = controller.deleteSupplier(supplierID);
            if (supplier == null) {
                System.out.println("\nFailed to delete supplier.");
            } else {
                System.out.println("\nSupplier deleted successfully.");
            }

        } catch (SupplierException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showSuppliers(List<Supplier> suppliers) {
        System.out.println("\nSuppliers:");
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers registered.");
        } else {
            for (Supplier supplier : suppliers) {
                System.out.println(" - Supplier ID: " + supplier.getId() + " | Name: " + supplier.getName());
            }
        }
    }

}
