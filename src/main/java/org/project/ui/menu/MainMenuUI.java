package org.project.ui.menu;

import org.project.ui.DeleteClientUI;
import org.project.ui.RegisterClientUI;
import org.project.ui.RegisterOrderUI;
import org.project.ui.UpdateClientUI;
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
        options.add(new MenuItem("Register a Client's Order", new RegisterOrderUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- MAIN MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
