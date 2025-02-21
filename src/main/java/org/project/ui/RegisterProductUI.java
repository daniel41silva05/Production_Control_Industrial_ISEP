package org.project.ui;

import org.project.controller.ProductController;
import org.project.domain.Product;
import org.project.domain.ProductCategory;
import org.project.exceptions.ProductException;
import org.project.ui.utils.Utils;

import java.util.*;

public class RegisterProductUI implements Runnable {

    private final ProductController controller;

    public RegisterProductUI() {
        this.controller = new ProductController();
    }

    public void run() {
        try {
            showProductCategories();

            List<String> optionCategory = new ArrayList<>();
            optionCategory.add("Register the product in a new category.");
            optionCategory.add("Register the product in an existing category.");
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
            String name = Utils.readLineFromConsole("Enter Name: ");
            String description = Utils.readLineFromConsole("Enter Description: ");
            int size = Utils.readIntegerFromConsole("Enter Size: ");
            int capacity = Utils.readIntegerFromConsole("Enter Capacity: ");
            String color = Utils.readLineFromConsole("Enter Color: ");
            String unitName = Utils.readLineFromConsole("Enter Unit Name: ");
            String unitSymbol = Utils.readLineFromConsole("Enter Unit Symbol: ");
            double price = Utils.readDoubleFromConsole("Enter Price: ");

            Product product = controller.registerProduct(productID, unitName, unitSymbol, name, description, category, capacity, size, color, price);
            if (product == null) {
                System.out.println("\nProduct registration failed.");
            } else {
                System.out.println("\nProduct registered successfully.");
                showProduct(product);
            }
        } catch (ProductException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nProduct registration failed.");
        }
    }

    private void showProductCategories () {
        List<ProductCategory> categories = controller.getProductCategories();
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
        System.out.println(" - Product ID: " + product.getId());
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
