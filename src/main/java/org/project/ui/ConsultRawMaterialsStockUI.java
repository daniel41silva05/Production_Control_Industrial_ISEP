package org.project.ui;

import org.project.controller.ProductController;
import org.project.model.RawMaterial;

import java.util.List;

public class ConsultRawMaterialsStockUI implements Runnable {

    private final ProductController controller;

    public ConsultRawMaterialsStockUI() {
        this.controller = new ProductController();
    }

    public void run() {
        List<RawMaterial> rawMaterials = controller.getRawMaterials();
        System.out.println("\nRaw Materials:");
        if (rawMaterials.isEmpty()) {
            System.out.println("No raw materials registered.");
        } else {
            for (RawMaterial rawMaterial : rawMaterials) {
                System.out.println(" - Raw Material ID: " + rawMaterial.getId() + " | Name: " + rawMaterial.getName() + " | Current Stock: " + rawMaterial.getCurrentStock());
            }
        }
    }

}
