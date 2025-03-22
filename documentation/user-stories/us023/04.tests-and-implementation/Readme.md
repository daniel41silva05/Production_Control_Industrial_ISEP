# US023 - Register a Workstation

## 4. Tests 

**Test 1:** Check if the workstation is being registered correctly, being stored in the repository.

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

**Test 2:** Check that it is not possible to register an workstation that already exists - AC04.

    @Test
    void testRegisterWorkstation_AlreadyExists() {
        int workstationId = 1;
        when(workstationRepository.getWorkstationExists(connection, workstationId)).thenReturn(true);

        assertThrows(WorkstationException.class, () -> {
            workstationService.registerWorkstation(workstationId, "Cutting Station", 3);
        });
    }

**Test 3:** Check that it is not possible to register a workstation without a type - AC04.

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

## 5. Construction (Implementation)

### Class WorkstationService 

```java
public Workstation registerWorkstation (int id, String name, int typeId) {
    if (workstationRepository.getWorkstationExists(connection, id)) {
        throw WorkstationException.workstationAlreadyExists(id);
    }

    WorkstationType type = getWorkstationTypeById(typeId);

    Workstation workstation = new Workstation(id, name);
    workstationRepository.save(connection, workstation, type);

    return workstation;
}
```
```java
private WorkstationType getWorkstationTypeById(int id) {
    WorkstationType workstationType = workstationTypeRepository.getById(connection, id);

    if (workstationType == null) {
        throw WorkstationException.workstationTypeNotFound(id);
    }

    return workstationType;
}
```

## 6. Observations

n/a