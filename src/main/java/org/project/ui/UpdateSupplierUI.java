package org.project.ui;

import org.project.controller.SupplierController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.SupplierException;
import org.project.model.Supplier;
import org.project.ui.utils.Utils;

import java.util.List;

public class UpdateSupplierUI implements Runnable {

    private final SupplierController controller;

    public UpdateSupplierUI() {
        this.controller = new SupplierController();
    }

    public void run() {
        try {
            showSuppliers(controller.getSuppliers());

            boolean update = Utils.confirm("Do you want to update a supplier's information?");
            if (!update) {
                return;
            }

            int supplierID = Utils.readIntegerFromConsole("Enter Supplier ID to update: ");

            Supplier supplier = controller.getSupplierByID(supplierID);

            showSupplier(supplier);

            int phoneNumber = supplier.getPhoneNumber();
            String email = supplier.getEmail();

            if (Utils.confirm("Do you want to update the Email?")) {
                email = Utils.readEmailFromConsole("Enter new Email: ");
            }

            if (Utils.confirm("Do you want to update the Phone Number?")) {
                phoneNumber = Utils.readPhoneNumberFromConsole("Enter new Phone Number: ");
            }

            Supplier newSupplier = controller.updateSupplier(supplier, phoneNumber, email);
            if (newSupplier != null) {
                System.out.println("\nSupplier updated successfully.");
                showSupplier(newSupplier);
            } else {
                System.out.println("\nSupplier update failed.");
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

    private void showSupplier(Supplier supplier) {
        System.out.println(" - Supplier ID: " + supplier.getId());
        System.out.println(" - Name: " + supplier.getName());
        System.out.println(" - Phone Number: " + supplier.getPhoneNumber());
        System.out.println(" - Email: " + supplier.getEmail());
        System.out.println(" - State: " + supplier.getState());
    }

}
