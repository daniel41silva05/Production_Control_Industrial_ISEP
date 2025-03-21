# US013 - Register a Component

## 4. Tests 

**Test 1:** Check if the component is being registered correctly, being stored in the repository.

    @Test
    void testRegisterComponent_Success() {
        String componentId = "C001";
        String name = "Test Component";
        String description = "Description of Test Component";

        when(componentRepository.getComponentExists(connection, componentId)).thenReturn(false);

        Component result = componentService.registerComponent(componentId, name, description);

        assertNotNull(result);
        assertEquals(componentId, result.getId());
        assertEquals(name, result.getName());
        verify(componentRepository, times(1)).saveComponent(connection, result);
    }

**Test 2:** Check that it is not possible to register a component that already exists - AC03.

    @Test
    void testRegisterComponent_AlreadyExists() {
        String componentId = "C001";
        when(componentRepository.getComponentExists(connection, componentId)).thenReturn(true);

        assertThrows(ProductException.class, () -> {
            componentService.registerComponent(componentId, "Test", "Description");
        });
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