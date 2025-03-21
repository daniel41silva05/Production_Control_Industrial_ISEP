# US014 - Register a Raw Material

## 4. Tests 

**Test 1:** Check if the raw material is being registered correctly, being stored in the repository.

    @Test
    void testRegisterRawMaterial_Success() {
        String rawMaterialId = "RM001";
        String name = "Test Raw Material";
        String description = "Description of Test Raw Material";
        int currentStock = 100;
        int minimumStock = 50;

        when(rawMaterialRepository.getRawMaterialExists(connection, rawMaterialId)).thenReturn(false);

        RawMaterial result = rawMaterialService.registerRawMaterial(rawMaterialId, name, description, currentStock, minimumStock);

        assertNotNull(result);
        assertEquals(rawMaterialId, result.getId());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(currentStock, result.getCurrentStock());
        assertEquals(minimumStock, result.getMinimumStock());
        verify(rawMaterialRepository, times(1)).saveRawMaterial(connection, result);
    }

**Test 2:** Check that it is not possible to register a raw material that already exists - AC03.

    @Test
    void testRegisterRawMaterial_AlreadyExists() {
        String rawMaterialId = "RM001";
        when(rawMaterialRepository.getRawMaterialExists(connection, rawMaterialId)).thenReturn(true);

        assertThrows(ProductException.class, () -> {
            rawMaterialService.registerRawMaterial(rawMaterialId, "Test Raw Material", "Description", 100, 50);
        });
    }

## 5. Construction (Implementation)

### Class RawMaterialService 

```java
    public RawMaterial registerRawMaterial(String id, String name, String description, int currentStock, int minimumStock) {
    if (rawMaterialRepository.getRawMaterialExists(connection, id)) {
        throw ProductException.rawMaterialAlreadyExists(id);
    }

    RawMaterial rawMaterial = new RawMaterial(id, name, description, currentStock, minimumStock);
    rawMaterialRepository.saveRawMaterial(connection, rawMaterial);

    return rawMaterial;
}
```

## 6. Observations

n/a