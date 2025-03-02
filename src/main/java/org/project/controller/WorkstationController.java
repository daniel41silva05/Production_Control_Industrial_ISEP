package org.project.controller;

import org.project.exceptions.OperationException;
import org.project.exceptions.WorkstationException;
import org.project.model.OperationType;
import org.project.model.Workstation;
import org.project.model.WorkstationType;
import org.project.service.OperationService;
import org.project.service.WorkstationService;

import java.util.List;
import java.util.Map;

public class WorkstationController {

    private WorkstationService workstationService;
    private OperationService operationService;

    public WorkstationController() {
        workstationService = new WorkstationService();
        operationService = new OperationService();
    }

    public List<WorkstationType> getWorkstationTypes() {
        return workstationService.getWorkstationTypes();
    }

    public List<OperationType> getOperationTypes() {
        return operationService.getOperationTypes();
    }

    public List<Workstation> getWorkstations() {
        return workstationService.getWorkstations();
    }

    public Workstation registerWorkstation (int id, String name, int typeId) throws WorkstationException {
        return workstationService.registerWorkstation (id, name, typeId);
    }

    public WorkstationType registerWorkstationType (int id, String name) throws WorkstationException {
        return workstationService.registerWorkstationType (id, name);
    }

    public Workstation deleteWorkstation (int id) throws WorkstationException {
        return workstationService.deleteWorkstation (id);
    }

    public WorkstationType deleteWorkstationType (int id) throws WorkstationException {
        return workstationService.deleteWorkstationType (id);
    }

    public Integer changeSetupTime (int operationTypeId, int workstationTypeId, int newSetupTime) throws WorkstationException, OperationException {
        return workstationService.changeSetupTime (operationTypeId, workstationTypeId, newSetupTime);
    }

    public Map<OperationType, List<WorkstationType>> registerWorkstationTypesFromCSV (String filePath) {
        return workstationService.registerWorkstationTypesFromCSV (filePath);
    }

    public Map<Workstation, WorkstationType> registerWorkstationFromCSV (String filePath) {
        return workstationService.registerWorkstationFromCSV (filePath);
    }

}
