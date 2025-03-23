package org.project.ui;

import org.project.controller.RawMaterialController;
import org.project.exceptions.DatabaseException;
import org.project.model.RawMaterial;
import org.project.model.Supplier;
import org.project.ui.utils.Utils;

import java.util.List;
import java.util.Map;

public class ConsultSuppliersRawMaterialUI implements Runnable {

    private final RawMaterialController rawMaterialController;

    public ConsultSuppliersRawMaterialUI() {
        this.rawMaterialController = new RawMaterialController();
    }

    public void run() {
        try {
            showRawMaterials(rawMaterialController.getRawMaterials());

            String rawMaterialID = Utils.readLineFromConsole("Enter Raw Material ID: ");

            List<Map.Entry<Supplier, Double>> suppliers = rawMaterialController.getSuppliersByCost(rawMaterialID);
            showSuppliers(suppliers);

        } catch (DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showSuppliers(List<Map.Entry<Supplier, Double>> suppliers) {
        System.out.println("\nSuppliers:");
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers.");
        } else {
            for (Map.Entry<Supplier, Double> entry : suppliers) {
                System.out.println(" - Supplier ID: " + entry.getKey().getId() + " | Cost: " + entry.getValue());
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

}
