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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConsultSuppliersRawMaterialTest {

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

    @Test
    void testGetSuppliersByCost_EmptyList() {
        RawMaterial rawMaterial = mock(RawMaterial.class);
        when(rawMaterial.getRawMaterialCost()).thenReturn(new HashMap<>());
        when(rawMaterialRepository.getRawMaterialByID(connection, "RM2")).thenReturn(rawMaterial);

        List<Map.Entry<Supplier, Double>> result = rawMaterialService.getSuppliersByCost("RM2");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetSuppliersByCost_NullRawMaterial() {
        when(rawMaterialRepository.getRawMaterialByID(connection, "RM3")).thenReturn(null);

        assertThrows(ProductException.class, () -> rawMaterialService.getSuppliersByCost("RM3"));
    }
}