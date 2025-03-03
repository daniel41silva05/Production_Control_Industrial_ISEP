package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.exceptions.SupplierException;
import org.project.model.*;
import org.project.repository.Repositories;
import org.project.repository.SupplierRepository;

import java.util.Date;
import java.util.List;

public class SupplierService {

    private DatabaseConnection connection;
    private SupplierRepository supplierRepository;

    public SupplierService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        supplierRepository = repositories.getSupplierRepository();
    }

    public List<Supplier> getSuppliers() {
        return supplierRepository.getAll(connection);
    }

    public Supplier getSupplierByID (int id) throws SupplierException {
        if (!supplierRepository.getSupplierExists(connection, id)) {
            throw new SupplierException("Supplier with ID " + id + " not exists.");
        }

        return supplierRepository.getById(connection, id);
    }

    public Supplier registerSupplier(int supplierID, String name, int phoneNumber, String email, EntityState state) throws SupplierException {
        if (supplierRepository.getSupplierExists(connection, supplierID)) {
            throw new SupplierException("Supplier with ID " + supplierID + " already exists.");
        }

        Supplier supplier = new Supplier(supplierID, name, phoneNumber, email, state);
        boolean success = supplierRepository.save(connection, supplier);
        if (!success) {
            return null;
        }
        return supplier;
    }

    public Supplier deleteSupplier (int id) throws SupplierException {

        Supplier supplier = getSupplierByID(id);

        boolean success = supplierRepository.delete(connection, supplier);
        if (!success) {
            return null;
        }

        return supplier;
    }

    public Supplier updateSupplier (Supplier supplier, int phoneNumber, String email) {
        supplier.setPhoneNumber(phoneNumber);
        supplier.setEmail(email);

        boolean success = supplierRepository.update(connection, supplier);
        if (!success) {
            return null;
        }

        return supplier;
    }

    public List<Supplier> updateSupplierStatus () throws SupplierException {
        List<Supplier> suppliers = getSuppliers();

        for (Supplier supplier : suppliers) {

            boolean containsActiveSupplyOffers = false;
            for (SupplyOffer supplyOffer : supplier.getSupplyOffers()) {
                if (supplyOffer.getEndDate().after(new Date())) {
                    containsActiveSupplyOffers = true;
                    break;
                }
            }

            if (supplier.getState().equals(EntityState.INACTIVE) && containsActiveSupplyOffers) {
                supplier.setState(EntityState.ACTIVE);
                boolean success = supplierRepository.updateStatus(connection, supplier);
                if (!success) {
                    throw new SupplierException("Problems updating supplier status.");
                }
            } else if (supplier.getState().equals(EntityState.ACTIVE) && !containsActiveSupplyOffers) {
                supplier.setState(EntityState.INACTIVE);
                boolean success = supplierRepository.updateStatus(connection, supplier);
                if (!success) {
                    throw new SupplierException("Problems updating supplier status.");
                }
            }

        }

        return suppliers;
    }

}
