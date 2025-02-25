package org.project.model;

import org.project.common.NaryTree;

public class Product extends Part {

    private ProductCategory category;
    private int capacity;
    private int size;
    private String color;
    private double price;
    private NaryTree<ProductionElement> productionTree;

    public Product(String id, String name, String description, ProductCategory category, int capacity, int size, String color, double price, NaryTree<ProductionElement> productionTree) {
        super(id, name, description);
        this.category = category;
        this.capacity = capacity;
        this.size = size;
        this.color = color;
        this.price = price;
        this.productionTree = productionTree;
    }

    public Product(String id, String name, String description, ProductCategory category, int capacity, int size, String color, double price) {
        super(id, name, description);
        this.category = category;
        this.capacity = capacity;
        this.size = size;
        this.color = color;
        this.price = price;
        this.productionTree = null;
    }

    public Product(String id, String name, String description) {
        super(id, name, description);
        this.category = null;
        this.capacity = 0;
        this.size = 0;
        this.color = null;
        this.price = 0;
        this.productionTree = null;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public NaryTree<ProductionElement> getProductionTree() {
        return productionTree;
    }

    public void setProductionTree(NaryTree<ProductionElement> tree) {
        this.productionTree = tree;
    }
}
