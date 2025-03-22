package org.project.ui;

import org.project.controller.OperationController;
import org.project.controller.WorkstationController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.OperationException;
import org.project.exceptions.WorkstationException;
import org.project.model.*;
import org.project.ui.utils.Utils;

import java.util.List;

public class ChangeOperationWorkstationSetupTimeUI implements Runnable {

    private final WorkstationController workstationController;
    private final OperationController operationController;

    public ChangeOperationWorkstationSetupTimeUI() {
        this.workstationController = new WorkstationController();
        this.operationController = new OperationController();
    }

    public void run() {
        try {
            showOperationTypes(operationController.getOperationTypes());
            showWorkstationTypes(workstationController.getWorkstationTypes());

            int operationID = Utils.readIntegerFromConsole("Enter Operation Type ID: ");
            int workstationID = Utils.readIntegerFromConsole("Enter Workstation Type ID: ");
            int setupTime = Utils.readIntegerFromConsole("Enter New Setup Time: ");

            Integer newSetupTime = workstationController.changeSetupTime(operationID, workstationID, setupTime);
            if (newSetupTime == null) {
                System.out.println("\nSetup time update failed.");
            } else {
                System.out.println("\nSetup time updated successfully.");
                showChange(operationID, workstationID, newSetupTime);
            }
        } catch (WorkstationException | OperationException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showOperationTypes(List<OperationType> types) {
        System.out.println("\nOperation Types:");
        if (types.isEmpty()) {
            System.out.println("No operation types registered.");
        } else {
            for (OperationType type : types) {
                System.out.println(" - Operation Type ID: " + type.getId() + " | Name: " + type.getName());
            }
        }
    }

    private void showWorkstationTypes(List<WorkstationType> types) {
        System.out.println("\nWorkstation Types:");
        if (types.isEmpty()) {
            System.out.println("No workstation types registered.");
        } else {
            for (WorkstationType type : types) {
                System.out.println(" - Workstation Type ID: " + type.getId() + " | Name: " + type.getName());
            }
        }
    }

    private void showChange (int operationID, int workstationID, int setupTime) {
        System.out.println("\nWorkstation Type ID: " + workstationID);
        System.out.println("Operation Type ID: " + operationID);
        System.out.println("Setup Time: " + setupTime);
    }

}
