package org.project.ui.menu;

import org.project.ui.*;
import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SupplierManagementMenuUI implements Runnable {

    public SupplierManagementMenuUI() {
    }

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Register a Supplier", new RegisterSupplierUI()));
        options.add(new MenuItem("Delete a Supplier", new DeleteSupplierUI()));
        options.add(new MenuItem("Update a Supplier", new UpdateSupplierUI()));
        options.add(new MenuItem("Consult Supplier Status", new StatusSupplierUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- SUPPLIER MANAGEMENT MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
