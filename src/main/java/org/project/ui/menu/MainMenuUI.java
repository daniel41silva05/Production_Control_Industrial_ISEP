package org.project.ui.menu;

import org.project.ui.*;
import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainMenuUI implements Runnable {
//    private final DataLoaderController controller;

    public MainMenuUI() {
//        controller = new DataLoaderController();
    }

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Register a Client", new RegisterClientUI()));
        options.add(new MenuItem("Delete a Client", new DeleteClientUI()));
        options.add(new MenuItem("Update a Client", new UpdateClientUI()));
        options.add(new MenuItem("Consult Client Status", new StatusClientUI()));
        options.add(new MenuItem("Register a Client's Order", new RegisterOrderUI()));
        options.add(new MenuItem("Cancel an Order", new CancelOrderUI()));
        options.add(new MenuItem("Update an Order", new UpdateOrderUI()));
        options.add(new MenuItem("Consult Active Orders", new ConsultActiveOrdersUI()));
        options.add(new MenuItem("Register a Product", new RegisterProductUI()));
        options.add(new MenuItem("Change a product Category", new ChangeProductCategoryUI()));
        options.add(new MenuItem("Delete a Product Category", new DeleteProductCategoryUI()));
        options.add(new MenuItem("Consult products in a Category", new ConsultProductsInCategoryUI()));
        options.add(new MenuItem("Register an Operation", new RegisterOperationUI()));
        options.add(new MenuItem("Change Operation Execution Time", new ChangeOperationExecutionTimeUI()));
        options.add(new MenuItem("Upload Operations from CSV file", new LoadOperationsCsvUI()));
        options.add(new MenuItem("Register a Workstation", new RegisterWorkstationUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- MAIN MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
