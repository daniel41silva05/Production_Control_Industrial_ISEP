package org.project.ui;

import org.project.controller.ClientController;
import org.project.controller.OrderController;
import org.project.exceptions.ClientException;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.OrderException;
import org.project.exceptions.ProductException;
import org.project.model.Client;
import org.project.model.Order;
import org.project.model.ProcessState;
import org.project.ui.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.List;

public class CompleteOrderUI implements Runnable {

    private final OrderController orderController;
    private final ClientController clientController;

    public CompleteOrderUI() {
        this.orderController = new OrderController();
        this.clientController = new ClientController();
    }

    public void run() {
        try {
            showClients(clientController.getAllClients());

            int clientID = Utils.readIntegerFromConsole("Enter Client ID: ");
            Client client = clientController.getClientById(clientID);

            showOrdersClient(client);
            boolean update = Utils.confirm("Do you want to complete an order?");
            if (!update) {
                return;
            }

            int orderID = Utils.readIntegerFromConsole("Enter Order ID: ");

            Order order = orderController.completeOrder(orderID);
            if (order == null) {
                System.out.println("\nFailed to complete order.");
            } else {
                System.out.println("\nOrder completed successfully.");
            }

        } catch (ClientException | OrderException | ProductException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showClients(List<Client> clients) {
        System.out.println("\nClients:");
        if (clients.isEmpty()) {
            System.out.println("No clients registered.");
        } else {
            for (Client client : clients) {
                System.out.println(" - Client ID: " + client.getId() + " | Name: " + client.getName() + " | VATIN: " + client.getVatin());
            }
        }
    }

    private void showOrdersClient (Client client) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("\nClient ID: " + client.getId() + " | Name: " + client.getName() + " | VATIN: " + client.getVatin());
        List<Order> orders = client.getOrders();
        if (!orders.isEmpty()) {
            System.out.println("Orders: ");
            for (Order order : orders) {
                if (order.getState().equals(ProcessState.PENDING)) {
                    System.out.println(" - Order ID: " + order.getId() + " | Delivery Date: " + dateFormat.format(order.getDeliveryDate()));
                }
            }
        }
    }

}
