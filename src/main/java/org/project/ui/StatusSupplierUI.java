package org.project.ui;

import org.project.controller.SupplierController;
import org.project.exceptions.DatabaseException;
import org.project.model.Supplier;

import java.util.List;

public class StatusSupplierUI implements Runnable {

    private final SupplierController controller;

    public StatusSupplierUI() {
        this.controller = new SupplierController();
    }

    public void run() {
        try {
            List<Supplier> suppliers = controller.updateSupplierStatus();

            System.out.println("\nSuppliers:");
            if (suppliers.isEmpty()) {
                System.out.println("No suppliers registered.");
            } else {
                for (Supplier supplier : suppliers) {
                    System.out.println(" - Supplier ID: " + supplier.getId() + " | Name: " + supplier.getName() + " | State: " + supplier.getState());
                }
            }

        } catch (DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

}
