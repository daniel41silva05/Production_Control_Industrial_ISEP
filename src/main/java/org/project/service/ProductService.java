package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.io.CsvReader;
import org.project.model.*;
import org.project.exceptions.ProductException;
import org.project.repository.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductService {

    private DatabaseConnection connection;
    private ProductRepository productRepository;
    private ProductCategoryRepository productCategoryRepository;

    public ProductService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        productRepository = repositories.getProductRepository();
        productCategoryRepository  = repositories.getProductCategoryRepository();
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
        return productRepository.getAllProducts(connection);
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

    public Product registerProduct(String productID, String name, String description, ProductCategory category, int capacity, int size, String color, double price) throws ProductException {

        if (productRepository.getProductExists(connection, productID)) {
            throw new ProductException("Product with ID " + productID + " already exists.");
        }

        Product product = new Product(productID, name, description, category, capacity, size, color, price);
        boolean success = productRepository.saveProduct(connection, product);
        if (!success) {
            return null;
        }

        return product;
    }

    public Product changeProductCategory (String productID, ProductCategory category) throws ProductException {
        if (!productRepository.getProductExists(connection, productID)) {
            throw new ProductException("Product with ID " + productID + " not exists.");
        }

        Product product = productRepository.getProductByID(connection, productID);
        if (product.getCategory().equals(category)) {
            throw new ProductException("Product with ID " + productID + " already belongs to category" + category.getName() + ".");
        }
        product.setCategory(category);

        boolean success = productRepository.updateCategory(connection, product);
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

        List<Product> products = productRepository.getAllProducts(connection);
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

    public List<Component> getComponents() {
        return productRepository.getAllComponents(connection);
    }

    public Component registerComponent(String id, String name, String description) throws ProductException {
        if (productRepository.getComponentExists(connection, id)) {
            throw new ProductException("Component with ID " + id + " already exists.");
        }

        Component component = new Component(id, name, description);
        boolean success = productRepository.saveComponent(connection, component);
        if (!success) {
            return null;
        }
        return component;
    }

    public List<Component> registerComponentsFromCSV(String filePath) {
        List<Component> components = CsvReader.loadComponents(filePath);

        for (Component component : components) {

            if (!productRepository.getComponentExists(connection, component.getId())) {

                boolean success = productRepository.saveComponent(connection, component);
                if (!success) {
                    return null;
                }
            }
        }
        return components;
    }

    public List<RawMaterial> getRawMaterials() { return productRepository.getAllRawMaterials(connection); }

    public RawMaterial registerRawMaterial(String id, String name, String description, int currentStock, int minimumStock) throws ProductException {
        if (productRepository.getRawMaterialExists(connection, id)) {
            throw new ProductException("RawMaterial with ID " + id + " already exists.");
        }

        RawMaterial rawMaterial = new RawMaterial(id, name, description, currentStock, minimumStock);
        boolean success = productRepository.saveRawMaterial(connection, rawMaterial);
        if (!success) {
            return null;
        }
        return rawMaterial;
    }

    public List<RawMaterial> registerRawMaterialsFromCSV(String filePath) {
        List<RawMaterial> rawMaterials = CsvReader.loadRawMaterials(filePath);

        for (RawMaterial rawMaterial : rawMaterials) {

            boolean success;

            if (!productRepository.getRawMaterialExists(connection, rawMaterial.getId())) {

                success = productRepository.saveRawMaterial(connection, rawMaterial);

            } else {
                int currentStock = rawMaterial.getCurrentStock();
                int minimumStock = rawMaterial.getMinimumStock();

                rawMaterial = productRepository.getRawMaterialByID(connection, rawMaterial.getId());
                rawMaterial.setCurrentStock(currentStock);
                rawMaterial.setMinimumStock(minimumStock);

                success = productRepository.updateRawMaterial(connection, rawMaterial);
            }

            if (!success) {
                return null;
            }

        }
        return rawMaterials;
    }

    public RawMaterial changeMinimumRawMaterialStock (String id, int newRawMaterial) throws ProductException {
        if (!productRepository.getRawMaterialExists(connection, id)) {
            throw new ProductException("RawMaterial with ID " + id + " not exists.");
        }

        RawMaterial rawMaterial = productRepository.getRawMaterialByID(connection, id);

        if (rawMaterial.getMinimumStock() == newRawMaterial) {
            throw new ProductException("The minimum stock remains the same");
        }

        rawMaterial.setMinimumStock(newRawMaterial);

        boolean success = productRepository.updateRawMaterial(connection, rawMaterial);
        if (!success) {
            return null;
        }
        return rawMaterial;
    }

    public List<RawMaterial> consultRawMaterialsStockAlert () {
        List<RawMaterial> rawMaterialsStockAlert = new ArrayList<>();

        for (RawMaterial rawMaterial : getRawMaterials()) {
            if (rawMaterial.getMinimumStock() > rawMaterial.getCurrentStock()) {
                rawMaterialsStockAlert.add(rawMaterial);
            }
        }

        return rawMaterialsStockAlert;
    }

}
