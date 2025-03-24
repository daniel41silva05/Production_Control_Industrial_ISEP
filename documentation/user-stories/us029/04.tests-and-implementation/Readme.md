# US029 - Consult prices of Suppliers for a Raw Material

## 4. Tests 

**Test 1:** Checks that the list of suppliers is returned correctly, sorted by the cost associated with the raw material.

    @Test
    void testGetSuppliersByCost_Success() {
        RawMaterial rawMaterial = mock(RawMaterial.class);
        Supplier supplier1 = mock(Supplier.class);
        Supplier supplier2 = mock(Supplier.class);
        Supplier supplier3 = mock(Supplier.class);

        when(supplier1.getId()).thenReturn(1);
        when(supplier2.getId()).thenReturn(2);
        when(supplier3.getId()).thenReturn(3);
        
        Map<Supplier, Double> costMap = new HashMap<>();
        costMap.put(supplier1, 30.0);
        costMap.put(supplier2, 20.0);
        costMap.put(supplier3, 25.0);

        when(rawMaterial.getRawMaterialCost()).thenReturn(costMap);
        when(rawMaterialRepository.getRawMaterialByID(connection, "RM1")).thenReturn(rawMaterial);
        
        List<Map.Entry<Supplier, Double>> result = rawMaterialService.getSuppliersByCost("RM1");
        
        assertEquals(3, result.size());
        assertEquals(supplier2, result.get(0).getKey());
        assertEquals(20.0, result.get(0).getValue());
        assertEquals(supplier3, result.get(1).getKey());
        assertEquals(25.0, result.get(1).getValue());
        assertEquals(supplier1, result.get(2).getKey());
        assertEquals(30.0, result.get(2).getValue());
    }

**Test 2:** Checks if an empty list is returned when the raw material has no associated suppliers.

    @Test
    void testGetSuppliersByCost_EmptyList() {
        RawMaterial rawMaterial = mock(RawMaterial.class);
        when(rawMaterial.getRawMaterialCost()).thenReturn(new HashMap<>());
        when(rawMaterialRepository.getRawMaterialByID(connection, "RM2")).thenReturn(rawMaterial);
        
        List<Map.Entry<Supplier, Double>> result = rawMaterialService.getSuppliersByCost("RM2");
        
        assertTrue(result.isEmpty());
    }

**Test 3:** Checks if query is not possible when raw material is not found.

    @Test
    void testGetSuppliersByCost_NullRawMaterial() {
        when(rawMaterialRepository.getRawMaterialByID(connection, "RM3")).thenReturn(null);
        
        assertThrows(ProductException.class, () -> rawMaterialService.getSuppliersByCost("RM3"));
    }

## 5. Construction (Implementation)

### Class RawMaterialService 

```java
public List<Map.Entry<Supplier, Double>> getSuppliersByCost(String rawMaterialID) {
    RawMaterial rawMaterial = getRawMaterialByID(rawMaterialID);

    List<Map.Entry<Supplier, Double>> sortedList = new ArrayList<>(rawMaterial.getRawMaterialCost().entrySet());
    sortedList.sort(Comparator.comparingDouble(Map.Entry::getValue));
    return sortedList;
}
```
```java
public RawMaterial getRawMaterialByID (String id) {
        RawMaterial rawMaterial = rawMaterialRepository.getRawMaterialByID(connection, id);

        if (rawMaterial == null) {
            throw ProductException.rawMaterialNotFound(id);
        }

        return rawMaterial;
    }
```

## 6. Observations

n/a