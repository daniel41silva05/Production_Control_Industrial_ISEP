package org.project.domain;

public class Product extends Part {

    private ProductCategory category;
    private int capacity;
    private int size;
    private String color;
    private double price;
    // missing tree

    public Product(String id, Unit unit, String name, String description, ProductCategory category, int capacity, int size, String color, double price) {
        super(id, unit, name, description);
        this.category = category;
        this.capacity = capacity;
        this.size = size;
        this.color = color;
        this.price = price;
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
}
