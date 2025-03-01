package org.project.ui;

import org.project.controller.OrderController;
import org.project.controller.WorkstationController;
import org.project.exceptions.*;
import org.project.model.*;
import org.project.ui.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.*;

public class RegisterWorkstationUI implements Runnable {

    private final WorkstationController controller;

    public RegisterWorkstationUI() {
        this.controller = new WorkstationController();
    }

    public void run() {
        try {
            showWorkstationTypes();

            List<String> optionWorkstation = new ArrayList<>();
            optionWorkstation.add("Register the workstation in a new type.");
            optionWorkstation.add("Register the workstation in an existing type.");
            optionWorkstation.add("Cancel registration of the workstation.");
            int option = Utils.showAndSelectIndex(optionWorkstation, "\nWorkstation Type:");

            int typeID = Utils.readIntegerFromConsole("Enter Workstation Type ID: ");
            if (option == 0) {
                String typeName = Utils.readLineFromConsole("Enter Workstation Type Name: ");
                controller.registerWorkstationType(typeID, typeName);
            } else if (option == 2) {
                return;
            }

            int workstationID = Utils.readIntegerFromConsole("Enter Workstation ID: ");
            String workstationName = Utils.readLineFromConsole("Enter Workstation Name: ");

            Workstation workstation = controller.registerWorkstation(workstationID, workstationName, typeID);
            if (workstation == null) {
                System.out.println("\nWorkstation registration failed.");
            } else {
                System.out.println("\nWorkstation registered successfully.");
                showWorkstation(workstation, typeID);
            }
        } catch (WorkstationException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nWorkstation registration failed.");
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

    private void showWorkstation(Workstation workstation, int type) {
        System.out.println(" - Workstation ID: " + workstation.getId());
        System.out.println(" - Name: " + workstation.getName());
        System.out.println(" - Type ID: " + type);
    }
}
