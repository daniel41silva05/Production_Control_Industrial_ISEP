package org.project.ui;

import org.project.controller.ProductController;
import org.project.exceptions.DatabaseException;
import org.project.model.Product;
import org.project.model.ProductCategory;
import org.project.exceptions.ProductException;
import org.project.ui.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteProductCategoryUI implements Runnable {

    private final ProductController controller;

    public DeleteProductCategoryUI() {
        this.controller = new ProductController();
    }

    public void run() {
        try {
            showProductCategories(controller.getProductCategories());

            int categoryID = Utils.readIntegerFromConsole("Enter Product Category ID: ");

            List<Product> products = controller.productListInCategory(categoryID);

            if (products.isEmpty()) {
                System.out.println("\nThe category you want to consult does not currently contain any products.");
            } else {
                System.out.println("\nProducts from category " + categoryID + ": ");
                for (Product product : products) {
                    showProduct(product);
                }
            }

            boolean delete = Utils.confirm("Do you want to delete a category?");
            if (!delete) {
                return;
            }

            Map<Product,Integer> productNewCategory = new HashMap<>();
            for (Product product : products) {
                int newCategoryID = Utils.readIntegerFromConsole("Enter the new category ID for product " + product.getId() + ": ");
                productNewCategory.put(product, newCategoryID);
            }

            ProductCategory category = controller.deleteCategory(categoryID, productNewCategory);
            if (category == null) {
                System.out.println("\nFailed to delete category.");
            } else {
                System.out.println("\nCategory deleted successfully.");
            }

        } catch (ProductException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showProductCategories (List<ProductCategory> categories) {
        if (!categories.isEmpty()) {
            System.out.println("\nProduct Categories: ");
            for (ProductCategory category : categories) {
                System.out.println(" - Product Category ID: " + category.getId() + " | Name: " + category.getName());
            }
        } else {
            System.out.println("\nNo product categories registered.");
        }
    }

    private void showProduct(Product product) {
        System.out.println("\nProduct ID: " + product.getId());
        System.out.println(" - Name: " + product.getName());
        System.out.println(" - Description: " + product.getDescription());
        System.out.println(" - Size: " + product.getSize());
        System.out.println(" - Capacity: " + product.getCapacity());
        System.out.println(" - Color: " + product.getColor());
        System.out.println(" - Price: " + product.getPrice() + "$");
    }
}
