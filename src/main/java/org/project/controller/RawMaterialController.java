package org.project.controller;

import org.project.model.RawMaterial;
import org.project.model.Supplier;
import org.project.service.RawMaterialService;
import org.project.service.SupplierService;

import java.util.List;
import java.util.Map;

public class RawMaterialController {

    private RawMaterialService rawMaterialService;
    private SupplierService supplierService;

    public RawMaterialController() {
        rawMaterialService = new RawMaterialService();
        supplierService = new SupplierService();
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

    public RawMaterial registerRawMaterialSupplier(RawMaterial rawMaterial, int supplierID, double unitCost) {
        Supplier supplier = supplierService.getSupplierByID(supplierID);
        return rawMaterialService.registerRawMaterialSupplier(rawMaterial, supplier, unitCost);
    }

    public RawMaterial deleteRawMaterialSupplier(RawMaterial rawMaterial, int supplierID) {
        Supplier supplier = supplierService.getSupplierByID(supplierID);
        return rawMaterialService.deleteRawMaterialSupplier(rawMaterial, supplier);
    }

    public RawMaterial changeUnitCostRawMaterialSupplier(RawMaterial rawMaterial, int supplierID, double unitCost) {
        Supplier supplier = supplierService.getSupplierByID(supplierID);
        return rawMaterialService.changeUnitCostRawMaterialSupplier(rawMaterial, supplier, unitCost);
    }

    public List<Map.Entry<Supplier, Double>> getSuppliersByCost(RawMaterial rawMaterial) {
        return rawMaterialService.getSuppliersByCost(rawMaterial);
    }

}
