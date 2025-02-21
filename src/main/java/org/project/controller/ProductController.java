package org.project.controller;

import org.project.domain.Product;
import org.project.domain.ProductCategory;
import org.project.exceptions.ProductException;
import org.project.service.ProductService;

import java.util.List;

public class ProductController {

    private ProductService productService;

    public ProductController() {
        productService = new ProductService();
    }

    public List<ProductCategory> getProductCategories() {
        return productService.getProductCategories();
    }

    public ProductCategory getProductCategory(int id) throws ProductException {
        return productService.getCategoryByID(id);
    }

    public ProductCategory registerCategory(int id, String name) throws ProductException {
        return productService.registerCategory(id, name);
    }

    public Product registerProduct(String productID, String unitName, String unitSymbol, String name, String description, ProductCategory category, int capacity, int size, String color, double price) throws ProductException {
        return productService.registerProduct(productID, unitName, unitSymbol, name, description, category, capacity, size, color, price);
    }
}
