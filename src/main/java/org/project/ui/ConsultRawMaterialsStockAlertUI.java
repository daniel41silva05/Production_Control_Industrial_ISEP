package org.project.ui;

import org.project.controller.ProductController;
import org.project.model.RawMaterial;

import java.util.List;

public class ConsultRawMaterialsStockAlertUI implements Runnable {

    private final ProductController controller;

    public ConsultRawMaterialsStockAlertUI() {
        this.controller = new ProductController();
    }

    public void run() {
        List<RawMaterial> rawMaterials = controller.consultRawMaterialsStockAlert();
        System.out.println("\nRaw Materials in Stock Alert:");
        if (rawMaterials.isEmpty()) {
            System.out.println("No raw materials.");
        } else {
            for (RawMaterial rawMaterial : rawMaterials) {
                System.out.println(" - Raw Material ID: " + rawMaterial.getId() + " | Name: " + rawMaterial.getName() + " | Current Stock: " + rawMaterial.getCurrentStock() + " | Minimum Stock: " + rawMaterial.getMinimumStock());
            }
        }
    }

}
