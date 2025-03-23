# US027 - Consult Supplier Status

## 4. Tests 

**Test 1:** Check if the supplier with the inactive status but containing active supply offers has its status changed - AC03.

    @Test
    public void testUpdateSupplierStatus_ActivateSupplier() {
        Supplier supplier = new Supplier(1, "Supplier A", 123456789, "supplierA@example.com", EntityState.INACTIVE);
        SupplyOffer activeOffer = new SupplyOffer(1, new Address(1, "Street 1", "1234-567", "City", "Portugal"), new Date(System.currentTimeMillis() + 10000), new Date(System.currentTimeMillis() + 20000), ProcessState.PENDING);
        supplier.getSupplyOffers().add(activeOffer);

        List<Supplier> suppliers = new ArrayList<>();
        suppliers.add(supplier);

        when(supplierRepository.getAll(connection)).thenReturn(suppliers);

        supplierService.updateSupplierStatus();

        assertEquals(EntityState.ACTIVE, supplier.getState());
        verify(supplierRepository, times(1)).updateStatus(eq(connection), eq(supplier));
    }

**Test 2:** Check if the supplier with the activated status but without active supply offers has its status changed - AC03.

    @Test
    public void testUpdateSupplierStatus_DeactivateSupplier() {
        Supplier supplier = new Supplier(1, "Supplier A", 123456789, "supplierA@example.com", EntityState.ACTIVE);
        SupplyOffer expiredOffer = new SupplyOffer(1, new Address(1, "Street 1", "1234-567", "City", "Portugal"), new Date(System.currentTimeMillis() - 20000), new Date(System.currentTimeMillis() - 10000), ProcessState.PENDING);
        supplier.getSupplyOffers().add(expiredOffer);

        List<Supplier> suppliers = new ArrayList<>();
        suppliers.add(supplier);

        when(supplierRepository.getAll(connection)).thenReturn(suppliers);

        supplierService.updateSupplierStatus();

        assertEquals(EntityState.INACTIVE, supplier.getState());
        verify(supplierRepository, times(1)).updateStatus(eq(connection), eq(supplier));
    }

**Test 3:** Check if the supplier without supply offers has the inactive status.

    @Test
    public void testUpdateSupplierStatus_NoSupplyOffers() {
        Supplier supplier = new Supplier(1, "Supplier A", 123456789, "supplierA@example.com", EntityState.ACTIVE);

        List<Supplier> suppliers = new ArrayList<>();
        suppliers.add(supplier);

        when(supplierRepository.getAll(connection)).thenReturn(suppliers);

        supplierService.updateSupplierStatus();

        assertEquals(EntityState.INACTIVE, supplier.getState());
        verify(supplierRepository, times(1)).updateStatus(eq(connection), eq(supplier));
    }

**Test 4:** Check if the supplier with the active status and containing active supply offers has not had its status changed - AC03.

    @Test
    public void testUpdateSupplierStatus_AlreadyActiveWithActiveOffers() {
        Supplier supplier = new Supplier(1, "Supplier A", 123456789, "supplierA@example.com", EntityState.ACTIVE);
        SupplyOffer activeOffer = new SupplyOffer(1, new Address(1, "Street 1", "1234-567", "City", "Portugal"), new Date(System.currentTimeMillis() + 10000), new Date(System.currentTimeMillis() + 20000), ProcessState.PENDING);
        supplier.getSupplyOffers().add(activeOffer);

        List<Supplier> suppliers = new ArrayList<>();
        suppliers.add(supplier);

        when(supplierRepository.getAll(connection)).thenReturn(suppliers);

        supplierService.updateSupplierStatus();

        assertEquals(EntityState.ACTIVE, supplier.getState());
        verify(supplierRepository, never()).updateStatus(eq(connection), eq(supplier));
    }

**Test 5:** Check if the supplier with inactive status and does not contain active supply offers has not had its status changed - AC03.

    @Test
    public void testUpdateSupplierStatus_AlreadyInactiveWithNoActiveOffers() {
        Supplier supplier = new Supplier(1, "Supplier A", 123456789, "supplierA@example.com", EntityState.INACTIVE);
        SupplyOffer expiredOffer = new SupplyOffer(1, new Address(1, "Street 1", "1234-567", "City", "Portugal"), new Date(System.currentTimeMillis() - 20000), new Date(System.currentTimeMillis() - 10000), ProcessState.PENDING);
        supplier.getSupplyOffers().add(expiredOffer);

        List<Supplier> suppliers = new ArrayList<>();
        suppliers.add(supplier);

        when(supplierRepository.getAll(connection)).thenReturn(suppliers);

        supplierService.updateSupplierStatus();

        assertEquals(EntityState.INACTIVE, supplier.getState());
        verify(supplierRepository, never()).updateStatus(eq(connection), eq(supplier));
    }

## 5. Construction (Implementation)

### Class SupplierService 

```java
public List<Supplier> updateSupplierStatus () {
    List<Supplier> suppliers = getSuppliers();

    for (Supplier supplier : suppliers) {

        boolean containsActiveSupplyOffers = false;
        for (SupplyOffer supplyOffer : supplier.getSupplyOffers()) {
            if (supplyOffer.getEndDate().after(new Date())) {
                containsActiveSupplyOffers = true;
                break;
            }
        }

        if (supplier.getState().equals(EntityState.INACTIVE) && containsActiveSupplyOffers) {
            supplier.setState(EntityState.ACTIVE);
            supplierRepository.updateStatus(connection, supplier);
        } else if (supplier.getState().equals(EntityState.ACTIVE) && !containsActiveSupplyOffers) {
            supplier.setState(EntityState.INACTIVE);
            supplierRepository.updateStatus(connection, supplier);
        }

    }

    return suppliers;
}
```
```java
public List<Supplier> getSuppliers() {
    return supplierRepository.getAll(connection);
}
```

## 6. Observations

n/a