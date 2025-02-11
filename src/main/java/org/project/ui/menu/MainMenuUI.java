package org.project.ui.menu;

import org.project.ui.RegisterAddressUI;
import org.project.ui.RegisterClientUI;
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
        options.add(new MenuItem("Register a Address", new RegisterAddressUI()));
        options.add(new MenuItem("Register a Client", new RegisterClientUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- MAIN MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
