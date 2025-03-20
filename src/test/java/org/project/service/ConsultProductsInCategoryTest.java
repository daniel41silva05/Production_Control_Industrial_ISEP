package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ProductException;
import org.project.model.Product;
import org.project.model.ProductCategory;
import org.project.repository.ProductCategoryRepository;
import org.project.repository.ProductRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConsultProductsInCategoryTest {

    private ProductService productService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(connection, productRepository, productCategoryRepository);
    }

    @Test
    void testProductListInCategory_Success() {
        int categoryId = 1;
        ProductCategory category = new ProductCategory(categoryId, "Electronics");
        ProductCategory category2 = new ProductCategory(2, "Apple");
        Product product1 = new Product("P001", "Laptop", "High-end laptop", category, 2, 3, "Black", 1500);
        Product product2 = new Product("P002", "Smartphone", "Latest model", category, 1, 2, "Silver", 800);
        Product product3 = new Product("P003", "Iphone", "13", category2, 2, 2, "Blue", 1000);

        when(productCategoryRepository.getByID(connection, categoryId)).thenReturn(category);
        when(productRepository.getAllProducts(connection)).thenReturn(Arrays.asList(product1, product2, product3));

        List<Product> result = productService.productListInCategory(categoryId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(product1));
        assertTrue(result.contains(product2));
    }

    @Test
    void testProductListInCategory_ProductsNotInCategory() {
        int categoryId = 1;
        ProductCategory category = new ProductCategory(categoryId, "Electronics");
        ProductCategory otherCategory = new ProductCategory(2, "Furniture");
        Product product1 = new Product("P003", "Chair", "Office chair", otherCategory, 5, 10, "Blue", 200);
        Product product2 = new Product("P004", "Table", "Wooden table", otherCategory, 15, 20, "Brown", 500);

        when(productCategoryRepository.getByID(connection, categoryId)).thenReturn(category);
        when(productRepository.getAllProducts(connection)).thenReturn(Arrays.asList(product1, product2));

        List<Product> result = productService.productListInCategory(categoryId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProductListInCategory_EmptyList() {
        int categoryId = 2;
        ProductCategory category = new ProductCategory(categoryId, "Books");

        when(productCategoryRepository.getByID(connection, categoryId)).thenReturn(category);
        when(productRepository.getAllProducts(connection)).thenReturn(Collections.emptyList());

        List<Product> result = productService.productListInCategory(categoryId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProductListInCategory_CategoryNotFound() {
        int categoryId = 3;
        when(productCategoryRepository.getByID(connection, categoryId)).thenReturn(null);

        assertThrows(ProductException.class, () -> productService.productListInCategory(categoryId));
    }

}
