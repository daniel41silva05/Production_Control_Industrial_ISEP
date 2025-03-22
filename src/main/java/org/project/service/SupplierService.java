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

    public SupplierService(DatabaseConnection connection, SupplierRepository supplierRepository) {
        this.connection = connection;
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getSuppliers() {
        return supplierRepository.getAll(connection);
    }

    public Supplier getSupplierByID (int id) {
        Supplier supplier = supplierRepository.getById(connection, id);

        if (supplier == null) {
            throw SupplierException.supplierNotFound(id);
        }

        return supplier;
    }

    public Supplier registerSupplier(int supplierID, String name, int phoneNumber, String email, EntityState state) {
        if (supplierRepository.getSupplierExists(connection, supplierID)) {
            throw SupplierException.supplierAlreadyExists(supplierID);
        }

        Supplier supplier = new Supplier(supplierID, name, phoneNumber, email, state);
        supplierRepository.save(connection, supplier);

        return supplier;
    }

    public Supplier deleteSupplier (int id) {
        Supplier supplier = getSupplierByID(id);

        supplierRepository.delete(connection, supplier);

        return supplier;
    }

    public Supplier updateSupplier (Supplier supplier, int phoneNumber, String email) {
        supplier.setPhoneNumber(phoneNumber);
        supplier.setEmail(email);

        supplierRepository.update(connection, supplier);

        return supplier;
    }

    public List<Supplier> updateSupplierStatus () {
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
                supplierRepository.updateStatus(connection, supplier);
            } else if (supplier.getState().equals(EntityState.ACTIVE) && !containsActiveSupplyOffers) {
                supplier.setState(EntityState.INACTIVE);
                supplierRepository.updateStatus(connection, supplier);
            }

        }

        return suppliers;
    }

}
