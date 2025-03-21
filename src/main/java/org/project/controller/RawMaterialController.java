package org.project.controller;

import org.project.exceptions.ProductException;
import org.project.exceptions.SupplierException;
import org.project.model.RawMaterial;
import org.project.model.Supplier;
import org.project.service.RawMaterialService;

import java.util.List;
import java.util.Map;

public class RawMaterialController {

    private RawMaterialService rawMaterialService;

    public RawMaterialController() {
        rawMaterialService = new RawMaterialService();
    }

    public List<RawMaterial> getRawMaterials() {
        return rawMaterialService.getRawMaterials();
    }

    public RawMaterial getRawMaterialByID (String id) {
        return rawMaterialService.getRawMaterialByID(id);
    }

    public RawMaterial registerRawMaterial(String id, String name, String description, int currentStock, int minimumStock) {
        return rawMaterialService.registerRawMaterial(id, name, description, currentStock, minimumStock);
    }

    public List<RawMaterial> registerRawMaterialsFromCSV(String filePath) {
        return rawMaterialService.registerRawMaterialsFromCSV(filePath);
    }

    public RawMaterial changeMinimumRawMaterialStock (String id, int newRawMaterial) {
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

    public List<Map.Entry<Supplier, Double>> getSuppliersByCost(RawMaterial rawMaterial) {
        return rawMaterialService.getSuppliersByCost(rawMaterial);
    }

}
