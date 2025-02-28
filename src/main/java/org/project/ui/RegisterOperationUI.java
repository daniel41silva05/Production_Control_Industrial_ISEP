package org.project.ui;

import org.project.controller.OperationController;
import org.project.controller.OrderController;
import org.project.exceptions.ClientException;
import org.project.exceptions.OperationException;
import org.project.exceptions.OrderException;
import org.project.exceptions.ProductException;
import org.project.model.*;
import org.project.ui.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.*;

public class RegisterOperationUI implements Runnable {

    private final OperationController controller;

    public RegisterOperationUI() {
        this.controller = new OperationController();
    }

    public void run() {
        try {
            showOperationTypes();

            List<String> optionOperation = new ArrayList<>();
            optionOperation.add("Register the operation in a new type.");
            optionOperation.add("Register the operation in an existing type.");
            optionOperation.add("Cancel registration of the operation.");
            int option = Utils.showAndSelectIndex(optionOperation, "\nOperation Type:");

            int typeID = Utils.readIntegerFromConsole("Enter Operation Type ID: ");
            if (option == 0) {
                String typeName = Utils.readLineFromConsole("Enter Operation Type Name: ");
                controller.registerOperationType(typeID, typeName);
            } else if (option == 2) {
                return;
            }

            int operationID = Utils.readIntegerFromConsole("Enter Operation ID: ");
            String operationName = Utils.readLineFromConsole("Enter Operation Name: ");
            int executionTime = Utils.readIntegerFromConsole("Enter Execution Time: ");

            Operation operation = controller.registerOperation(operationID, operationName, executionTime, typeID);
            if (operation == null) {
                System.out.println("\nOperation registration failed.");
            } else {
                System.out.println("\nOperation registered successfully.");
                showOperation(operation);
            }
        } catch (OperationException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nOperation registration failed.");
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

    private void showOperation(Operation operation) {
        System.out.println(" - Operation ID: " + operation.getId());
        System.out.println(" - Operation Name: " + operation.getName());
        System.out.println(" - Execution Time: " + operation.getExecutionTime());
        System.out.println(" - Type ID: " + operation.getType().getId());
        System.out.println(" - Type Name: " + operation.getType().getName());
    }
}
