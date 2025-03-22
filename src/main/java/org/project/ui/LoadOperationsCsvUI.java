package org.project.ui;

import org.project.controller.OperationController;
import org.project.exceptions.DatabaseException;
import org.project.model.*;
import org.project.ui.utils.Utils;

import java.util.List;

public class LoadOperationsCsvUI implements Runnable {

    private final OperationController controller;

    public LoadOperationsCsvUI() {
        this.controller = new OperationController();
    }

    public void run() {
        try {

        String fileOperationTypes = Utils.readLineFromConsole("Enter file name of operation types (ex. operation_types.csv): ");

        List<OperationType> operationTypes = controller.registerOperationTypesFromCSV("files\\" + fileOperationTypes);

        if (operationTypes == null) {
            System.out.println("\nUpload of operation types failed.");
            return;
        } else {
            showOperationTypes(operationTypes);
        }

        String fileOperations = Utils.readLineFromConsole("Enter file name of operations (ex. operations.csv): ");

        List<Operation> operations = controller.registerOperationsFromCSV("files\\" + fileOperations);

        if (operations == null) {
            System.out.println("\nUpload of operations failed.");
        } else {
            showOperations(operations);
        }

        } catch (DatabaseException e) {
        System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showOperationTypes(List<OperationType> operationTypes) {
        System.out.println("\nOperation Types:");
        for (OperationType type : operationTypes) {
            System.out.println(" - Operation Type ID: " + type.getId() + " | Name: " + type.getName());
        }
    }

    private void showOperations(List<Operation> operations) {
        System.out.println("\nOperations:");
        for (Operation op : operations) {
            System.out.println("\nOperation ID: " + op.getId());
            System.out.println("Name: " + op.getName());
            System.out.println("Execution Time: " + op.getExecutionTime());
            System.out.println("Operation Type: " + op.getType().getId());
        }
    }
}
