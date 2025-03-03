package org.project.controller;

import org.project.model.Component;
import org.project.model.Product;
import org.project.model.ProductCategory;
import org.project.exceptions.ProductException;
import org.project.model.RawMaterial;
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

    public ProductCategory getProductCategory(int id) throws ProductException {
        return productService.getCategoryByID(id);
    }

    public ProductCategory registerCategory(int id, String name) throws ProductException {
        return productService.registerCategory(id, name);
    }

    public Product registerProduct(String productID, String name, String description, ProductCategory category, int capacity, int size, String color, double price) throws ProductException {
        return productService.registerProduct(productID, name, description, category, capacity, size, color, price);
    }

    public Product changeProductCategory (String productID, ProductCategory category) throws ProductException {
        return productService.changeProductCategory(productID, category);
    }

    public List<Product> productListInCategory(int categoryID) throws ProductException {
        return productService.productListInCategory(categoryID);
    }

    public ProductCategory deleteCategory(int id, Map<Product,Integer> productNewCategory) throws ProductException {
        return productService.deleteCategory(id, productNewCategory);
    }

    public List<Component> getComponents() {
        return productService.getComponents();
    }

    public List<RawMaterial> getRawMaterials() {
        return productService.getRawMaterials();
    }

    public Component registerComponent(String id, String name, String description) throws ProductException {
        return productService.registerComponent(id, name, description);
    }

    public RawMaterial registerRawMaterial(String id, String name, String description, int currentStock, int minimumStock) throws ProductException {
        return productService.registerRawMaterial(id, name, description, currentStock, minimumStock);
    }

    public List<Component> registerComponentsFromCSV(String filePath) {
        return productService.registerComponentsFromCSV(filePath);
    }

    public List<RawMaterial> registerRawMaterialsFromCSV(String filePath) {
        return productService.registerRawMaterialsFromCSV(filePath);
    }

}