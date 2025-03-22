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

    public WorkstationService (DatabaseConnection connection, WorkstationRepository workstationRepository, WorkstationTypeRepository workstationTypeRepository, OperationTypeRepository operationTypeRepository) {
        this.connection = connection;
        this.workstationRepository = workstationRepository;
        this.workstationTypeRepository = workstationTypeRepository;
        this.operationTypeRepository = operationTypeRepository;
    }

    public List<WorkstationType> getWorkstationTypes() {
        return workstationTypeRepository.getAll(connection);
    }

    public List<Workstation> getWorkstations() {
        return workstationRepository.getAll(connection);
    }

    private WorkstationType getWorkstationTypeById(int id) {
        WorkstationType workstationType = workstationTypeRepository.getById(connection, id);

        if (workstationType == null) {
            throw WorkstationException.workstationTypeNotFound(id);
        }

        return workstationType;
    }

    private Workstation getWorkstationById(int id) {
        Workstation workstation = workstationRepository.getById(connection, id);

        if (workstation == null) {
            throw WorkstationException.workstationNotFound(id);
        }

        return workstation;
    }

    public Workstation registerWorkstation (int id, String name, int typeId) {
        if (workstationRepository.getWorkstationExists(connection, id)) {
            throw WorkstationException.workstationAlreadyExists(id);
        }

        WorkstationType type = getWorkstationTypeById(typeId);

        Workstation workstation = new Workstation(id, name);
        workstationRepository.save(connection, workstation, type);

        return workstation;
    }

    public WorkstationType registerWorkstationType (int id, String name) {
        if (workstationTypeRepository.getWorkstationTypeExists(connection, id)) {
            throw WorkstationException.workstationTypeAlreadyExists(id);
        }

        WorkstationType workstationType = new WorkstationType(id, name);
        workstationTypeRepository.save(connection, workstationType);

        return workstationType;
    }

    public Workstation deleteWorkstation (int id) {
        Workstation workstation = getWorkstationById(id);

        workstationRepository.delete(connection, workstation);

        return workstation;
    }

    public WorkstationType deleteWorkstationType (int id) {
        WorkstationType workstationType = getWorkstationTypeById(id);

        if (!workstationType.getWorkstations().isEmpty()) {
            throw WorkstationException.workstationTypeCanNotDeleted();
        }

        workstationTypeRepository.delete(connection, workstationType);

        return workstationType;
    }

    public Map<OperationType, List<WorkstationType>> registerWorkstationTypesFromCSV (String filePath) {
        Map<OperationType, List<WorkstationType>> operationWorkstationsMap = new HashMap<>();
        Map<WorkstationType, Map<Integer, Integer>> workstationOperationTimeMap = CsvReader.loadWorkstationTypes(filePath);

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

                    workstationTypeRepository.saveOperationWorkstationTime(connection, operationType, workstationType);

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
                            workstationTypeRepository.updateOperationWorkstationTime(connection, operationType, workstationType);
                        }
                    } else {
                        operationType.getWorkstationSetupTime().put(workstationType, setupTime);
                        workstationTypeRepository.saveOperationWorkstationTime(connection, operationType, workstationType);
                    }

                    operationWorkstationsMap.computeIfAbsent(operationType, k -> new ArrayList<>()).add(workstationType);
                }
            }

        }

        return  operationWorkstationsMap;
    }

    public Map<Workstation, WorkstationType> registerWorkstationFromCSV (String filePath) {
        Map<Workstation, WorkstationType> operationWorkstationsMap = new HashMap<>();
        Map<Workstation, Integer> operationWorkstationIdMap = CsvReader.loadWorkstations(filePath);

        for (Map.Entry<Workstation, Integer> entry : operationWorkstationIdMap.entrySet()) {
            Workstation workstation = entry.getKey();
            Integer workstationTypeId = entry.getValue();
            WorkstationType workstationType = null;

            if (!workstationRepository.getWorkstationExists(connection, workstation.getId())) {
                if (!workstationTypeRepository.getWorkstationTypeExists(connection, workstationTypeId)) {
                    return null;
                }
                workstationType = workstationTypeRepository.getById(connection, workstationTypeId);

                workstationRepository.save(connection, workstation, workstationType);
            }

            operationWorkstationsMap.put(workstation, workstationType);
        }

        return operationWorkstationsMap;
    }

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

        if (!operationType.getWorkstationSetupTime().get(workstationType).equals(newSetupTime)) {
            throw OperationException.setupTimeRemainsSame();
        }

        operationType.getWorkstationSetupTime().put(workstationType, newSetupTime);

        workstationTypeRepository.updateOperationWorkstationTime(connection, operationType, workstationType);

        return newSetupTime;
    }

}
