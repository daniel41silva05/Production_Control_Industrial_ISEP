package org.project.domain;

import org.project.common.NaryTree;

public class Product extends Part {

    private ProductCategory category;
    private int capacity;
    private int size;
    private String color;
    private double price;
    private NaryTree<Product> tree;

    public Product(String id, String name, String description, ProductCategory category, int capacity, int size, String color, double price) {
        super(id, name, description);
        this.category = category;
        this.capacity = capacity;
        this.size = size;
        this.color = color;
        this.price = price;
        this.tree = new NaryTree<>(this);
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

    public NaryTree<Product> getTree() {
        return tree;
    }

    public void setTree(NaryTree<Product> tree) {
        this.tree = tree;
    }
}
