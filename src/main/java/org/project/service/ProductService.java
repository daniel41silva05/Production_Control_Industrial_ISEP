package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.domain.Product;
import org.project.domain.ProductCategory;
import org.project.domain.Unit;
import org.project.exceptions.ProductException;
import org.project.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public List<Product> productListInCategory(int categoryID) throws ProductException {
        if (!productCategoryRepository.getCategoryExists(connection, categoryID)) {
            throw new ProductException("Product Category with ID " + categoryID + " not exists.");
        }
        ProductCategory productCategory = productCategoryRepository.getByID(connection, categoryID);

        List<Product> products = productRepository.getAll(connection);
        List<Product> filteredProducts = new ArrayList<>();

        for (Product product : products) {
            if (product.getCategory().equals(productCategory)) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }

    public ProductCategory deleteCategory(int id, Map<Product,Integer> productNewCategory) throws ProductException {
        for (Map.Entry<Product, Integer> entry : productNewCategory.entrySet()) {
            Product product = entry.getKey();
            int categoryID = entry.getValue();

            if (!productCategoryRepository.getCategoryExists(connection, categoryID)) {
                throw new ProductException("Product Category with ID " + categoryID + " not exists.");
            } else if (categoryID == id) {
                throw new ProductException("Product Category with ID " + categoryID + " this is what will be deleted.");
            }

            ProductCategory productCategory = productCategoryRepository.getByID(connection, categoryID);
            product.setCategory(productCategory);

            boolean success = productRepository.updateCategory(connection, product);
            if (!success) {
                return null;
            }
        }

        ProductCategory productCategory = productCategoryRepository.getByID(connection, id);

        boolean success = productCategoryRepository.delete(connection, productCategory);
        if (!success) {
            return null;
        }

        return productCategory;
    }

}
