package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ProductException;
import org.project.model.RawMaterial;
import org.project.repository.RawMaterialRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterRawMaterialTest {

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
    void testRegisterRawMaterial_Success() {
        String rawMaterialId = "RM001";
        String name = "Test Raw Material";
        String description = "Description of Test Raw Material";
        int currentStock = 100;
        int minimumStock = 50;

        when(rawMaterialRepository.getRawMaterialExists(connection, rawMaterialId)).thenReturn(false);

        RawMaterial result = rawMaterialService.registerRawMaterial(rawMaterialId, name, description, currentStock, minimumStock);

        assertNotNull(result);
        assertEquals(rawMaterialId, result.getId());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(currentStock, result.getCurrentStock());
        assertEquals(minimumStock, result.getMinimumStock());
        verify(rawMaterialRepository, times(1)).saveRawMaterial(connection, result);
    }

    @Test
    void testRegisterRawMaterial_AlreadyExists() {
        String rawMaterialId = "RM001";
        when(rawMaterialRepository.getRawMaterialExists(connection, rawMaterialId)).thenReturn(true);

        assertThrows(ProductException.class, () -> {
            rawMaterialService.registerRawMaterial(rawMaterialId, "Test Raw Material", "Description", 100, 50);
        });
    }

}
