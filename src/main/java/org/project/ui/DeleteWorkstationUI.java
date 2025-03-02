package org.project.ui;

import org.project.controller.WorkstationController;
import org.project.exceptions.WorkstationException;
import org.project.model.Workstation;
import org.project.ui.utils.Utils;

import java.util.List;

public class DeleteWorkstationUI implements Runnable {

    private final WorkstationController controller;

    public DeleteWorkstationUI() {
        this.controller = new WorkstationController();
    }

    public void run() {
        try {
            showWorkstations();

            boolean delete = Utils.confirm("Do you want to delete a workstation?");
            if (!delete) {
                return;
            }

            int workstationID = Utils.readIntegerFromConsole("Enter Workstation ID: ");

            Workstation workstation = controller.deleteWorkstation(workstationID);
            if (workstation == null) {
                System.out.println("\nFailed to delete workstation.");
            } else {
                System.out.println("\nWorkstation deleted successfully.");
            }

        } catch (WorkstationException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nFailed to delete workstation.");
        }
    }

    private void showWorkstations() {
        List<Workstation> workstations = controller.getWorkstations();
        System.out.println("\nWorkstations:");
        if (workstations.isEmpty()) {
            System.out.println("No workstations registered.");
        } else {
            for (Workstation ws : workstations) {
                System.out.println(" - Workstation ID: " + ws.getId() + " | Name: " + ws.getName());
            }
        }
    }

}
