package org.project.ui.menu;

import org.project.ui.*;
import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class OperationManagementMenuUI implements Runnable {

    public OperationManagementMenuUI() {
    }

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Register an Operation", new RegisterOperationUI()));
        options.add(new MenuItem("Change Operation Execution Time", new ChangeOperationExecutionTimeUI()));
        options.add(new MenuItem("Upload Operations from CSV file", new LoadOperationsCsvUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- OPERATION MANAGEMENT MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
