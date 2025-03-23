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

public class DeleteSupplierTest {

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
    public void testDeleteSupplier_Success() {
        int supplierID = 1;
        Supplier supplier = new Supplier(supplierID, "ABC Ltd.", 123456789, "abc@example.com");

        when(supplierRepository.getById(connection, supplierID)).thenReturn(supplier);

        Supplier deletedSupplier = supplierService.deleteSupplier(supplierID);

        assertNotNull(deletedSupplier);
        assertEquals(supplierID, deletedSupplier.getId());

        verify(supplierRepository, times(1)).delete(connection, supplier);
    }

    @Test
    public void testDeleteSupplier_SupplierNotFound() {
        int supplierID = 1;

        when(supplierRepository.getById(connection, supplierID)).thenReturn(null);

        assertThrows(SupplierException.class, () -> {
            supplierService.deleteSupplier(supplierID);
        });
    }
}
