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

public class ChangeProductCategoryTest {

    private ProductService productService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(connection, productRepository, null);
    }

    @Test
    void testChangeProductCategory_Success() {
        String productId = "P001";
        ProductCategory oldCategory = new ProductCategory(1, "Electronics");
        ProductCategory newCategory = new ProductCategory(2, "Home Appliances");

        when(productRepository.getProductByID(connection, productId)).thenReturn(product);
        when(product.getCategory()).thenReturn(oldCategory);

        Product result = productService.changeProductCategory(productId, newCategory);

        assertNotNull(result);
        verify(product).setCategory(newCategory);
        verify(productRepository, times(1)).updateCategory(connection, product);
    }

    @Test
    void testChangeProductCategory_NullCategory() {
        String productId = "P002";
        when(productRepository.getProductByID(connection, productId)).thenReturn(product);

        Product result = productService.changeProductCategory(productId, null);
        assertNull(result);
    }

    @Test
    void testChangeProductCategory_AlreadyInCategory() {
        String productId = "P003";
        ProductCategory category = new ProductCategory(1, "Electronics");

        when(productRepository.getProductByID(connection, productId)).thenReturn(product);
        when(product.getCategory()).thenReturn(category);

        assertThrows(ProductException.class, () -> productService.changeProductCategory(productId, category));
    }

}
