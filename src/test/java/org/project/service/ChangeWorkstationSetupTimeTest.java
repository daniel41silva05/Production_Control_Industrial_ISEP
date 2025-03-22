package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.OperationException;
import org.project.exceptions.WorkstationException;
import org.project.model.OperationType;
import org.project.model.WorkstationType;
import org.project.repository.OperationTypeRepository;
import org.project.repository.WorkstationTypeRepository;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChangeWorkstationSetupTimeTest {

    private WorkstationService workstationService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private OperationTypeRepository operationTypeRepository;

    @Mock
    private WorkstationTypeRepository workstationTypeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        workstationService = new WorkstationService(connection, null, workstationTypeRepository, operationTypeRepository);
    }

    @Test
    void testChangeSetupTime_Success() {
        int operationTypeId = 1;
        int workstationTypeId = 2;
        int newSetupTime = 10;

        OperationType operationType = mock(OperationType.class);
        WorkstationType workstationType = mock(WorkstationType.class);

        when(operationTypeRepository.getOperationTypeExists(connection, operationTypeId)).thenReturn(true);
        when(workstationTypeRepository.getWorkstationTypeExists(connection, workstationTypeId)).thenReturn(true);
        when(operationTypeRepository.getById(connection, operationTypeId)).thenReturn(operationType);
        when(workstationTypeRepository.getById(connection, workstationTypeId)).thenReturn(workstationType);
        HashMap<WorkstationType, Integer> setupTimes = new HashMap<>();
        setupTimes.put(workstationType, 5);
        when(operationType.getWorkstationSetupTime()).thenReturn(setupTimes);

        int result = workstationService.changeSetupTime(operationTypeId, workstationTypeId, newSetupTime);

        assertEquals(newSetupTime, result);
        verify(workstationTypeRepository, times(1)).updateOperationWorkstationTime(connection, operationType, workstationType);
    }

    @Test
    void testChangeSetupTime_OperationTypeNotFound() {
        int operationTypeId = 1;
        int workstationTypeId = 2;
        int newSetupTime = 10;

        when(operationTypeRepository.getOperationTypeExists(connection, operationTypeId)).thenReturn(false);

        assertThrows(OperationException.class, () ->
                workstationService.changeSetupTime(operationTypeId, workstationTypeId, newSetupTime)
        );
    }

    @Test
    void testChangeSetupTime_WorkstationTypeNotFound() {
        int operationTypeId = 1;
        int workstationTypeId = 2;
        int newSetupTime = 10;

        when(operationTypeRepository.getOperationTypeExists(connection, operationTypeId)).thenReturn(true);
        when(workstationTypeRepository.getWorkstationTypeExists(connection, workstationTypeId)).thenReturn(false);

        assertThrows(WorkstationException.class, () ->
                workstationService.changeSetupTime(operationTypeId, workstationTypeId, newSetupTime)
        );
    }

    @Test
    void testChangeSetupTime_WorkstationOperationNotAssigned() {
        int operationTypeId = 1;
        int workstationTypeId = 2;
        int newSetupTime = 10;

        OperationType operationType = mock(OperationType.class);
        WorkstationType workstationType = mock(WorkstationType.class);

        when(operationTypeRepository.getOperationTypeExists(connection, operationTypeId)).thenReturn(true);
        when(workstationTypeRepository.getWorkstationTypeExists(connection, workstationTypeId)).thenReturn(true);
        when(operationTypeRepository.getById(connection, operationTypeId)).thenReturn(operationType);
        when(workstationTypeRepository.getById(connection, workstationTypeId)).thenReturn(workstationType);
        when(operationType.getWorkstationSetupTime()).thenReturn(Map.of());

        assertThrows(WorkstationException.class, () ->
                workstationService.changeSetupTime(operationTypeId, workstationTypeId, newSetupTime)
        );
    }

    @Test
    void testChangeSetupTime_SetupTimeRemainsSame() {
        int operationTypeId = 1;
        int workstationTypeId = 2;
        int newSetupTime = 5;

        OperationType operationType = mock(OperationType.class);
        WorkstationType workstationType = mock(WorkstationType.class);

        when(operationTypeRepository.getOperationTypeExists(connection, operationTypeId)).thenReturn(true);
        when(workstationTypeRepository.getWorkstationTypeExists(connection, workstationTypeId)).thenReturn(true);
        when(operationTypeRepository.getById(connection, operationTypeId)).thenReturn(operationType);
        when(workstationTypeRepository.getById(connection, workstationTypeId)).thenReturn(workstationType);
        HashMap<WorkstationType, Integer> setupTimes = new HashMap<>();
        setupTimes.put(workstationType, 5);
        when(operationType.getWorkstationSetupTime()).thenReturn(setupTimes);

        assertThrows(OperationException.class, () ->
                workstationService.changeSetupTime(operationTypeId, workstationTypeId, newSetupTime)
        );
    }
}
