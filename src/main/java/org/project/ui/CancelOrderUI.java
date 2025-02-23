package org.project.ui;

import org.project.controller.OrderController;
import org.project.domain.Client;
import org.project.domain.Order;
import org.project.exceptions.ClientException;
import org.project.exceptions.OrderException;
import org.project.ui.utils.Utils;

import java.util.List;

public class CancelOrderUI implements Runnable {

    private final OrderController controller;

    public CancelOrderUI() {
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

            boolean delete = Utils.confirm("Do you want to cancel an order?");
            if (!delete) {
                return;
            }

            int orderID = Utils.readIntegerFromConsole("Enter Order ID: ");

            Order order = controller.cancelOrder(orderID);
            if (order == null) {
                System.out.println("\nFailed to cancel order.");
            } else {
                System.out.println("\nOrder canceled successfully.");
            }

        } catch (ClientException | OrderException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nFailed to cancel order.");
        }
    }

    private void showOrdersClient (Client client) {
        System.out.println(" - Client ID: " + client.getId() + " | Name: " + client.getName() + " | VATIN: " + client.getVatin());
        List<Order> orders = client.getOrders();
        if (!orders.isEmpty()) {
            System.out.println(" - Orders: ");
            for (Order order : orders) {
                System.out.println(" -- Order ID: " + order.getId());
            }
        }
    }

}
