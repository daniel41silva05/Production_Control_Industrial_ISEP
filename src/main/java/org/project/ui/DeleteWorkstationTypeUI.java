package org.project.ui;

import org.project.controller.WorkstationController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.WorkstationException;
import org.project.model.WorkstationType;
import org.project.ui.utils.Utils;

import java.util.List;

public class DeleteWorkstationTypeUI implements Runnable {

    private final WorkstationController controller;

    public DeleteWorkstationTypeUI() {
        this.controller = new WorkstationController();
    }

    public void run() {
        try {
            showWorkstationTypes(controller.getWorkstationTypes());

            boolean delete = Utils.confirm("Do you want to delete a workstation type?");
            if (!delete) {
                return;
            }

            int typeID = Utils.readIntegerFromConsole("Enter Workstation Type ID: ");

            WorkstationType type = controller.deleteWorkstationType(typeID);
            if (type == null) {
                System.out.println("\nFailed to delete workstation type.");
            } else {
                System.out.println("\nWorkstation Type deleted successfully.");
            }

        } catch (WorkstationException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
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

}