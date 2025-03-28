package org.project.controller;

import org.project.model.Product;
import org.project.model.ProductCategory;
import org.project.service.ProductService;

import java.util.List;
import java.util.Map;

public class ProductController {

    private ProductService productService;

    public ProductController() {
        productService = new ProductService();
    }

    public List<Product> getProducts() {
        return productService.getProducts();
    }

    public List<ProductCategory> getProductCategories() {
        return productService.getProductCategories();
    }

    public ProductCategory getProductCategory(int id) {
        return productService.getCategoryByID(id);
    }

    public ProductCategory registerCategory(int id, String name) {
        return productService.registerCategory(id, name);
    }

    public Product registerProduct(String productID, String name, String description, ProductCategory category, int capacity, int size, String color, double price) {
        return productService.registerProduct(productID, name, description, category, capacity, size, color, price);
    }

    public Product changeProductCategory (String productID, ProductCategory category) {
        return productService.changeProductCategory(productID, category);
    }

    public List<Product> productListInCategory(int categoryID) {
        return productService.productListInCategory(categoryID);
    }

    public ProductCategory deleteCategory(int id, Map<Product,Integer> productNewCategory) {
        return productService.deleteCategory(id, productNewCategory);
    }

}