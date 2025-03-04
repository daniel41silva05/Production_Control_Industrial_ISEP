package org.project.controller;

import org.project.exceptions.ProductException;
import org.project.exceptions.SupplierException;
import org.project.model.RawMaterial;
import org.project.service.RawMaterialService;

import java.util.List;

public class RawMaterialController {

    private RawMaterialService rawMaterialService;

    public RawMaterialController() {
        rawMaterialService = new RawMaterialService();
    }

    public List<RawMaterial> getRawMaterials() {
        return rawMaterialService.getRawMaterials();
    }

    public RawMaterial getRawMaterialByID (String id) throws ProductException {
        return rawMaterialService.getRawMaterialByID(id);
    }

    public RawMaterial registerRawMaterial(String id, String name, String description, int currentStock, int minimumStock) throws ProductException {
        return rawMaterialService.registerRawMaterial(id, name, description, currentStock, minimumStock);
    }

    public List<RawMaterial> registerRawMaterialsFromCSV(String filePath) {
        return rawMaterialService.registerRawMaterialsFromCSV(filePath);
    }

    public RawMaterial changeMinimumRawMaterialStock (String id, int newRawMaterial) throws ProductException {
        return rawMaterialService.changeMinimumRawMaterialStock(id, newRawMaterial);
    }

    public List<RawMaterial> consultRawMaterialsStockAlert () {
        return rawMaterialService.consultRawMaterialsStockAlert();
    }

    public RawMaterial registerRawMaterialSupplier(String rawMaterialID, int supplierID, double unitCost) throws ProductException, SupplierException {
        return rawMaterialService.registerRawMaterialSupplier(rawMaterialID, supplierID, unitCost);
    }

    public RawMaterial deleteRawMaterialSupplier(String rawMaterialID, int supplierID) throws ProductException, SupplierException {
        return rawMaterialService.deleteRawMaterialSupplier(rawMaterialID, supplierID);
    }

    public RawMaterial changeUnitCostRawMaterialSupplier(String rawMaterialID, int supplierID, double unitCost) throws ProductException, SupplierException {
        return rawMaterialService.changeUnitCostRawMaterialSupplier(rawMaterialID, supplierID, unitCost);
    }

}
