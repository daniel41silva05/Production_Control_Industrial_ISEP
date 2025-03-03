package org.project.ui;

import org.project.controller.ProductController;
import org.project.exceptions.ProductException;
import org.project.model.RawMaterial;
import org.project.ui.utils.Utils;

import java.util.List;

public class ChangeMinimumRawMaterialStockUI implements Runnable {

    private final ProductController controller;

    public ChangeMinimumRawMaterialStockUI() {
        this.controller = new ProductController();
    }

    public void run() {
        try {
            showRawMaterials(controller.getRawMaterials());

            boolean update = Utils.confirm("Do you want to update a minimum stock?");
            if (!update) {
                return;
            }

            String id = Utils.readLineFromConsole("Enter Raw Material ID: ");
            int minimumStock = Utils.readIntegerFromConsole("Enter New Minimum Stock: ");

            RawMaterial rawMaterial = controller.changeMinimumRawMaterialStock(id, minimumStock);
            if (rawMaterial == null) {
                System.out.println("\nRaw Material update failed.");
            } else {
                System.out.println("\nRaw Material updated successfully.");
                showRawMaterial(rawMaterial);
            }

        } catch (ProductException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nRaw Material updated failed.");
        }
    }

    private void showRawMaterials(List<RawMaterial> rawMaterials) {
        System.out.println("\nRaw Materials:");
        if (rawMaterials.isEmpty()) {
            System.out.println("No raw materials registered.");
        } else {
            for (RawMaterial rawMaterial : rawMaterials) {
                System.out.println(" - Raw Material ID: " + rawMaterial.getId() + " | Name: " + rawMaterial.getName() + " | Current Stock: " + rawMaterial.getCurrentStock() + " | Minimum Stock: " + rawMaterial.getMinimumStock());
            }
        }
    }

    private void showRawMaterial(RawMaterial rawMaterial) {
        System.out.println(" - Raw Material ID: " + rawMaterial.getId());
        System.out.println(" - Name: " + rawMaterial.getName());
        System.out.println(" - Description: " + rawMaterial.getDescription());
        System.out.println(" - Current Stock: " + rawMaterial.getCurrentStock());
        System.out.println(" - Minimum Stock: " + rawMaterial.getMinimumStock());
    }
}
