package org.project.ui;

import org.project.controller.RawMaterialController;
import org.project.exceptions.DatabaseException;
import org.project.model.RawMaterial;

import java.util.List;

public class ConsultRawMaterialsStockUI implements Runnable {

    private final RawMaterialController controller;

    public ConsultRawMaterialsStockUI() {
        this.controller = new RawMaterialController();
    }

    public void run() {
        try {
            List<RawMaterial> rawMaterials = controller.getRawMaterials();
            System.out.println("\nRaw Materials:");
            if (rawMaterials.isEmpty()) {
                System.out.println("No raw materials registered.");
            } else {
                for (RawMaterial rawMaterial : rawMaterials) {
                    System.out.println(" - Raw Material ID: " + rawMaterial.getId() + " | Name: " + rawMaterial.getName() + " | Current Stock: " + rawMaterial.getCurrentStock());
                }
            }
        } catch (DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

}
