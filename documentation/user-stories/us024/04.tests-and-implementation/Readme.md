# US024 - Change Workstation Setup Time

## 4. Tests 

**Test 1:** Check if the setup time is changed successfully when all conditions are met.

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

**Test 2:** Check that it is not possible to change the setup time if the operation type does not exist.

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

**Test 3:** Check that it is not possible to change the setup time if the workstation type does not exist.

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

**Test 4:** Check that it is not possible to change the setup time if the workstation is not assigned to the operation type.

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

**Test 5:** Check that it is not possible to change the setup time if the new setup time is the same as the current setup time.

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

## 5. Construction (Implementation)

### Class WorkstationService 

```java
public Integer changeSetupTime (int operationTypeId, int workstationTypeId, int newSetupTime) {
    if (!operationTypeRepository.getOperationTypeExists(connection, operationTypeId)) {
        throw OperationException.operationTypeNotFound(operationTypeId);
    }

    if (!workstationTypeRepository.getWorkstationTypeExists(connection, workstationTypeId)) {
        throw WorkstationException.workstationNotFound(workstationTypeId);
    }

    OperationType operationType = operationTypeRepository.getById(connection, operationTypeId);
    WorkstationType workstationType = workstationTypeRepository.getById(connection, workstationTypeId);

    if (!operationType.getWorkstationSetupTime().containsKey(workstationType)) {
        throw WorkstationException.workstationOperationNotAssigned(workstationTypeId, operationTypeId);
    }

    if (operationType.getWorkstationSetupTime().get(workstationType).equals(newSetupTime)) {
        throw OperationException.setupTimeRemainsSame();
    }

    operationType.getWorkstationSetupTime().put(workstationType, newSetupTime);

    workstationTypeRepository.updateOperationWorkstationTime(connection, operationType, workstationType);

    return newSetupTime;
}
```

## 6. Observations

n/a