package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.exceptions.WorkstationException;
import org.project.model.Workstation;
import org.project.model.WorkstationType;
import org.project.repository.WorkstationRepository;
import org.project.repository.WorkstationTypeRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterWorkstationTest {

    private WorkstationService workstationService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private WorkstationRepository workstationRepository;

    @Mock
    private WorkstationTypeRepository workstationTypeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        workstationService = new WorkstationService(connection, workstationRepository, workstationTypeRepository, null);
    }

    @Test
    void testRegisterWorkstation_Success() {
        int workstationId = 1;
        String name = "Assembly Station";
        int typeId = 2;
        WorkstationType type = new WorkstationType(typeId, "Mechanical");

        when(workstationRepository.getWorkstationExists(connection, workstationId)).thenReturn(false);
        when(workstationTypeRepository.getById(connection, typeId)).thenReturn(type);

        Workstation result = workstationService.registerWorkstation(workstationId, name, typeId);

        assertNotNull(result);
        assertEquals(workstationId, result.getId());
        assertEquals(name, result.getName());
        verify(workstationRepository, times(1)).save(connection, result, type);
    }

    @Test
    void testRegisterWorkstation_AlreadyExists() {
        int workstationId = 1;
        when(workstationRepository.getWorkstationExists(connection, workstationId)).thenReturn(true);

        assertThrows(WorkstationException.class, () -> {
            workstationService.registerWorkstation(workstationId, "Cutting Station", 3);
        });
    }

    @Test
    void testRegisterWorkstation_WorkstationTypeNotFound() {
        int workstationId = 2;
        int typeId = 5;

        when(workstationRepository.getWorkstationExists(connection, workstationId)).thenReturn(false);
        when(workstationTypeRepository.getById(connection, typeId)).thenReturn(null);

        assertThrows(WorkstationException.class, () -> {
            workstationService.registerWorkstation(workstationId, "Painting Station", typeId);
        });
    }
}
