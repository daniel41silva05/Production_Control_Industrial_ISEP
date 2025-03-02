package org.project.ui;

import org.project.controller.WorkstationController;
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
            showWorkstationTypes();

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

        } catch (WorkstationException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nFailed to delete workstation type.");
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

}