package org.project.ui;

import org.project.controller.OrderController;
import org.project.model.Order;
import org.project.model.Product;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ConsultActiveOrdersUI implements Runnable {

    private final OrderController controller;

    public ConsultActiveOrdersUI() {
        this.controller = new OrderController();
    }

    public void run() {
        try {

            List<Order> orders = controller.consultActiveOrders();

            if (orders.isEmpty()) {
                System.out.println("\nNo active orders.");
            } else {
                System.out.println("\nActive Orders:\n");
                for (Order order : orders) {
                    showOrder(order);
                }
            }

        } catch (Exception e) {
            System.out.println("\nConsult Order failed.");
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
        System.out.println(" - Price: " + order.getPrice());
        System.out.println(" - Products: ");
        for (Map.Entry<Product, Integer> entry : order.getProductQuantity().entrySet()) {
            System.out.println(" --- Product ID: " + entry.getKey().getId() + " | Product Name: " + entry.getKey().getName() + " | Quantity: " + entry.getValue());
        }
        System.out.println();
    }

}
