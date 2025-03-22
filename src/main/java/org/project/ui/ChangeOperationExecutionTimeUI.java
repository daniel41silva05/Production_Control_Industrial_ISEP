package org.project.ui;

import org.project.controller.OperationController;
import org.project.controller.ProductController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.OperationException;
import org.project.exceptions.ProductException;
import org.project.model.Operation;
import org.project.model.OperationType;
import org.project.model.Product;
import org.project.model.ProductCategory;
import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ChangeOperationExecutionTimeUI implements Runnable {

    private final OperationController controller;

    public ChangeOperationExecutionTimeUI() {
        this.controller = new OperationController();
    }

    public void run() {
        try {
            showOperations(controller.getOperations());

            int operationID = Utils.readIntegerFromConsole("Enter Operation ID: ");
            int executionTime = Utils.readIntegerFromConsole("Enter New Execution Time: ");

            Operation operation = controller.updateOperationTime(operationID, executionTime);
            if (operation == null) {
                System.out.println("\nOperation update failed.");
            } else {
                System.out.println("\nOperation updated successfully.");
                showOperation(operation);
            }

        } catch (OperationException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showOperations(List<Operation> operations) {
        System.out.println("\nOperations:");
        if (operations.isEmpty()) {
            System.out.println("No operations registered.");
        } else {
            for (Operation operation : operations) {
                System.out.println(" - Operation ID: " + operation.getId() + " | Name: " + operation.getName() + " | Execution Time: " + operation.getExecutionTime());
            }
        }
    }

    private void showOperation(Operation operation) {
        System.out.println(" - Operation ID: " + operation.getId());
        System.out.println(" - Operation Name: " + operation.getName());
        System.out.println(" - Execution Time: " + operation.getExecutionTime());
        System.out.println(" - Type ID: " + operation.getType().getId());
        System.out.println(" - Type Name: " + operation.getType().getName());
    }
}
