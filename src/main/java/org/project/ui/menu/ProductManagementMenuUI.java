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
        options.add(new MenuItem("Register a Component", new RegisterComponentUI()));
        options.add(new MenuItem("Register a Raw Material", new RegisterRawMaterialUI()));
        options.add(new MenuItem("Change Minimum Raw Material Stock", new ChangeMinimumRawMaterialStockUI()));
        options.add(new MenuItem("Consult Stock of Raw Materials", new ConsultRawMaterialsStockUI()));
        options.add(new MenuItem("Consult Raw Materials in Stock Alert", new ConsultRawMaterialsStockAlertUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- PRODUCT, COMPONENT AND RAW MATERIAL MANAGEMENT MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
