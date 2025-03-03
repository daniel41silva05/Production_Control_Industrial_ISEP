package org.project.controller;

import org.project.exceptions.SupplierException;
import org.project.model.EntityState;
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

    public Supplier getSupplierByID (int id) throws SupplierException {
        return supplierService.getSupplierByID(id);
    }

    public Supplier registerSupplier(int supplierID, String name, int phoneNumber, String email, EntityState state) throws SupplierException {
        return supplierService.registerSupplier(supplierID, name, phoneNumber, email, state);
    }

    public Supplier deleteSupplier (int id) throws SupplierException {
        return supplierService.deleteSupplier(id);
    }

    public Supplier updateSupplier (Supplier supplier, int phoneNumber, String email) {
        return supplierService.updateSupplier(supplier, phoneNumber, email);
    }

    public List<Supplier> updateSupplierStatus () throws SupplierException {
        return supplierService.updateSupplierStatus();
    }

}
