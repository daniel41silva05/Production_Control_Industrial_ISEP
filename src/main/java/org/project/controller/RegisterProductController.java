package org.project.controller;

import org.project.domain.Product;
import org.project.domain.ProductCategory;
import org.project.exceptions.ProductException;
import org.project.service.RegisterProductService;

import java.util.List;

public class RegisterProductController {

    private RegisterProductService registerProductService;

    public RegisterProductController() {
        registerProductService = new RegisterProductService();
    }

    public List<ProductCategory> getProductCategories() {
        return registerProductService.getProductCategories();
    }

    public ProductCategory registerCategory(int id, String name) throws ProductException {
        return registerProductService.registerCategory(id, name);
    }

    public Product registerProduct(String productID, String unitName, String unitSymbol, String name, String description, int categoryID, int capacity, int size, String color, double price) throws ProductException {
        return registerProductService.registerProduct(productID, unitName, unitSymbol, name, description, categoryID, capacity, size, color, price);
    }
}
