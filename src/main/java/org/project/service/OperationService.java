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

    private OperationType getOperationTypeByID(int id) throws OperationException {
        if (!operationTypeRepository.getOperationTypeExists(connection, id)) {
            throw OperationException.operationTypeNotFound(id);
        }

        return operationTypeRepository.getById(connection, id);
    }

    public Operation registerOperation(int id, String name, int executionTime, int typeID) throws OperationException {
        if (operationRepository.getOperationExists(connection, id)) {
            throw new OperationException("Operation with ID " + id + " already exists.");
        }

        OperationType type = getOperationTypeByID(typeID);

        Operation operation =  new Operation(id, type, name, executionTime);
        boolean success = operationRepository.save(connection, operation);
        if (!success) {
            return null;
        }
        return operation;
    }

    public OperationType registerOperationType(int id, String name) throws OperationException {
        if (operationTypeRepository.getOperationTypeExists(connection, id)) {
            throw new OperationException("Operation Type with ID " + id + " already exists.");
        }

        OperationType operationType =  new OperationType(id, name);
        boolean success = operationTypeRepository.save(connection, operationType);
        if (!success) {
            return null;
        }
        return operationType;
    }

    public List<Operation> registerOperationsFromCSV(String filePath) {
        List<OperationDTO> operationDTOs = CsvReader.loadOperations(filePath);
        List<Operation> operations = new ArrayList<>();

        for (OperationDTO dto : operationDTOs) {

            Operation operation;

            if (!operationTypeRepository.getOperationTypeExists(connection, dto.getTypeId())) {
                return null;
            }
            OperationType type = operationTypeRepository.getById(connection, dto.getTypeId());

            boolean success;

            if (!operationRepository.getOperationExists(connection, dto.getId())) {

                operation = new Operation(dto.getId(), type, dto.getName(), dto.getExecutionTime());
                success = operationRepository.save(connection, operation);

            } else {

                operation = operationRepository.getById(connection, dto.getId());
                operation.setName(dto.getName());
                operation.setExecutionTime(dto.getExecutionTime());
                operation.setType(type);

                success = operationRepository.updateOperation(connection, operation);
            }

            if (!success) {
                return null;
            } else {
                operations.add(operation);
            }
        }
        return operations;
    }

    public List<OperationType> registerOperationTypesFromCSV(String filePath) {
        List<OperationType> operationTypes = CsvReader.loadOperationTypes(filePath);

        for (OperationType operationType : operationTypes) {

            if (!operationTypeRepository.getOperationTypeExists(connection, operationType.getId())) {

                boolean success = operationTypeRepository.save(connection, operationType);
                if (!success) {
                    return null;
                }
            }
        }
        return operationTypes;
    }

    public Operation updateOperationTime(int id, int executionTime) throws OperationException {
        if (!operationRepository.getOperationExists(connection, id)) {
            throw new OperationException("Operation with ID " + id + " not exists.");
        }

        Operation operation = operationRepository.getById(connection, id);

        if (operation.getExecutionTime() == executionTime) {
            throw new OperationException("The execution time remains the same.");
        }

        operation.setExecutionTime(executionTime);

        boolean success = operationRepository.updateOperation(connection, operation);
        if (!success) {
            return null;
        }
        return operation;
    }

}
