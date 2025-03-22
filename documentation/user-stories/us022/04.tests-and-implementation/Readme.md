# US022 - Register an Operation

## 4. Tests 

**Test 1:** Check if the operation is being registered correctly, being stored in the repository.

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

**Test 2:** Check that it is not possible to register an operation that already exists - AC04.

    @Test
    void testRegisterOperation_AlreadyExists() {
        int operationId = 1;
        when(operationRepository.getOperationExists(connection, operationId)).thenReturn(true);

        assertThrows(OperationException.class, () -> {
            operationService.registerOperation(operationId, "Welding", 45, 3);
        });
    }

**Test 3:** Check that it is not possible to register a operation without a type - AC04.

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

## 5. Construction (Implementation)

### Class OperationService 

```java
public Operation registerOperation(int id, String name, int executionTime, int typeID) {
    if (operationRepository.getOperationExists(connection, id)) {
        throw OperationException.operationAlreadyExists(id);
    }

    OperationType type = getOperationTypeByID(typeID);

    Operation operation =  new Operation(id, type, name, executionTime);

    operationRepository.save(connection, operation);

    return operation;
}
```
```java
private OperationType getOperationTypeByID(int id) {
    OperationType opType = operationTypeRepository.getById(connection, id);

    if (opType == null) {
        throw OperationException.operationTypeNotFound(id);
    }

    return opType;
}
```

## 6. Observations

n/a