package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.model.*;
import org.project.repository.SupplierRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UpdateSupplierStatusTest {

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
}
