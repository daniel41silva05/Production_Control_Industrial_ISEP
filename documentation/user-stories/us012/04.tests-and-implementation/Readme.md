# US012 - Consult Products by Category

## 4. Tests 

**Test 1:** Retrieve products correctly filtered by category.

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

**Test 2:** Ensure products from different categories are not included.

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

**Test 3:** Handle scenario where no products exist in the category.

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

**Test 4:** Check that it is not possible to consult with category does not exist - AC01.

    @Test
    void testProductListInCategory_CategoryNotFound() {
        int categoryId = 3;
        when(productCategoryRepository.getByID(connection, categoryId)).thenReturn(null);

        assertThrows(ProductException.class, () -> productService.productListInCategory(categoryId));
    }

## 5. Construction (Implementation)

### Class ProductService 

```java
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
```
```java
public ProductCategory getCategoryByID(int id) {
        ProductCategory category = productCategoryRepository.getByID(connection, id);

        if (category == null) {
            throw ProductException.categoryNotFound(id);
        }

        return category;
    }
```

## 6. Observations

n/a