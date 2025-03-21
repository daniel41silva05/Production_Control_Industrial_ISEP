package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ProductException;
import org.project.model.RawMaterial;
import org.project.repository.RawMaterialRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConsultRawMaterialsStockAlertTest {

    private RawMaterialService rawMaterialService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rawMaterialService = new RawMaterialService(connection, rawMaterialRepository, null);
    }

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

    @Test
    void testConsultRawMaterialsStockAlert_NoAlerts() {
        RawMaterial rawMaterial1 = new RawMaterial("RM004", "Glass", "Tempered glass", 100, 50);
        RawMaterial rawMaterial2 = new RawMaterial("RM005", "Rubber", "High-quality rubber", 80, 30);

        when(rawMaterialRepository.getAllRawMaterials(connection)).thenReturn(Arrays.asList(rawMaterial1, rawMaterial2));

        List<RawMaterial> result = rawMaterialService.consultRawMaterialsStockAlert();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConsultRawMaterialsStockAlert_EmptyList() {
        when(rawMaterialRepository.getAllRawMaterials(connection)).thenReturn(Collections.emptyList());

        List<RawMaterial> result = rawMaterialService.consultRawMaterialsStockAlert();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
