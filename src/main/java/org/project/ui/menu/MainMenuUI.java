package org.project.ui.menu;

import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainMenuUI implements Runnable {

    public MainMenuUI() {
    }

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Client Management", new ClientManagementMenuUI()));
        options.add(new MenuItem("Order Management", new OrderManagementMenuUI()));
        options.add(new MenuItem("Product, Component and Raw Material Management", new ProductManagementMenuUI()));
        options.add(new MenuItem("Operation and Workstation Management", new OperationWorkstationManagementMenuUI()));
        options.add(new MenuItem("Supplier Management", new SupplierManagementMenuUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- MAIN MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
