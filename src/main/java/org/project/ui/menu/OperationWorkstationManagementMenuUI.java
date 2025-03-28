package org.project.ui.menu;

import org.project.ui.*;
import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class OperationWorkstationManagementMenuUI implements Runnable {

    public OperationWorkstationManagementMenuUI() {
    }

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Register an Operation", new RegisterOperationUI()));
        options.add(new MenuItem("Change Operation Execution Time", new ChangeOperationExecutionTimeUI()));
        options.add(new MenuItem("Upload Operations from CSV file", new LoadOperationsCsvUI()));
        options.add(new MenuItem("Register a Workstation", new RegisterWorkstationUI()));
        options.add(new MenuItem("Delete a Workstation", new DeleteWorkstationUI()));
        options.add(new MenuItem("Delete a Workstation Type", new DeleteWorkstationTypeUI()));
        options.add(new MenuItem("Change Setup Time Workstation to an Operation", new ChangeOperationWorkstationSetupTimeUI()));
        options.add(new MenuItem("Upload Workstations from CSV file", new LoadWorkstationsCsvUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- OPERATION AND WORKSTATION MANAGEMENT MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
