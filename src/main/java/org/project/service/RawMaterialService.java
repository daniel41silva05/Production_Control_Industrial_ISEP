package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ProductException;
import org.project.exceptions.SupplierException;
import org.project.io.CsvReader;
import org.project.model.RawMaterial;
import org.project.model.Supplier;
import org.project.repository.RawMaterialRepository;
import org.project.repository.Repositories;
import org.project.repository.SupplierRepository;

import java.util.ArrayList;
import java.util.List;

public class RawMaterialService {

    private DatabaseConnection connection;
    private RawMaterialRepository rawMaterialRepository;
    private SupplierRepository supplierRepository;

    public RawMaterialService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        rawMaterialRepository = repositories.getRawMaterialRepository();
        supplierRepository  = repositories.getSupplierRepository();
    }

    public List<RawMaterial> getRawMaterials() { return rawMaterialRepository.getAllRawMaterials(connection); }

    public RawMaterial getRawMaterialByID (String id) throws ProductException {
        if (!rawMaterialRepository.getRawMaterialExists(connection, id)) {
            throw new ProductException("RawMaterial with ID " + id + " not exists.");
        }

        return rawMaterialRepository.getRawMaterialByID(connection, id);
    }

    public RawMaterial registerRawMaterial(String id, String name, String description, int currentStock, int minimumStock) throws ProductException {
        if (rawMaterialRepository.getRawMaterialExists(connection, id)) {
            throw new ProductException("RawMaterial with ID " + id + " already exists.");
        }

        RawMaterial rawMaterial = new RawMaterial(id, name, description, currentStock, minimumStock);
        boolean success = rawMaterialRepository.saveRawMaterial(connection, rawMaterial);
        if (!success) {
            return null;
        }
        return rawMaterial;
    }

    public List<RawMaterial> registerRawMaterialsFromCSV(String filePath) {
        List<RawMaterial> rawMaterials = CsvReader.loadRawMaterials(filePath);

        for (RawMaterial rawMaterial : rawMaterials) {

            boolean success;

            if (!rawMaterialRepository.getRawMaterialExists(connection, rawMaterial.getId())) {

                success = rawMaterialRepository.saveRawMaterial(connection, rawMaterial);

            } else {
                int currentStock = rawMaterial.getCurrentStock();
                int minimumStock = rawMaterial.getMinimumStock();

                rawMaterial = rawMaterialRepository.getRawMaterialByID(connection, rawMaterial.getId());
                rawMaterial.setCurrentStock(currentStock);
                rawMaterial.setMinimumStock(minimumStock);

                success = rawMaterialRepository.updateRawMaterial(connection, rawMaterial);
            }

            if (!success) {
                return null;
            }

        }
        return rawMaterials;
    }

    public RawMaterial changeMinimumRawMaterialStock (String id, int newRawMaterial) throws ProductException {

        RawMaterial rawMaterial = getRawMaterialByID(id);

        if (rawMaterial.getMinimumStock() == newRawMaterial) {
            throw new ProductException("The minimum stock remains the same");
        }

        rawMaterial.setMinimumStock(newRawMaterial);

        boolean success = rawMaterialRepository.updateRawMaterial(connection, rawMaterial);
        if (!success) {
            return null;
        }
        return rawMaterial;
    }

    public List<RawMaterial> consultRawMaterialsStockAlert () {
        List<RawMaterial> rawMaterialsStockAlert = new ArrayList<>();

        for (RawMaterial rawMaterial : getRawMaterials()) {
            if (rawMaterial.getMinimumStock() > rawMaterial.getCurrentStock()) {
                rawMaterialsStockAlert.add(rawMaterial);
            }
        }

        return rawMaterialsStockAlert;
    }

    private Supplier getSupplierByID (int id) throws SupplierException {
        if (!supplierRepository.getSupplierExists(connection, id)) {
            throw new SupplierException("Supplier with ID " + id + " not exists.");
        }

        return supplierRepository.getById(connection, id);
    }

    public RawMaterial registerRawMaterialSupplier(String rawMaterialID, int supplierID, double unitCost) throws ProductException, SupplierException {
        RawMaterial rawMaterial = getRawMaterialByID(rawMaterialID);
        Supplier supplier = getSupplierByID(supplierID);

        if (rawMaterial.getRawMaterialCost().containsKey(supplier)) {
            throw new ProductException("Supplier with ID " + supplierID + " has already been registered as a supplier of raw material with ID " + rawMaterialID);
        }

        rawMaterial.getRawMaterialCost().put(supplier, unitCost);

        boolean success = rawMaterialRepository.saveRawMaterialSupplier(connection, supplier, rawMaterial, unitCost);
        if (!success) {
            return null;
        }

        return rawMaterial;
    }

    public RawMaterial deleteRawMaterialSupplier(String rawMaterialID, int supplierID) throws ProductException, SupplierException {
        RawMaterial rawMaterial = getRawMaterialByID(rawMaterialID);
        Supplier supplier = getSupplierByID(supplierID);

        if (!rawMaterial.getRawMaterialCost().containsKey(supplier)) {
            throw new ProductException("Supplier with ID " + supplierID + " was never registered as a supplier of raw material with ID " + rawMaterialID);
        }

        rawMaterial.getRawMaterialCost().remove(supplier);

        boolean success = rawMaterialRepository.deleteRawMaterialSupplier(connection, supplier, rawMaterial);
        if (!success) {
            return null;
        }

        return rawMaterial;
    }

    public RawMaterial changeUnitCostRawMaterialSupplier(String rawMaterialID, int supplierID, double unitCost) throws ProductException, SupplierException {
        RawMaterial rawMaterial = getRawMaterialByID(rawMaterialID);
        Supplier supplier = getSupplierByID(supplierID);

        if (!rawMaterial.getRawMaterialCost().containsKey(supplier)) {
            throw new ProductException("Supplier with ID " + supplierID + " was never registered as a supplier of raw material with ID " + rawMaterialID);
        }

        if (rawMaterial.getRawMaterialCost().get(supplier).equals(unitCost)) {
            throw new ProductException("The expected unit cost remains the same");
        }

        rawMaterial.getRawMaterialCost().put(supplier, unitCost);

        boolean success = rawMaterialRepository.updateRawMaterialSupplier(connection, supplier, rawMaterial, unitCost);
        if (!success) {
            return null;
        }

        return rawMaterial;
    }

}
