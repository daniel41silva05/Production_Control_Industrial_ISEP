# US015 - View Low Stock Raw Materials

## 4. Tests 

**Test 1:** Retrieve raw materials correctly filtered by low stock.

    @Test
    void testConsultRawMaterialsStockAlert_WithAlerts() {
        RawMaterial rawMaterial1 = new RawMaterial("RM001", "Wood", "High-quality wood", 30, 50);
        RawMaterial rawMaterial2 = new RawMaterial("RM002", "Metal", "Stainless steel", 60, 40);
        RawMaterial rawMaterial3 = new RawMaterial("RM003", "Plastic", "Durable plastic", 20, 25);

        when(rawMaterialRepository.getAllRawMaterials(connection)).thenReturn(Arrays.asList(rawMaterial1, rawMaterial2, rawMaterial3));

        List<RawMaterial> result = rawMaterialService.consultRawMaterialsStockAlert();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(rawMaterial1));
        assertTrue(result.contains(rawMaterial3));
        assertFalse(result.contains(rawMaterial2));
    }

**Test 2:** Ensure raw materials that are not in stock are not included.

    @Test
    void testConsultRawMaterialsStockAlert_NoAlerts() {
        RawMaterial rawMaterial1 = new RawMaterial("RM004", "Glass", "Tempered glass", 100, 50);
        RawMaterial rawMaterial2 = new RawMaterial("RM005", "Rubber", "High-quality rubber", 80, 30);

        when(rawMaterialRepository.getAllRawMaterials(connection)).thenReturn(Arrays.asList(rawMaterial1, rawMaterial2));

        List<RawMaterial> result = rawMaterialService.consultRawMaterialsStockAlert();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

**Test 3:** Handle scenario where no raw materials exist in the system.

    @Test
    void testConsultRawMaterialsStockAlert_EmptyList() {
        when(rawMaterialRepository.getAllRawMaterials(connection)).thenReturn(Collections.emptyList());

        List<RawMaterial> result = rawMaterialService.consultRawMaterialsStockAlert();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

## 5. Construction (Implementation)

### Class RawMaterialService 

```java
public List<RawMaterial> consultRawMaterialsStockAlert () {
    List<RawMaterial> rawMaterialsStockAlert = new ArrayList<>();

    for (RawMaterial rawMaterial : getRawMaterials()) {
        if (rawMaterial.getMinimumStock() > rawMaterial.getCurrentStock()) {
            rawMaterialsStockAlert.add(rawMaterial);
        }
    }

    return rawMaterialsStockAlert;
}
```
```java
public List<RawMaterial> getRawMaterials() {
    return rawMaterialRepository.getAllRawMaterials(connection);
}
```

## 6. Observations

n/a