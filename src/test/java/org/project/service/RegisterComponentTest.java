package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ProductException;
import org.project.model.Component;
import org.project.repository.ComponentRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterComponentTest {

    private ComponentService componentService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private ComponentRepository componentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        componentService = new ComponentService(connection, componentRepository);
    }

    @Test
    void testRegisterComponent_Success() {
        String componentId = "C001";
        String name = "Test Component";
        String description = "Description of Test Component";

        when(componentRepository.getComponentExists(connection, componentId)).thenReturn(false);

        Component result = componentService.registerComponent(componentId, name, description);

        assertNotNull(result);
        assertEquals(componentId, result.getId());
        assertEquals(name, result.getName());
        verify(componentRepository, times(1)).saveComponent(connection, result);
    }

    @Test
    void testRegisterComponent_AlreadyExists() {
        String componentId = "C001";
        when(componentRepository.getComponentExists(connection, componentId)).thenReturn(true);

        assertThrows(ProductException.class, () -> {
            componentService.registerComponent(componentId, "Test", "Description");
        });
    }

}
