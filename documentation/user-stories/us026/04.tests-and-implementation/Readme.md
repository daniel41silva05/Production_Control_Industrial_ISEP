# US026 - Delete a Supplier

## 4. Tests 

**Test 1:** Check if the supplier is being deleted correctly and removed from the repository - AC02.

    @Test
    public void testDeleteSupplier_Success() {
        int supplierID = 1;
        Supplier supplier = new Supplier(supplierID, "ABC Ltd.", 123456789, "abc@example.com");

        when(supplierRepository.getById(connection, supplierID)).thenReturn(supplier);

        Supplier deletedSupplier = supplierService.deleteSupplier(supplierID);

        assertNotNull(deletedSupplier);
        assertEquals(supplierID, deletedSupplier.getId());

        verify(supplierRepository, times(1)).delete(connection, supplier);
    }

**Test 2:** Check that it's not possible to delete a supplier that doesn't exist - AC01.

    @Test
    public void testDeleteSupplier_SupplierNotFound() {
        int supplierID = 1;

        when(supplierRepository.getById(connection, supplierID)).thenReturn(null);

        assertThrows(SupplierException.class, () -> {
            supplierService.deleteSupplier(supplierID);
        });
    }

## 5. Construction (Implementation)

### Class SupplierService 

```java
public Supplier deleteSupplier (int id) {
    Supplier supplier = getSupplierByID(id);

    supplierRepository.delete(connection, supplier);

    return supplier;
}
```
```java
public Supplier getSupplierByID (int id) {
    Supplier supplier = supplierRepository.getById(connection, id);

    if (supplier == null) {
        throw SupplierException.supplierNotFound(id);
    }

    return supplier;
}
```

## 6. Observations

n/a