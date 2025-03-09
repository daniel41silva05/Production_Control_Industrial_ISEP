package org.project.ui.menu;

import org.project.ui.*;
import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainMenuUI implements Runnable {

    public MainMenuUI() {
    }

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Client Management", new ClientManagementMenuUI()));
        options.add(new MenuItem("Register a Client's Order", new RegisterOrderUI()));
        options.add(new MenuItem("Cancel an Order", new CancelOrderUI()));
        options.add(new MenuItem("Update an Order", new UpdateOrderUI()));
        options.add(new MenuItem("Confirm completion of an Order", new CompleteOrderUI()));
        options.add(new MenuItem("Consult Active Orders", new ConsultActiveOrdersUI()));
        options.add(new MenuItem("Register a Product", new RegisterProductUI()));
        options.add(new MenuItem("Change a product Category", new ChangeProductCategoryUI()));
        options.add(new MenuItem("Delete a Product Category", new DeleteProductCategoryUI()));
        options.add(new MenuItem("Consult products in a Category", new ConsultProductsInCategoryUI()));
        options.add(new MenuItem("Register a Component", new RegisterComponentUI()));
        options.add(new MenuItem("Register a Raw Material", new RegisterRawMaterialUI()));
        options.add(new MenuItem("Change Minimum Raw Material Stock", new ChangeMinimumRawMaterialStockUI()));
        options.add(new MenuItem("Consult Stock of Raw Materials", new ConsultRawMaterialsStockUI()));
        options.add(new MenuItem("Consult Raw Materials in Stock Alert", new ConsultRawMaterialsStockAlertUI()));
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
            option = Utils.showAndSelectIndex(options, "\n\n--- MAIN MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
