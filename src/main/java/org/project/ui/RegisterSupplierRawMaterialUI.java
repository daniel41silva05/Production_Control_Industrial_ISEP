package org.project.ui;

import org.project.controller.*;
import org.project.exceptions.*;
import org.project.model.*;
import org.project.ui.utils.Utils;

import java.util.List;
import java.util.Map;

public class RegisterSupplierRawMaterialUI implements Runnable {

    private final SupplierController supplierController;
    private final RawMaterialController rawMaterialController;

    public RegisterSupplierRawMaterialUI() {
        this.supplierController = new SupplierController();
        this.rawMaterialController = new RawMaterialController();
    }

    public void run() {
        try {
            showSuppliers(supplierController.getSuppliers());
            showRawMaterials(rawMaterialController.getRawMaterials());

            String rawMaterialID = Utils.readLineFromConsole("Enter Raw Material ID: ");
            RawMaterial rawMaterial = rawMaterialController.getRawMaterialByID(rawMaterialID);

            showSuppliersRawMaterial(rawMaterial);

            boolean confirm = Utils.confirm("Do you want to associate a new supplier to the raw material?");
            if (!confirm) {
                return;
            }

            int supplierID = Utils.readIntegerFromConsole("Enter Supplier ID: ");
            double cost = Utils.readDoubleFromConsole("Enter Cost: ");

            rawMaterial = rawMaterialController.registerRawMaterialSupplier(rawMaterial, supplierID, cost);
            if (rawMaterial == null) {
                System.out.println("\nRegistration failed.");
            } else {
                System.out.println("\nRegistered successfully.");
                showSuppliersRawMaterial(rawMaterial);
            }

        } catch (SupplierException | ProductException | DatabaseException e) {
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

    private void showRawMaterials(List<RawMaterial> rawMaterials) {
        System.out.println("\nRaw Materials:");
        if (rawMaterials.isEmpty()) {
            System.out.println("No raw materials registered.");
        } else {
            for (RawMaterial rawMaterial : rawMaterials) {
                System.out.println(" - Raw Material ID: " + rawMaterial.getId() + " | Name: " + rawMaterial.getName());
            }
        }
    }

    private void showSuppliersRawMaterial (RawMaterial rawMaterial) {
        System.out.println("\nRaw Material ID: " + rawMaterial.getId() + " | Name: " + rawMaterial.getName());
        Map<Supplier, Double> suppliers = rawMaterial.getRawMaterialCost();
        if (!suppliers.isEmpty()) {
            System.out.println("Suppliers: ");
            for (Map.Entry<Supplier, Double> entry : suppliers.entrySet()) {
                System.out.println(" - Supplier ID: " + entry.getKey().getId() + " | Name: " + entry.getKey().getName() + " | Cost: " + entry.getValue() + "$");
            }
        }
    }

}
