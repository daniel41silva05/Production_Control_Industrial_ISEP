package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.OperationException;
import org.project.model.Operation;
import org.project.model.OperationType;
import org.project.repository.OperationRepository;
import org.project.repository.OperationTypeRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterOperationTest {

    private OperationService operationService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private OperationTypeRepository operationTypeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        operationService = new OperationService(connection, operationRepository, operationTypeRepository);
    }

    @Test
    void testRegisterOperation_Success() {
        int operationId = 1;
        String name = "Cutting";
        int executionTime = 30;
        int typeId = 2;
        OperationType type = new OperationType(typeId, "Mechanical");

        when(operationRepository.getOperationExists(connection, operationId)).thenReturn(false);
        when(operationTypeRepository.getById(connection, typeId)).thenReturn(type);

        Operation result = operationService.registerOperation(operationId, name, executionTime, typeId);

        assertNotNull(result);
        assertEquals(operationId, result.getId());
        assertEquals(name, result.getName());
        verify(operationRepository, times(1)).save(connection, result);
    }

    @Test
    void testRegisterOperation_AlreadyExists() {
        int operationId = 1;
        when(operationRepository.getOperationExists(connection, operationId)).thenReturn(true);

        assertThrows(OperationException.class, () -> {
            operationService.registerOperation(operationId, "Welding", 45, 3);
        });
    }

    @Test
    void testRegisterOperation_OperationTypeNotFound() {
        int operationId = 2;
        int typeId = 5;

        when(operationRepository.getOperationExists(connection, operationId)).thenReturn(false);
        when(operationTypeRepository.getById(connection, typeId)).thenReturn(null);

        assertThrows(OperationException.class, () -> {
            operationService.registerOperation(operationId, "Painting", 20, typeId);
        });
    }
}
