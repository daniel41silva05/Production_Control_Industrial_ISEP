package org.project.ui;

import org.project.controller.WorkstationController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.WorkstationException;
import org.project.model.*;
import org.project.ui.utils.Utils;

import java.util.List;
import java.util.Map;

public class LoadWorkstationsCsvUI implements Runnable {

    private final WorkstationController controller;

    public LoadWorkstationsCsvUI() {
        this.controller = new WorkstationController();
    }

    public void run() {

        try {

        String fileWorkstationTypes = Utils.readLineFromConsole("Enter file name of workstation types (ex. workstation_types.csv): ");

        Map<OperationType, List<WorkstationType>> operationWorkstationMap = controller.registerWorkstationTypesFromCSV("files\\" + fileWorkstationTypes);

        if (operationWorkstationMap == null) {
            System.out.println("\nUpload of workstation types failed.");
            return;
        } else {
            showWorkstationTypes(operationWorkstationMap);
        }

        String fileWorkstations = Utils.readLineFromConsole("Enter file name of workstations (ex. workstations.csv): ");

        Map<Workstation, WorkstationType> workstationTypeMap = controller.registerWorkstationFromCSV("files\\" + fileWorkstations);

        if (workstationTypeMap == null) {
            System.out.println("\nUpload of workstations failed.");
        } else {
            showWorkstations(workstationTypeMap);
        }

        } catch (WorkstationException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showWorkstationTypes(Map<OperationType, List<WorkstationType>> map) {
        for (Map.Entry<OperationType, List<WorkstationType>> entry : map.entrySet()) {
            OperationType operationType = entry.getKey();
            List<WorkstationType> workstationTypes = entry.getValue();
            System.out.println("\nOperation Type ID: " + operationType.getId() + " | Name: " + operationType.getName());
            for (WorkstationType workstationType : workstationTypes) {
                System.out.println("- Workstation Type ID: " + workstationType.getId() + " | Name: " + workstationType.getName());
            }
        }
    }

    private void showWorkstations(Map<Workstation, WorkstationType> map) {
        System.out.println("\nWorkstations:");
        for (Map.Entry<Workstation, WorkstationType> entry : map.entrySet()) {
            Workstation workstation = entry.getKey();
            WorkstationType type = entry.getValue();
            System.out.println("- Workstation ID: " + workstation.getId() + " | Name: " + workstation.getName() + " | Type ID: " + type.getId() + " | Type Name: " + type.getName());
        }
    }
}
