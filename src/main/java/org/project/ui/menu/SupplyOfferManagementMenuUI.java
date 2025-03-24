package org.project.ui.menu;

import org.project.ui.*;
import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SupplyOfferManagementMenuUI implements Runnable {

    public SupplyOfferManagementMenuUI() {
    }

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Register a Supply Offer", new RegisterSupplyOfferUI()));
        options.add(new MenuItem("Cancel an Order", new CancelOrderUI()));
        options.add(new MenuItem("Update an Order", new UpdateOrderUI()));
        options.add(new MenuItem("Confirm completion of an Order", new CompleteOrderUI()));
        options.add(new MenuItem("Consult Active Orders", new ConsultActiveOrdersUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- SUPPLY OFFER MANAGEMENT MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
