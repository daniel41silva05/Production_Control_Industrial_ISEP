package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.dto.OperationDTO;
import org.project.exceptions.OperationException;
import org.project.io.CsvReader;
import org.project.model.Operation;
import org.project.model.OperationType;
import org.project.repository.OperationRepository;
import org.project.repository.OperationTypeRepository;
import org.project.repository.Repositories;

import java.util.ArrayList;
import java.util.List;

public class OperationService {

    private DatabaseConnection connection;
    private OperationRepository operationRepository;
    private OperationTypeRepository operationTypeRepository;

    public OperationService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        operationRepository = repositories.getOperationRepository();
        operationTypeRepository = repositories.getOperationTypeRepository();
    }

    public List<OperationType> getOperationTypes() {
        return operationTypeRepository.getAll(connection);
    }

    public List<Operation> getOperations() {
        return operationRepository.getAll(connection);
    }

    private OperationType getOperationTypeByID(int id) {
        OperationType opType = operationTypeRepository.getById(connection, id);

        if (opType == null) {
            throw OperationException.operationTypeNotFound(id);
        }

        return opType;
    }

    private Operation getOperationByID(int id) {
        Operation operation = operationRepository.getById(connection, id);

        if (operation == null) {
            throw OperationException.operationNotFound(id);
        }

        return operation;
    }

    public Operation registerOperation(int id, String name, int executionTime, int typeID) {
        if (operationRepository.getOperationExists(connection, id)) {
            throw OperationException.operationAlreadyExists(id);
        }

        OperationType type = getOperationTypeByID(typeID);

        Operation operation =  new Operation(id, type, name, executionTime);

        operationRepository.save(connection, operation);

        return operation;
    }

    public OperationType registerOperationType(int id, String name) {
        if (operationTypeRepository.getOperationTypeExists(connection, id)) {
            throw OperationException.operationTypeAlreadyExists(id);
        }

        OperationType operationType =  new OperationType(id, name);

        operationTypeRepository.save(connection, operationType);

        return operationType;
    }

    public List<Operation> registerOperationsFromCSV(String filePath) {
        List<OperationDTO> operationDTOs = CsvReader.loadOperations(filePath);
        List<Operation> operations = new ArrayList<>();

        for (OperationDTO dto : operationDTOs) {

            Operation operation;
            OperationType type = operationTypeRepository.getById(connection, dto.getTypeId());

            if (!operationRepository.getOperationExists(connection, dto.getId())) {

                operation = new Operation(dto.getId(), type, dto.getName(), dto.getExecutionTime());
                operationRepository.save(connection, operation);

            } else {
                operation = operationRepository.getById(connection, dto.getId());
                operation.setName(dto.getName());
                operation.setExecutionTime(dto.getExecutionTime());
                operation.setType(type);

                operationRepository.updateOperation(connection, operation);
            }

            operations.add(operation);
        }
        return operations;
    }

    public List<OperationType> registerOperationTypesFromCSV(String filePath) {
        List<OperationType> operationTypes = CsvReader.loadOperationTypes(filePath);

        for (OperationType operationType : operationTypes) {

            if (!operationTypeRepository.getOperationTypeExists(connection, operationType.getId())) {
                operationTypeRepository.save(connection, operationType);
            }
        }
        return operationTypes;
    }

    public Operation updateOperationTime(int id, int executionTime) {
        Operation operation = getOperationByID(id);

        if (operation.getExecutionTime() == executionTime) {
            throw OperationException.timeRemainsSame();
        }

        operation.setExecutionTime(executionTime);

        operationRepository.updateOperation(connection, operation);

        return operation;
    }

}
