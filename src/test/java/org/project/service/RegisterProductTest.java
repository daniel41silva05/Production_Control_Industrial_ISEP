package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ProductException;
import org.project.model.Product;
import org.project.model.ProductCategory;
import org.project.repository.ProductRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterProductTest {

    private ProductService productService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(connection, productRepository, null);
    }

    @Test
    void testRegisterProduct_Success() {
        String productId = "P001";
        String name = "Test Product";
        String description = "Description";
        int categoryId = 1;
        int weight = 10;
        int height = 20;
        String color = "Red";
        int price = 100;
        ProductCategory category = new ProductCategory(categoryId, "Electronics");

        Product result = productService.registerProduct(productId, name, description, category, weight, height, color, price);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals(name, result.getName());
        verify(productRepository, times(1)).saveProduct(connection, result);
    }

    @Test
    void testRegisterProduct_AlreadyExists() {
        String productId = "P001";
        when(productRepository.getProductExists(connection, productId)).thenReturn(true);

        assertThrows(ProductException.class, () -> {
            productService.registerProduct(productId, "Test", "Desc", new ProductCategory(1, "Electronics"), 10, 20, "Red", 100);
        });

    }

    @Test
    void testRegisterProduct_CategoryNotFound() {
        String productId = "P002";
        ProductCategory category = null;

        assertNull(productService.registerProduct(productId, "Test", "Desc", category, 10, 20, "Blue", 150));
    }
}
