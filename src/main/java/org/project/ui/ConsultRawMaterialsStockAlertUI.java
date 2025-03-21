package org.project.ui;

import org.project.controller.RawMaterialController;
import org.project.exceptions.DatabaseException;
import org.project.model.RawMaterial;

import java.util.List;

public class ConsultRawMaterialsStockAlertUI implements Runnable {

    private final RawMaterialController controller;

    public ConsultRawMaterialsStockAlertUI() {
        this.controller = new RawMaterialController();
    }

    public void run() {
        try {

        List<RawMaterial> rawMaterials = controller.consultRawMaterialsStockAlert();
        System.out.println("\nRaw Materials in Stock Alert:");
        if (rawMaterials.isEmpty()) {
            System.out.println("No raw materials.");
        } else {
            for (RawMaterial rawMaterial : rawMaterials) {
                System.out.println(" - Raw Material ID: " + rawMaterial.getId() + " | Name: " + rawMaterial.getName() + " | Current Stock: " + rawMaterial.getCurrentStock() + " | Minimum Stock: " + rawMaterial.getMinimumStock());
            }
        }

        } catch (DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

}
