package org.project.ui;

import org.project.controller.RawMaterialController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.ProductException;
import org.project.model.RawMaterial;
import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RegisterRawMaterialUI implements Runnable {

    private final RawMaterialController controller;

    public RegisterRawMaterialUI() {
        this.controller = new RawMaterialController();
    }

    public void run() {
        try {
            showRawMaterials(controller.getRawMaterials());

            List<String> optionRawMaterial = new ArrayList<>();
            optionRawMaterial.add("Register a Raw Material.");
            optionRawMaterial.add("Register Raw Materials from a CSV file.");
            int option = Utils.showAndSelectIndex(optionRawMaterial, "\nRaw Material:");

            if (option == 0) {
                String id = Utils.readLineFromConsole("Enter Raw Material ID: ");
                String name = Utils.readLineFromConsole("Enter Name: ");
                String description = Utils.readLineFromConsole("Enter Description: ");
                int currentStock = Utils.readIntegerFromConsole("Enter Current Stock: ");
                int minimumStock = Utils.readIntegerFromConsole("Enter Minimum Stock: ");
                RawMaterial rawMaterial = controller.registerRawMaterial(id, name, description, currentStock, minimumStock);
                if (rawMaterial == null) {
                    System.out.println("\nRaw Material registration failed.");
                } else {
                    System.out.println("\nRaw Material registered successfully.");
                    showRawMaterial(rawMaterial);
                }

            } else if (option == 1) {
                String fileRawMaterials = Utils.readLineFromConsole("Enter file name of raw materials (ex. raw_materials.csv): ");
                List<RawMaterial> rawMaterials = controller.registerRawMaterialsFromCSV("files\\" + fileRawMaterials);
                if (rawMaterials == null) {
                    System.out.println("\nUpload of raw materials failed.");
                } else {
                    showRawMaterials(rawMaterials);
                }
            }

        } catch (ProductException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
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
