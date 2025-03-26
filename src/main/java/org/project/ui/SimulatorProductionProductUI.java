package org.project.ui;

import org.project.controller.ProductController;
import org.project.controller.ProductionController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.OperationException;
import org.project.exceptions.ProductException;
import org.project.model.Product;
import org.project.service.Event;
import org.project.ui.utils.Utils;

import java.util.LinkedList;

public class SimulatorProductionProductUI implements Runnable {

    private final ProductionController controller;
    private final ProductController productController;

    public SimulatorProductionProductUI() {
        this.controller = new ProductionController();
        this.productController = new ProductController();
    }

    public void run() {

        try {

            for (Product product : productController.getProducts()) {
                showProduct(product);
            }

            String productID = Utils.readLineFromConsole("Enter Product ID: ");

            LinkedList<Event> events = controller.simulator(productID);

            if (!events.isEmpty()) {
                System.out.println("\nSimulated Production Events:\n");
                showEventTableHeader();
                for (Event event : events) {
                    showEvent(event);
                }
            } else {
                System.out.println("\nNo events generated during the simulation.");
            }

        } catch (ProductException | OperationException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showProduct(Product product) {
        System.out.println("Product ID: " + product.getId());
        System.out.println(" - Name: " + product.getName());
        System.out.println(" - Description: " + product.getDescription());
        System.out.println(" - Category ID: " + product.getCategory().getId());
        System.out.println(" - Category Name: " + product.getCategory().getName());
        System.out.println(" - Size: " + product.getSize());
        System.out.println(" - Capacity: " + product.getCapacity());
        System.out.println(" - Color: " + product.getColor());
        System.out.println(" - Price: " + product.getPrice() + "$\n");
    }

    private void showEventTableHeader() {
        System.out.printf("| %-20s | %-25s | %-15s | %-15s | %-15s | %-10s | %-10s |\n",
                "Operation", "Item", "Quantity", "WorkStation", "Start Time", "End Time", "Duration");
        System.out.println("-----------------------------------------------------------------------------------");
    }

    private void showEvent(Event event) {
        System.out.printf("| %-20d | %-25s | %-15.2f | %-15d | %-15d | %-10d | %-10d |\n",
                event.getElement().getOperation().getId(),
                event.getElement().getPart().getId(),
                event.getElement().getQuantity(),
                event.getWorkStation().getId(),
                event.getStartTime(),
                event.getEndTime(),
                event.getEndTime() - event.getStartTime());
    }

}
