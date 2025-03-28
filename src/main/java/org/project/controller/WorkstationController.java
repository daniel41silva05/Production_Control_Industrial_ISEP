package org.project.controller;

import org.project.model.OperationType;
import org.project.model.Workstation;
import org.project.model.WorkstationType;
import org.project.service.OperationService;
import org.project.service.WorkstationService;

import java.util.List;
import java.util.Map;

public class WorkstationController {

    private WorkstationService workstationService;

    public WorkstationController() {
        workstationService = new WorkstationService();
    }

    public List<WorkstationType> getWorkstationTypes() {
        return workstationService.getWorkstationTypes();
    }

    public List<Workstation> getWorkstations() {
        return workstationService.getWorkstations();
    }

    public Workstation registerWorkstation (int id, String name, int typeId) {
        return workstationService.registerWorkstation (id, name, typeId);
    }

    public WorkstationType registerWorkstationType (int id, String name) {
        return workstationService.registerWorkstationType (id, name);
    }

    public Workstation deleteWorkstation (int id) {
        return workstationService.deleteWorkstation (id);
    }

    public WorkstationType deleteWorkstationType (int id) {
        return workstationService.deleteWorkstationType (id);
    }

    public Integer changeSetupTime (int operationTypeId, int workstationTypeId, int newSetupTime) {
        return workstationService.changeSetupTime (operationTypeId, workstationTypeId, newSetupTime);
    }

    public Map<OperationType, List<WorkstationType>> registerWorkstationTypesFromCSV (String filePath) {
        return workstationService.registerWorkstationTypesFromCSV (filePath);
    }

    public Map<Workstation, WorkstationType> registerWorkstationFromCSV (String filePath) {
        return workstationService.registerWorkstationFromCSV (filePath);
    }

}
