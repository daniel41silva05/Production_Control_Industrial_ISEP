package org.project.ui;

import org.project.controller.WorkstationController;
import org.project.exceptions.OperationException;
import org.project.exceptions.WorkstationException;
import org.project.model.*;
import org.project.ui.utils.Utils;

import java.util.List;

public class ChangeOperationWorkstationSetupTimeUI implements Runnable {

    private final WorkstationController controller;

    public ChangeOperationWorkstationSetupTimeUI() {
        this.controller = new WorkstationController();
    }

    public void run() {
        try {
            showOperationTypes();
            showWorkstationTypes();

            int operationID = Utils.readIntegerFromConsole("Enter Operation Type ID: ");
            int workstationID = Utils.readIntegerFromConsole("Enter Workstation Type ID: ");
            int setupTime = Utils.readIntegerFromConsole("Enter New Setup Time: ");

            Integer newSetupTime = controller.changeSetupTime(operationID, workstationID, setupTime);
            if (newSetupTime == null) {
                System.out.println("\nSetup time update failed.");
            } else {
                System.out.println("\nSetup time updated successfully.");
                showChange(operationID, workstationID, newSetupTime);
            }
        } catch (WorkstationException | OperationException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nSetup time update failed.");
        }
    }

    private void showOperationTypes() {
        List<OperationType> types = controller.getOperationTypes();
        System.out.println("\nOperation Types:");
        if (types.isEmpty()) {
            System.out.println("No operation types registered.");
        } else {
            for (OperationType type : types) {
                System.out.println(" - Operation Type ID: " + type.getId() + " | Name: " + type.getName());
            }
        }
    }

    private void showWorkstationTypes() {
        List<WorkstationType> types = controller.getWorkstationTypes();
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
