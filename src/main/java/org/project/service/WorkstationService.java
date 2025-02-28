package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.exceptions.OperationException;
import org.project.exceptions.WorkstationException;
import org.project.io.CsvReader;
import org.project.model.OperationType;
import org.project.model.Workstation;
import org.project.model.WorkstationType;
import org.project.repository.OperationTypeRepository;
import org.project.repository.Repositories;
import org.project.repository.WorkstationRepository;
import org.project.repository.WorkstationTypeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkstationService {

    private DatabaseConnection connection;
    private WorkstationRepository workstationRepository;
    private WorkstationTypeRepository workstationTypeRepository;
    private OperationTypeRepository operationTypeRepository;

    public WorkstationService () {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        workstationRepository = repositories.getWorkstationRepository();
        workstationTypeRepository = repositories.getWorkstationTypeRepository();
        operationTypeRepository = repositories.getOperationTypeRepository();
    }

    public List<WorkstationType> getWorkstationTypes() {
        return workstationTypeRepository.getAll(connection);
    }

    private WorkstationType getWorkstationTypeById(int id) throws WorkstationException {
        if (!workstationTypeRepository.getWorkstationTypeExists(connection, id)) {
            throw new WorkstationException("Workstation Type with ID " + id + " not exists.");
        }

        return workstationTypeRepository.getById(connection, id);
    }

    public Workstation registerWorkstation (int id, String name, int typeId) throws WorkstationException {
        if (workstationRepository.getWorkstationExists(connection, id)) {
            throw new WorkstationException("Workstation with ID " + id + " already exists.");
        }

        WorkstationType type = getWorkstationTypeById(typeId);

        Workstation workstation = new Workstation(id, name);
        boolean success = workstationRepository.save(connection, workstation, type);
        if (!success) {
            return null;
        }
        return workstation;
    }

    public WorkstationType registerWorkstationType (int id, String name) throws WorkstationException {
        if (workstationTypeRepository.getWorkstationTypeExists(connection, id)) {
            throw new WorkstationException("Workstation Type with ID " + id + " already exists.");
        }

        WorkstationType workstationType = new WorkstationType(id, name);
        boolean success = workstationTypeRepository.save(connection, workstationType);
        if (!success) {
            return null;
        }
        return workstationType;
    }

    public Workstation deleteWorkstation (int id) throws WorkstationException {
        if (!workstationRepository.getWorkstationExists(connection, id)) {
            throw new WorkstationException("Workstation with ID " + id + " not exists.");
        }

        Workstation workstation = workstationRepository.getById(connection, id);

        boolean success = workstationRepository.delete(connection, workstation);
        if (!success) {
            return null;
        }
        return workstation;
    }

    public WorkstationType deleteWorkstationType (int id) throws WorkstationException {
        WorkstationType workstationType = getWorkstationTypeById(id);

        if (!workstationType.getWorkstations().isEmpty()) {
            throw new WorkstationException("Workstation Type cannot be deleted because it contains active workstations.");
        }

        boolean success = workstationTypeRepository.delete(connection, workstationType);
        if (!success) {
            return null;
        }
        return workstationType;
    }

    public Map<OperationType, List<WorkstationType>> registerWorkstationTypesFromCSV (String filePath) throws WorkstationException, OperationException {
        Map<OperationType, List<WorkstationType>> operationWorkstationsMap = new HashMap<>();
        Map<WorkstationType, Map<Integer, Integer>> workstationOperationTimeMap = CsvReader.loadWorkstationTypes(filePath);

        boolean success = false;

        for (Map.Entry<WorkstationType, Map<Integer, Integer>> entry : workstationOperationTimeMap.entrySet()) {
            WorkstationType workstationType = entry.getKey();
            Map<Integer, Integer> operationTimeMap = entry.getValue();

            if (!workstationTypeRepository.getWorkstationTypeExists(connection, workstationType.getId())) {
                workstationTypeRepository.save(connection, workstationType);

                for (Map.Entry<Integer, Integer> entry2 : operationTimeMap.entrySet()) {
                    int operationID = entry2.getKey();
                    int setupTime = entry2.getValue();

                    if (!operationTypeRepository.getOperationTypeExists(connection, operationID)) {
                        return null;
                    }
                    OperationType operationType = operationTypeRepository.getById(connection, operationID);
                    operationType.getWorkstationSetupTime().put(workstationType, setupTime);

                    success = workstationTypeRepository.saveOperationWorkstationTime(connection, operationType, workstationType);
                    if (!success) {
                        return null;
                    }
                    operationWorkstationsMap.computeIfAbsent(operationType, k -> new ArrayList<>()).add(workstationType);
                }

            } else {
                workstationType = workstationTypeRepository.getById(connection, workstationType.getId());

                for (Map.Entry<Integer, Integer> entry2 : operationTimeMap.entrySet()) {
                    int operationID = entry2.getKey();
                    int setupTime = entry2.getValue();

                    if (!operationTypeRepository.getOperationTypeExists(connection, operationID)) {
                        return null;
                    }
                    OperationType operationType = operationTypeRepository.getById(connection, operationID);

                    if (operationType.getWorkstationSetupTime().containsKey(workstationType)) {
                        if (operationType.getWorkstationSetupTime().get(workstationType) != setupTime) {
                            operationType.getWorkstationSetupTime().put(workstationType, setupTime);
                            success = workstationTypeRepository.updateOperationWorkstationTime(connection, operationType, workstationType);
                        }
                    } else {
                        operationType.getWorkstationSetupTime().put(workstationType, setupTime);
                        success = workstationTypeRepository.saveOperationWorkstationTime(connection, operationType, workstationType);
                    }

                    if (!success) {
                        return null;
                    }
                    operationWorkstationsMap.computeIfAbsent(operationType, k -> new ArrayList<>()).add(workstationType);
                }
            }

        }

        return  operationWorkstationsMap;
    }

    public Integer changeSetupTime (int operationTypeId, int workstationTypeId, int newSetupTime) throws WorkstationException, OperationException {
        if (!operationTypeRepository.getOperationTypeExists(connection, operationTypeId)) {
            throw new OperationException("Operation type with ID " + operationTypeId + " not exists.");
        }

        if (!workstationTypeRepository.getWorkstationTypeExists(connection, workstationTypeId)) {
            throw new WorkstationException("Workstation Type with ID " + workstationTypeId + " not exists.");
        }

        OperationType operationType = operationTypeRepository.getById(connection, operationTypeId);
        WorkstationType workstationType = workstationTypeRepository.getById(connection, workstationTypeId);

        if (!operationType.getWorkstationSetupTime().containsKey(workstationType)) {
            throw new WorkstationException("Workstation Type with ID " + workstationTypeId + " is not assigned to perform Operation Type with ID " + operationTypeId);
        }

        if (!operationType.getWorkstationSetupTime().get(workstationType).equals(newSetupTime)) {
            throw new OperationException("The setup time remains the same.");
        }

        operationType.getWorkstationSetupTime().put(workstationType, newSetupTime);

        boolean success = workstationTypeRepository.updateOperationWorkstationTime(connection, operationType, workstationType);
        if (!success) {
            return null;
        }

        return newSetupTime;
    }
}
