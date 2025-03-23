package org.project.controller;

import org.project.model.Supplier;
import org.project.service.SupplierService;

import java.util.List;

public class SupplierController {

    private SupplierService supplierService;

    public SupplierController() {
        supplierService = new SupplierService();
    }

    public List<Supplier> getSuppliers() {
        return supplierService.getSuppliers();
    }

    public Supplier getSupplierByID (int id) {
        return supplierService.getSupplierByID(id);
    }

    public Supplier registerSupplier(int supplierID, String name, int phoneNumber, String email) {
        return supplierService.registerSupplier(supplierID, name, phoneNumber, email);
    }

    public Supplier deleteSupplier (int id) {
        return supplierService.deleteSupplier(id);
    }

    public Supplier updateSupplier (Supplier supplier, int phoneNumber, String email) {
        return supplierService.updateSupplier(supplier, phoneNumber, email);
    }

    public List<Supplier> updateSupplierStatus () {
        return supplierService.updateSupplierStatus();
    }

}
