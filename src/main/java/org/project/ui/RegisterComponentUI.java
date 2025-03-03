package org.project.ui;

import org.project.controller.ComponentController;
import org.project.exceptions.ProductException;
import org.project.model.Component;
import org.project.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RegisterComponentUI implements Runnable {

    private final ComponentController controller;

    public RegisterComponentUI() {
        this.controller = new ComponentController();
    }

    public void run() {
        try {
            showComponents(controller.getComponents());

            List<String> optionComponent = new ArrayList<>();
            optionComponent.add("Register a Component.");
            optionComponent.add("Register Components from a CSV file.");
            int option = Utils.showAndSelectIndex(optionComponent, "\nComponent:");

            if (option == 0) {
                String id = Utils.readLineFromConsole("Enter Component ID: ");
                String name = Utils.readLineFromConsole("Enter Name: ");
                String description = Utils.readLineFromConsole("Enter Description: ");
                Component component = controller.registerComponent(id, name, description);
                if (component == null) {
                    System.out.println("\nComponent registration failed.");
                } else {
                    System.out.println("\nComponent registered successfully.");
                    showComponent(component);
                }

            } else if (option == 1) {
                String fileComponents = Utils.readLineFromConsole("Enter file name of components (ex. components.csv): ");
                List<Component> components = controller.registerComponentsFromCSV("files\\" + fileComponents);
                if (components == null) {
                    System.out.println("\nUpload of components failed.");
                } else {
                    showComponents(components);
                }
            }

        } catch (ProductException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nComponent registration failed.");
        }
    }

    private void showComponents(List<Component> components) {
        System.out.println("\nComponents:");
        if (components.isEmpty()) {
            System.out.println("No components registered.");
        } else {
            for (Component component : components) {
                System.out.println(" - Component ID: " + component.getId() + " | Name: " + component.getName() + " | Description: " + component.getDescription());
            }
        }
    }

    private void showComponent(Component component) {
        System.out.println(" - Component ID: " + component.getId());
        System.out.println(" - Name: " + component.getName());
        System.out.println(" - Description: " + component.getDescription());
    }
}
