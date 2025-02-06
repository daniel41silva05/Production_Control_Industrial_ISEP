package org.project.domain;

import java.util.Map;

public class RawMaterial extends Part {

    private int currentStock;
    private int minimumStock;
    private Map<Supplier, Double> rawMaterialCost;

    public RawMaterial(int id, Unit unit, String name, String description, int currentStock, int minimumStock, Map<Supplier, Double> rawMaterialCost) {
        super(id, unit, name, description);
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.rawMaterialCost = rawMaterialCost;
    }

    public RawMaterial(int id, Unit unit, String name, String description, int currentStock, Map<Supplier, Double> rawMaterialCost) {
        super(id, unit, name, description);
        this.currentStock = currentStock;
        this.minimumStock = 0;
        this.rawMaterialCost = rawMaterialCost;
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
