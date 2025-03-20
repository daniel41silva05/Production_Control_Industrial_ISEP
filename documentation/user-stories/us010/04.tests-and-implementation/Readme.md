# US010 - Register a Product

## 4. Tests 

**Test 1:** Check if the product is being registered correctly, being stored in the repository.

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

**Test 2:** Check that it is not possible to register a product that already exists - AC03.

    @Test
    void testRegisterProduct_AlreadyExists() {
        String productId = "P001";
        when(productRepository.getProductExists(connection, productId)).thenReturn(true);

        assertThrows(ProductException.class, () -> {
            productService.registerProduct(productId, "Test", "Desc", new ProductCategory(1, "Electronics"), 10, 20, "Red", 100);
        });

    }

**Test 3:** Check that it is not possible to register a product without a category - AC04.

    @Test
    void testRegisterProduct_CategoryNotFound() {
        String productId = "P002";
        ProductCategory category = null;

        assertNull(productService.registerProduct(productId, "Test", "Desc", category, 10, 20, "Blue", 150));
    }

## 5. Construction (Implementation)

### Class ProductService 

```java
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
```

## 6. Observations

n/a