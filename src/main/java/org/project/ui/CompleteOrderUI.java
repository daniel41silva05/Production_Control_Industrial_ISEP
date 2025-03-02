package org.project.ui;

import org.project.controller.OrderController;
import org.project.exceptions.ClientException;
import org.project.exceptions.OrderException;
import org.project.exceptions.ProductException;
import org.project.model.Client;
import org.project.model.Order;
import org.project.model.ProcessState;
import org.project.ui.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.List;

public class CompleteOrderUI implements Runnable {

    private final OrderController controller;

    public CompleteOrderUI() {
        this.controller = new OrderController();
    }

    public void run() {
        try {
            List<Client> clients = controller.getClients();
            System.out.println("\nClients:");
            if (clients.isEmpty()) {
                System.out.println("No clients registered.");
                return;
            }
            for (Client client : clients) {
                System.out.println(" - Client ID: " + client.getId() + " | Name: " + client.getName() + " | VATIN: " + client.getVatin());
            }

            int clientID = Utils.readIntegerFromConsole("Enter Client ID: ");

            Client client = controller.getClient(clientID);
            if (client == null) {
                System.out.println("\nClient acquisition failed.");
                return;
            }

            showOrdersClient(client);

            boolean update = Utils.confirm("Do you want to complete an order?");
            if (!update) {
                return;
            }

            int orderID = Utils.readIntegerFromConsole("Enter Order ID: ");

            Order order = controller.completeOrder(orderID);
            if (order == null) {
                System.out.println("\nFailed to complete order.");
            } else {
                System.out.println("\nOrder completed successfully.");
            }

        } catch (ClientException | OrderException | ProductException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nFailed to complete order.");
        }
    }

    private void showOrdersClient (Client client) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println(" - Client ID: " + client.getId() + " | Name: " + client.getName() + " | VATIN: " + client.getVatin());
        List<Order> orders = client.getOrders();
        if (!orders.isEmpty()) {
            System.out.println(" - Orders: ");
            for (Order order : orders) {
                if (order.getState().equals(ProcessState.PENDING)) {
                    System.out.println(" -- Order ID: " + order.getId() + " | Delivery Date: " + dateFormat.format(order.getDeliveryDate()));
                }
            }
        }
    }

}
