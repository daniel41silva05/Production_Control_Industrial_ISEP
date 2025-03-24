package org.project.ui.menu;

import org.project.ui.*;
import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ProductionManagementMenuUI implements Runnable {

    public ProductionManagementMenuUI() {
    }

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Upload Production Tree from CSV file", new LoadProductionTreeCsvUI()));
        options.add(new MenuItem("Consult Production Tree", new ConsultProductionTreeUI()));
        options.add(new MenuItem("Check Has Sufficient Stock for Order", new CheckHasSufficientStockOrderUI()));
        options.add(new MenuItem("Check Has Sufficient Stock for Product", new CheckHasSufficientStockProductUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- PRODUCTION MANAGEMENT MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
