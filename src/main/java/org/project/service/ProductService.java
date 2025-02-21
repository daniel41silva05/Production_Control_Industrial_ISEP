package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.domain.Product;
import org.project.domain.ProductCategory;
import org.project.domain.Unit;
import org.project.exceptions.ProductException;
import org.project.repository.*;

import java.util.List;

public class ProductService {

    private DatabaseConnection connection;
    private ProductRepository productRepository;
    private ProductCategoryRepository productCategoryRepository;
    private UnitRepository unitRepository;

    public ProductService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        productRepository = repositories.getProductRepository();
        productCategoryRepository  = repositories.getProductCategoryRepository();
        unitRepository = repositories.getUnitRepository();
    }

    public List<ProductCategory> getProductCategories() {
        return productCategoryRepository.getAll(connection);
    }

    public ProductCategory getCategoryByID(int id) throws ProductException {
        if (!productCategoryRepository.getCategoryExists(connection, id)) {
            throw new ProductException("Product Category with ID " + id + " not exists.");
        }

        return productCategoryRepository.getByID(connection, id);
    }

    public List<Product> getProducts() {
        return productRepository.getAll(connection);
    }

    public ProductCategory registerCategory(int id, String name) throws ProductException {
        if (productCategoryRepository.getCategoryExists(connection, id)) {
            throw new ProductException("Product Category with ID " + id + " already exists.");
        }

        ProductCategory productCategory = new ProductCategory(id, name);
        boolean success = productCategoryRepository.save(connection, productCategory);
        if (!success) {
            return null;
        }
        return productCategory;
    }

    public Product registerProduct(String productID, String unitName, String unitSymbol, String name, String description, ProductCategory category, int capacity, int size, String color, double price) throws ProductException {

        if (productRepository.getProductExists(connection, productID)) {
            throw new ProductException("Product with ID " + productID + " already exists.");
        }

        Unit unit = unitRepository.findUnit(connection, unitName, unitSymbol);
        if (unit == null) {
            int id = unitRepository.getUnitCount(connection);
            unit = new Unit(id, unitName, unitSymbol);
            unitRepository.save(connection, unit);
        }

        Product product = new Product(productID, unit, name, description, category, capacity, size, color, price);
        boolean success = productRepository.save(connection, product);
        if (!success) {
            return null;
        }

        return product;
    }

}
