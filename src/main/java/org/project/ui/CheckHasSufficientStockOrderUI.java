package org.project.ui;

import org.project.controller.OrderController;
import org.project.controller.ProductionController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.OperationException;
import org.project.exceptions.OrderException;
import org.project.exceptions.ProductException;
import org.project.model.*;
import org.project.ui.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class CheckHasSufficientStockOrderUI implements Runnable {

    private final ProductionController controller;
    private final OrderController orderController;

    public CheckHasSufficientStockOrderUI() {
        this.controller = new ProductionController();
        this.orderController = new OrderController();
    }

    public void run() {

        try {

        for (Order order : orderController.getOrders()) {
            showOrder(order);
        }

        int orderID = Utils.readIntegerFromConsole("Enter Order ID: ");

        List<RawMaterial> insufficientStock = controller.getInsufficientRawMaterialStockOrder(orderID);

        if (insufficientStock.isEmpty()) {
            System.out.println("\nAll raw materials have sufficient stock.");
        } else {
            System.out.println("\nInsufficient stock for the following raw materials:");
            for (RawMaterial rawMaterial : insufficientStock) {
                System.out.println(" - Raw Material ID: " + rawMaterial.getId() + " | Name: " + rawMaterial.getName());
            }
        }

        } catch (OrderException | ProductException | OperationException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showOrder(Order order) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Order ID: " + order.getId());
        System.out.println(" - Order Date: " + dateFormat.format(order.getOrderDate()));
        System.out.println(" - Delivery Date: " + dateFormat.format(order.getDeliveryDate()));
        System.out.println(" - Delivery Address ID: " + order.getDeliveryAddress().getId());
        System.out.println(" - Delivery Street: " + order.getDeliveryAddress().getStreet());
        System.out.println(" - Delivery Zip Code: " + order.getDeliveryAddress().getZipCode());
        System.out.println(" - Delivery Town: " + order.getDeliveryAddress().getTown());
        System.out.println(" - Delivery Country: " + order.getDeliveryAddress().getCountry());
        System.out.println(" - Price: " + order.getPrice() + "$");
        System.out.println(" - Products: ");
        for (Map.Entry<Product, Integer> entry : order.getProductQuantity().entrySet()) {
            System.out.println(" --- Product ID: " + entry.getKey().getId() + " | Product Name: " + entry.getKey().getName() + " | Quantity: " + entry.getValue());
        }
        System.out.println();
    }

}
