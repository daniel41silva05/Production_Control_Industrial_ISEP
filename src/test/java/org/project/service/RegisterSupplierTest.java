package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.SupplierException;
import org.project.model.Supplier;
import org.project.repository.SupplierRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterSupplierTest {

    private SupplierService supplierService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private SupplierRepository supplierRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        supplierService = new SupplierService(connection, supplierRepository);
    }

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

    @Test
    public void testRegisterSupplier_SupplierAlreadyExists() {
        int supplierID = 1;
        when(supplierRepository.getSupplierExists(connection, supplierID)).thenReturn(true);

        assertThrows(SupplierException.class, () -> {
            supplierService.registerSupplier(supplierID, "Supplier One", 123456789, "supplier@example.com");
        });
    }

    @Test
    public void testRegisterSupplier_InvalidPhoneNumber() {
        int supplierID = 1;
        int invalidPhoneNumber = 123;

        when(supplierRepository.getSupplierExists(connection, supplierID)).thenReturn(false);

        assertThrows(SupplierException.class, () -> {
            supplierService.registerSupplier(supplierID, "Supplier One", invalidPhoneNumber, "supplier@example.com");
        });
    }

    @Test
    public void testRegisterSupplier_InvalidEmail() {
        int supplierID = 1;
        String invalidEmail = "invalid-email";

        when(supplierRepository.getSupplierExists(connection, supplierID)).thenReturn(false);

        assertThrows(SupplierException.class, () -> {
            supplierService.registerSupplier(supplierID, "Supplier One", 123456789, invalidEmail);
        });
    }
}
