package org.project.model;

import java.util.HashMap;
import java.util.Map;

public class RawMaterial extends Part {

    private int currentStock;
    private int minimumStock;
    private Map<Supplier, Double> rawMaterialCost;

    public RawMaterial(String id, String name, String description, int currentStock, int minimumStock, Map<Supplier, Double> rawMaterialCost) {
        super(id, name, description);
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.rawMaterialCost = rawMaterialCost;
    }

    public RawMaterial(String id, String name, String description, int currentStock, int minimumStock) {
        super(id, name, description);
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.rawMaterialCost = new HashMap<>();
    }

    public RawMaterial(String id, String name, String description) {
        super(id, name, description);
        this.currentStock = 0;
        this.minimumStock = 0;
        this.rawMaterialCost = new HashMap<>();
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(int minimumStock) {
        this.minimumStock = minimumStock;
    }

    public Map<Supplier, Double> getRawMaterialCost() {
        return rawMaterialCost;
    }

    public void setRawMaterialCost(Map<Supplier, Double> rawMaterialCost) {
        this.rawMaterialCost = rawMaterialCost;
    }
}
