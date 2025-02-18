package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.domain.Product;
import org.project.domain.ProductCategory;
import org.project.domain.Unit;
import org.project.exceptions.ProductException;
import org.project.repository.*;

import java.util.List;

public class RegisterProductService {

    private DatabaseConnection connection;
    private ProductRepository productRepository;
    private ProductCategoryRepository productCategoryRepository;
    private UnitRepository unitRepository;

    public RegisterProductService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        productRepository = repositories.getProductRepository();
        productCategoryRepository  = repositories.getProductCategoryRepository();
        unitRepository = repositories.getUnitRepository();
    }

    public List<ProductCategory> getProductCategories() {
        return productCategoryRepository.getAll(connection);
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

    public Product registerProduct(String productID, String unitName, String unitSymbol, String name, String description, int categoryID, int capacity, int size, String color, double price) throws ProductException {

        if (productRepository.getProductExists(connection, productID)) {
            throw new ProductException("Product with ID " + productID + " already exists.");
        }

        if (!productCategoryRepository.getCategoryExists(connection, categoryID)) {
            throw new ProductException("Product Category with ID " + categoryID + " not exists.");
        }

        ProductCategory category = productCategoryRepository.getByID(connection, categoryID);
        if (category == null) {
            return null;
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
