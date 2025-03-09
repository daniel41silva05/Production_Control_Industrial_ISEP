package org.project.ui.menu;

import org.project.ui.*;
import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ClientManagementMenuUI implements Runnable {

    public ClientManagementMenuUI() {
    }

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Register a Client", new RegisterClientUI()));
        options.add(new MenuItem("Delete a Client", new DeleteClientUI()));
        options.add(new MenuItem("Update a Client", new UpdateClientUI()));
        options.add(new MenuItem("Consult Client Status", new StatusClientUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- CLIENT MANAGEMENT MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
