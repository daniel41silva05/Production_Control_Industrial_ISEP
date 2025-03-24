package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ProductException;
import org.project.model.RawMaterial;
import org.project.model.Supplier;
import org.project.repository.RawMaterialRepository;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterSupplierRawMaterialTest {

    private RawMaterialService rawMaterialService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rawMaterialService = new RawMaterialService(connection, rawMaterialRepository);
    }

    @Test
    void testRegisterRawMaterialSupplier_Success() {
        RawMaterial rawMaterial = mock(RawMaterial.class);
        Supplier supplier = mock(Supplier.class);
        double unitCost = 10.5;

        when(rawMaterial.getRawMaterialCost()).thenReturn(new HashMap<>());

        RawMaterial result = rawMaterialService.registerRawMaterialSupplier(rawMaterial, supplier, unitCost);

        assertNotNull(result);
        assertEquals(unitCost, result.getRawMaterialCost().get(supplier));
        verify(rawMaterialRepository, times(1)).saveRawMaterialSupplier(connection, supplier, rawMaterial, unitCost);
    }

    @Test
    void testRegisterRawMaterialSupplier_AlreadyRegistered() {
        RawMaterial rawMaterial = mock(RawMaterial.class);
        Supplier supplier = mock(Supplier.class);
        double unitCost = 10.5;
        Map<Supplier, Double> costMap = new HashMap<>();
        costMap.put(supplier, unitCost);

        when(rawMaterial.getRawMaterialCost()).thenReturn(costMap);

        assertThrows(ProductException.class, () -> {
            rawMaterialService.registerRawMaterialSupplier(rawMaterial, supplier, unitCost);
        });
    }

    @Test
    void testRegisterRawMaterialSupplier_NullRawMaterial() {
        Supplier supplier = mock(Supplier.class);
        double unitCost = 10.5;

        RawMaterial result = rawMaterialService.registerRawMaterialSupplier(null, supplier, unitCost);
        assertNull(result);

        verifyNoInteractions(rawMaterialRepository);
    }

    @Test
    void testRegisterRawMaterialSupplier_NullSupplier() {
        RawMaterial rawMaterial = mock(RawMaterial.class);
        double unitCost = 10.5;

        RawMaterial result = rawMaterialService.registerRawMaterialSupplier(rawMaterial, null, unitCost);
        assertNull(result);

        verifyNoInteractions(rawMaterialRepository);
    }
}
