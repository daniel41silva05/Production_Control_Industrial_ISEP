package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.model.*;
import org.project.exceptions.ProductException;
import org.project.repository.*;

import java.util.ArrayList;
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

    public ProductService(DatabaseConnection connection, ProductRepository productRepository, ProductCategoryRepository productCategoryRepository) {
        this.connection = connection;
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    public List<ProductCategory> getProductCategories() {
        return productCategoryRepository.getAll(connection);
    }

    public ProductCategory getCategoryByID(int id) {
        ProductCategory category = productCategoryRepository.getByID(connection, id);

        if (category == null) {
            throw ProductException.categoryNotFound(id);
        }

        return category;
    }

    public List<Product> getProducts() {
        return productRepository.getAllProducts(connection);
    }

    public Product getProductByID(String id) {
        Product product = productRepository.getProductByID(connection, id);

        if (product == null) {
            throw ProductException.productNotFound(id);
        }

        return product;
    }

    public ProductCategory registerCategory(int id, String name) {
        if (productCategoryRepository.getCategoryExists(connection, id)) {
            throw ProductException.categoryAlreadyExists(id);
        }

        ProductCategory productCategory = new ProductCategory(id, name);

        productCategoryRepository.save(connection, productCategory);

        return productCategory;
    }

    public Product registerProduct(String productID, String name, String description, ProductCategory category, int capacity, int size, String color, double price) {
        if (productRepository.getProductExists(connection, productID)) {
            throw ProductException.productAlreadyExists(productID);
        }

        if (category == null) {
            return null;
        }

        Product product = new Product(productID, name, description, category, capacity, size, color, price);

        productRepository.saveProduct(connection, product);

        return product;
    }

    public Product changeProductCategory (String productID, ProductCategory category) {
        Product product = getProductByID(productID);

        if (category == null) {
            return null;
        }

        if (product.getCategory().equals(category)) {
            throw ProductException.productAlreadyInCategory(productID, category.getName());
        }

        product.setCategory(category);

        productRepository.updateCategory(connection, product);

        return product;
    }

    public List<Product> productListInCategory(int categoryID) {
        ProductCategory productCategory = getCategoryByID(categoryID);

        List<Product> products = productRepository.getAllProducts(connection);
        List<Product> filteredProducts = new ArrayList<>();

        for (Product product : products) {
            if (product.getCategory().equals(productCategory)) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }

    public ProductCategory deleteCategory(int id, Map<Product,Integer> productNewCategory){
        for (Map.Entry<Product, Integer> entry : productNewCategory.entrySet()) {
            Product product = entry.getKey();
            int categoryID = entry.getValue();

            if (categoryID == id) {
                throw ProductException.categoryDeletionMessage(categoryID);
            }

            ProductCategory productCategory = getCategoryByID(categoryID);

            product.setCategory(productCategory);

            productRepository.updateCategory(connection, product);
        }

        ProductCategory productCategory = getCategoryByID(id);

        productCategoryRepository.delete(connection, productCategory);

        return productCategory;
    }

}
