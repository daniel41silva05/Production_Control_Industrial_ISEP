package org.project.ui;

import org.project.controller.SupplierController;
import org.project.exceptions.ClientException;
import org.project.exceptions.DatabaseException;
import org.project.model.Supplier;
import org.project.ui.utils.Utils;

import java.util.List;

public class RegisterSupplierUI implements Runnable {

    private final SupplierController controller;

    public RegisterSupplierUI() {
        this.controller = new SupplierController();
    }

    public void run() {
        try {
            showSuppliers(controller.getSuppliers());

            boolean register = Utils.confirm("Do you want to register a new supplier?");
            if (!register) {
                return;
            }

            int supplierID = Utils.readIntegerFromConsole("Enter Supplier ID: ");
            String name = Utils.readLineFromConsole("Enter Name: ");
            int phoneNumber = Utils.readPhoneNumberFromConsole("Enter Phone Number: ");
            String email = Utils.readEmailFromConsole("Enter Email: ");

            Supplier supplier = controller.registerSupplier(supplierID, name, phoneNumber, email);
            if (supplier == null) {
                System.out.println("\nSupplier registration failed.");
            } else {
                System.out.println("\nSupplier registered successfully:");
                showSupplier(supplier);
            }
        } catch (ClientException | DatabaseException e) {
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
