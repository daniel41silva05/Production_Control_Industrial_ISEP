# US025 - Register a Supplier

## 4. Tests 

**Test 1:** Check if the supplier is being registered correctly, being stored in the repository.

    @Test
    public void testRegisterSupplier_Success() {
        int supplierID = 1;
        String name = "Supplier One";
        int phoneNumber = 123456789;
        String email = "supplier@example.com";

        when(supplierRepository.getSupplierExists(connection, supplierID)).thenReturn(false);

        Supplier supplier = supplierService.registerSupplier(supplierID, name, phoneNumber, email);

        assertNotNull(supplier);
        assertEquals(supplierID, supplier.getId());
        assertEquals(name, supplier.getName());
        assertEquals(phoneNumber, supplier.getPhoneNumber());
        assertEquals(email, supplier.getEmail());

        verify(supplierRepository, times(1)).save(eq(connection), any(Supplier.class));
    }

**Test 2:** Check that it is not possible to register a supplier that already exists - AC03.

    @Test
    public void testRegisterSupplier_SupplierAlreadyExists() {
        int supplierID = 1;
        when(supplierRepository.getSupplierExists(connection, supplierID)).thenReturn(true);

        assertThrows(SupplierException.class, () -> {
            supplierService.registerSupplier(supplierID, "Supplier One", 123456789, "supplier@example.com");
        });
    }

**Test 3:** Check that it is not possible to register a supplier with an invalid phone number - AC04.

    @Test
    public void testRegisterSupplier_InvalidPhoneNumber() {
        int supplierID = 1;
        int invalidPhoneNumber = 123;

        when(supplierRepository.getSupplierExists(connection, supplierID)).thenReturn(false);

        assertThrows(SupplierException.class, () -> {
            supplierService.registerSupplier(supplierID, "Supplier One", invalidPhoneNumber, "supplier@example.com");
        });
    }

**Test 4:** Check that it is not possible to register a supplier with an invalid email format - AC05.

    @Test
    public void testRegisterSupplier_InvalidEmail() {
        int supplierID = 1;
        String invalidEmail = "invalid-email";

        when(supplierRepository.getSupplierExists(connection, supplierID)).thenReturn(false);

        assertThrows(SupplierException.class, () -> {
            supplierService.registerSupplier(supplierID, "Supplier One", 123456789, invalidEmail);
        });
    }

## 5. Construction (Implementation)

### Class SupplierService 

```java
public Supplier registerSupplier(int supplierID, String name, int phoneNumber, String email) {
    if (!Validator.isValidPhoneNumber(phoneNumber)) {
        throw SupplierException.invalidPhoneNumber();
    }

    if (!Validator.isValidEmail(email)) {
        throw SupplierException.invalidEmailFormat();
    }

    if (supplierRepository.getSupplierExists(connection, supplierID)) {
        throw SupplierException.supplierAlreadyExists(supplierID);
    }

    Supplier supplier = new Supplier(supplierID, name, phoneNumber, email);
    supplierRepository.save(connection, supplier);

    return supplier;
}
```

## 6. Observations

n/a