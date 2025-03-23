package org.project.ui;

import org.project.controller.RawMaterialController;
import org.project.controller.SupplierController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.ProductException;
import org.project.exceptions.SupplierException;
import org.project.model.RawMaterial;
import org.project.model.Supplier;
import org.project.ui.utils.Utils;

import java.util.List;
import java.util.Map;

public class DeleteSupplierRawMaterialUI implements Runnable {

    private final RawMaterialController rawMaterialController;

    public DeleteSupplierRawMaterialUI() {
        this.rawMaterialController = new RawMaterialController();
    }

    public void run() {
        try {
            showRawMaterials(rawMaterialController.getRawMaterials());

            String rawMaterialID = Utils.readLineFromConsole("Enter Raw Material ID: ");
            RawMaterial rawMaterial = rawMaterialController.getRawMaterialByID(rawMaterialID);

            showSuppliersRawMaterial(rawMaterial);

            boolean confirm = Utils.confirm("Do you want to delete a supplier to the raw material?");
            if (!confirm) {
                return;
            }

            int supplierID = Utils.readIntegerFromConsole("Enter Supplier ID: ");

            rawMaterial = rawMaterialController.deleteRawMaterialSupplier(rawMaterial, supplierID);
            if (rawMaterial == null) {
                System.out.println("\nDelete failed.");
            } else {
                System.out.println("\nDelete successfully.");
                showSuppliersRawMaterial(rawMaterial);
            }

        } catch (SupplierException | ProductException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
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
