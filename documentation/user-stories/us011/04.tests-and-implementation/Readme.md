# US011 - Change Product Category

## 4. Tests 

**Test 1:** Check if the product category is being changed correctly, being stored in the repository.

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

**Test 2:** Check that it is not possible to changed a product without a category - AC02.

    @Test
    void testChangeProductCategory_NullCategory() {
        String productId = "P002";
        when(productRepository.getProductByID(connection, productId)).thenReturn(product);
        
        Product result = productService.changeProductCategory(productId, null);
        assertNull(result);
    }

**Test 3:** Check if it is not possible to update category of a product with the same category - AC03.

    @Test
    void testChangeProductCategory_AlreadyInCategory() {
        String productId = "P003";
        ProductCategory category = new ProductCategory(1, "Electronics");

        when(productRepository.getProductByID(connection, productId)).thenReturn(product);
        when(product.getCategory()).thenReturn(category);

        assertThrows(ProductException.class, () -> productService.changeProductCategory(productId, category));
    }

## 5. Construction (Implementation)

### Class ProductService 

```java
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
```
```java
public Product getProductByID(String id) {
    Product product = productRepository.getProductByID(connection, id);

    if (product == null) {
        throw ProductException.productNotFound(id);
    }

    return product;
}
```

## 6. Observations

n/a