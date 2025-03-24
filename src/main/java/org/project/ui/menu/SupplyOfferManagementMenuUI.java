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
        options.add(new MenuItem("Cancel a Supply Offer", new CancelSupplyOfferUI()));
        options.add(new MenuItem("Update a Supply Offer", new UpdateSupplyOfferUI()));
        options.add(new MenuItem("Confirm completion of a Supply Offer", new CompleteSupplyOfferUI()));
        options.add(new MenuItem("Consult Active Supply Offers", new ConsultActiveSupplyOffersUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- SUPPLY OFFER MANAGEMENT MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
