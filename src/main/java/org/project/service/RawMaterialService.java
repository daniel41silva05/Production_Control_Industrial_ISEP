package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ProductException;
import org.project.io.CsvReader;
import org.project.model.RawMaterial;
import org.project.repository.RawMaterialRepository;
import org.project.repository.Repositories;

import java.util.ArrayList;
import java.util.List;

public class RawMaterialService {

    private DatabaseConnection connection;
    private RawMaterialRepository rawMaterialRepository;

    public RawMaterialService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        rawMaterialRepository = repositories.getRawMaterialRepository();
    }

    public List<RawMaterial> getRawMaterials() { return rawMaterialRepository.getAllRawMaterials(connection); }

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
        if (!rawMaterialRepository.getRawMaterialExists(connection, id)) {
            throw new ProductException("RawMaterial with ID " + id + " not exists.");
        }

        RawMaterial rawMaterial = rawMaterialRepository.getRawMaterialByID(connection, id);

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

}
