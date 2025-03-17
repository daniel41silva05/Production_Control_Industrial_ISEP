package org.project.ui.menu;

import org.project.ui.*;
import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ProductManagementMenuUI implements Runnable {

    public ProductManagementMenuUI() {
    }

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Register a Product", new RegisterProductUI()));
        options.add(new MenuItem("Change a product Category", new ChangeProductCategoryUI()));
        options.add(new MenuItem("Delete a Product Category", new DeleteProductCategoryUI()));
        options.add(new MenuItem("Consult products in a Category", new ConsultProductsInCategoryUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- PRODUCT MANAGEMENT MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
