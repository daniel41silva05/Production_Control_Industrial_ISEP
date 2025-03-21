package org.project.controller;

import org.project.model.Operation;
import org.project.model.OperationType;
import org.project.service.OperationService;

import java.util.List;

public class OperationController {

    private OperationService operationService;

    public OperationController() {
        operationService = new OperationService();
    }

    public List<OperationType> getOperationTypes() {
        return operationService.getOperationTypes();
    }

    public List<Operation> getOperations() {
        return operationService.getOperations();
    }

    public Operation registerOperation(int id, String name, int executionTime, int typeID) {
        return operationService.registerOperation(id, name, executionTime, typeID);
    }

    public OperationType registerOperationType(int id, String name) {
        return operationService.registerOperationType(id, name);
    }

    public Operation updateOperationTime(int id, int executionTime) {
        return operationService.updateOperationTime(id, executionTime);
    }

    public List<Operation> registerOperationsFromCSV(String filePath) {
        return operationService.registerOperationsFromCSV(filePath);
    }

    public List<OperationType> registerOperationTypesFromCSV(String filePath) {
        return operationService.registerOperationTypesFromCSV(filePath);
    }

}
