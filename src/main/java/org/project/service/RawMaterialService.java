package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ProductException;
import org.project.io.CsvReader;
import org.project.model.RawMaterial;
import org.project.model.Supplier;
import org.project.repository.RawMaterialRepository;
import org.project.repository.Repositories;
import org.project.repository.SupplierRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

    public RawMaterialService(DatabaseConnection connection, RawMaterialRepository rawMaterialRepository, SupplierRepository supplierRepository) {
        this.connection = connection;
        this.rawMaterialRepository = rawMaterialRepository;
        this.supplierRepository = supplierRepository;
    }

    public List<RawMaterial> getRawMaterials() { return rawMaterialRepository.getAllRawMaterials(connection); }

    public RawMaterial getRawMaterialByID (String id) {
        RawMaterial rawMaterial = rawMaterialRepository.getRawMaterialByID(connection, id);

        if (rawMaterial == null) {
            throw ProductException.rawMaterialNotFound(id);
        }

        return rawMaterial;
    }

    public RawMaterial registerRawMaterial(String id, String name, String description, int currentStock, int minimumStock) {
        if (rawMaterialRepository.getRawMaterialExists(connection, id)) {
            throw ProductException.rawMaterialAlreadyExists(id);
        }

        RawMaterial rawMaterial = new RawMaterial(id, name, description, currentStock, minimumStock);
        rawMaterialRepository.saveRawMaterial(connection, rawMaterial);

        return rawMaterial;
    }

    public List<RawMaterial> registerRawMaterialsFromCSV(String filePath) {
        List<RawMaterial> rawMaterials = CsvReader.loadRawMaterials(filePath);

        for (RawMaterial rawMaterial : rawMaterials) {

            if (!rawMaterialRepository.getRawMaterialExists(connection, rawMaterial.getId())) {

                rawMaterialRepository.saveRawMaterial(connection, rawMaterial);

            } else {
                int currentStock = rawMaterial.getCurrentStock();
                int minimumStock = rawMaterial.getMinimumStock();

                rawMaterial = rawMaterialRepository.getRawMaterialByID(connection, rawMaterial.getId());
                rawMaterial.setCurrentStock(currentStock);
                rawMaterial.setMinimumStock(minimumStock);

                rawMaterialRepository.updateRawMaterial(connection, rawMaterial);
            }

        }
        return rawMaterials;
    }

    public RawMaterial changeMinimumRawMaterialStock (String id, int newRawMaterial) {

        RawMaterial rawMaterial = getRawMaterialByID(id);

        if (rawMaterial.getMinimumStock() == newRawMaterial) {
            throw ProductException.minimumStockRemainsSame();
        }

        rawMaterial.setMinimumStock(newRawMaterial);

        rawMaterialRepository.updateRawMaterial(connection, rawMaterial);

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

    public RawMaterial registerRawMaterialSupplier(RawMaterial rawMaterial, Supplier supplier, double unitCost) {
        if (rawMaterial == null || supplier == null) {
            return null;
        }

        if (rawMaterial.getRawMaterialCost().containsKey(supplier)) {
            throw ProductException.supplierAlreadyRegistered(supplier.getId(), rawMaterial.getId());
        }

        rawMaterial.getRawMaterialCost().put(supplier, unitCost);

        rawMaterialRepository.saveRawMaterialSupplier(connection, supplier, rawMaterial, unitCost);

        return rawMaterial;
    }

    public RawMaterial deleteRawMaterialSupplier(RawMaterial rawMaterial, Supplier supplier) {
        if (rawMaterial == null || supplier == null) {
            return null;
        }

        if (!rawMaterial.getRawMaterialCost().containsKey(supplier)) {
            throw ProductException.supplierNotRegistered(supplier.getId(), rawMaterial.getId());
        }

        rawMaterial.getRawMaterialCost().remove(supplier);

        rawMaterialRepository.deleteRawMaterialSupplier(connection, supplier, rawMaterial);

        return rawMaterial;
    }

    public RawMaterial changeUnitCostRawMaterialSupplier(RawMaterial rawMaterial, Supplier supplier, double unitCost) {
        if (rawMaterial == null || supplier == null) {
            return null;
        }

        if (!rawMaterial.getRawMaterialCost().containsKey(supplier)) {
            throw ProductException.supplierNotRegistered(supplier.getId(), rawMaterial.getId());
        }

        if (rawMaterial.getRawMaterialCost().get(supplier).equals(unitCost)) {
            throw ProductException.expectedUnitCostRemainsSame();
        }

        rawMaterial.getRawMaterialCost().put(supplier, unitCost);

        rawMaterialRepository.updateRawMaterialSupplier(connection, supplier, rawMaterial, unitCost);

        return rawMaterial;
    }

    public List<Map.Entry<Supplier, Double>> getSuppliersByCost(RawMaterial rawMaterial) {
        List<Map.Entry<Supplier, Double>> sortedList = new ArrayList<>(rawMaterial.getRawMaterialCost().entrySet());
        sortedList.sort(Comparator.comparingDouble(Map.Entry::getValue));
        return sortedList;
    }

}
