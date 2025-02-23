package org.project.ui;

import org.project.controller.ProductController;
import org.project.domain.Product;
import org.project.domain.ProductCategory;
import org.project.exceptions.ProductException;
import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ChangeProductCategoryUI implements Runnable {

    private final ProductController controller;

    public ChangeProductCategoryUI() {
        this.controller = new ProductController();
    }

    public void run() {
        try {
            List<Product> products = controller.getProducts();
            if (products.isEmpty()) {
                System.out.println("\nNo products registered.");
                return;
            }
            for (Product product : products) {
                showProduct(product);
            }

            boolean update = Utils.confirm("Do you want to update a product category?");
            if (!update) {
                return;
            }

            List<ProductCategory> categories = controller.getProductCategories();
            System.out.println("\nProduct Categories: ");
            for (ProductCategory category : categories) {
                System.out.println(" - Product Category ID: " + category.getId() + " | Name: " + category.getName());
            }

            List<String> optionCategory = new ArrayList<>();
            optionCategory.add("Update the product in a new category.");
            optionCategory.add("Update the product in an existing category.");
            int option = Utils.showAndSelectIndex(optionCategory, "\nProduct Category:");

            int categoryID = Utils.readIntegerFromConsole("Enter Product Category ID: ");
            ProductCategory category = null;
            if (option == 0) {
                String categoryName = Utils.readLineFromConsole("Enter Product Category Name: ");
                category = controller.registerCategory(categoryID, categoryName);
            } else if (option == 1) {
                category = controller.getProductCategory(categoryID);
            }
            if (category == null) {
                System.out.println("Product Category failed.");
                return;
            }

            String productID = Utils.readLineFromConsole("Enter Product ID: ");

            Product product = controller.changeProductCategory(productID, category);
            if (product != null) {
                System.out.println("\nProduct Category updated successfully.");
                showProduct(product);
            } else {
                System.out.println("\nProduct Category update failed.");
            }
        } catch (ProductException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nUpdate Product Category failed.");
        }
    }

    private void showProduct(Product product) {
        System.out.println("\nProduct ID: " + product.getId());
        System.out.println(" - Name: " + product.getName());
        System.out.println(" - Description: " + product.getDescription());
        System.out.println(" - Category ID: " + product.getCategory().getId());
        System.out.println(" - Category Name: " + product.getCategory().getName());
        System.out.println(" - Size: " + product.getSize());
        System.out.println(" - Capacity: " + product.getCapacity());
        System.out.println(" - Color: " + product.getColor());
        System.out.println(" - Price: " + product.getPrice() + product.getUnit().getSymbol());
    }
}
