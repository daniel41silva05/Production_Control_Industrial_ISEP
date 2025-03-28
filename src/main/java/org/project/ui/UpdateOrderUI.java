package org.project.ui;

import org.project.controller.ClientController;
import org.project.controller.OrderController;
import org.project.exceptions.DatabaseException;
import org.project.model.Client;
import org.project.model.Order;
import org.project.exceptions.ClientException;
import org.project.exceptions.OrderException;
import org.project.ui.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UpdateOrderUI implements Runnable {

    private final OrderController orderController;
    private final ClientController clientController;

    public UpdateOrderUI() {
        this.orderController = new OrderController();
        this.clientController = new ClientController();
    }

    public void run() {
        try {
            showClients(clientController.getAllClients());

            int clientID = Utils.readIntegerFromConsole("Enter Client ID: ");
            Client client = clientController.getClientById(clientID);

            System.out.println("\nClient ID: " + client.getId() + " | Name: " + client.getName() + " | VATIN: " + client.getVatin());
            for (Order order : client.getOrders()) {
                showOrder(order);
            }

            boolean update = Utils.confirm("Do you want to update an order?");
            if (!update) {
                return;
            }

            int orderID = Utils.readIntegerFromConsole("Enter Order ID: ");
            Order order = orderController.getOrderByID(orderID);

            String street = order.getDeliveryAddress().getStreet();
            String zipCode = order.getDeliveryAddress().getZipCode();
            String town = order.getDeliveryAddress().getTown();
            String country = order.getDeliveryAddress().getCountry();
            Date orderDate = order.getOrderDate();
            Date deliveryDate = order.getDeliveryDate();
            double price = order.getPrice();

            if (Utils.confirm("Do you want to update the Order Date?")) {
                orderDate = Utils.readDateFromConsole("Enter new Order Date: ");
            }

            if (Utils.confirm("Do you want to update the Delivery Date?")) {
                deliveryDate = Utils.readDateFromConsole("Enter new Delivery Date: ");
            }

            if (Utils.confirm("Do you want to update the Price?")) {
                price = Utils.readIntegerFromConsole("Enter new Price: ");
            }

            if (Utils.confirm("Do you want to update the Address?")) {

                if (Utils.confirm("Do you want to update the Street?")) {
                    street = Utils.readLineFromConsole("Enter new Street: ");
                }
                if (Utils.confirm("Do you want to update the Zip Code?")) {
                    zipCode = Utils.readLineFromConsole("Enter new Zip Code: ");
                }
                if (Utils.confirm("Do you want to update the Town?")) {
                    town = Utils.readLineFromConsole("Enter new Town: ");
                }
                if (Utils.confirm("Do you want to update the Country?")) {
                    country = Utils.readLineFromConsole("Enter new Country: ");
                }
            }

            Order newOrder = orderController.updateOrder(order, street, zipCode, town, country, orderDate, deliveryDate, price);
            if (newOrder != null) {
                System.out.println("\nOrder updated successfully.");
                showOrder(newOrder);
            } else {
                System.out.println("\nOrder update failed.");
            }

        } catch (ClientException | OrderException | DatabaseException e) {
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

    private void showOrder(Order order) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("\nOrder ID: " + order.getId());
        System.out.println(" - Order Date: " + dateFormat.format(order.getOrderDate()));
        System.out.println(" - Delivery Date: " + dateFormat.format(order.getDeliveryDate()));
        System.out.println(" - Delivery Address ID: " + order.getDeliveryAddress().getId());
        System.out.println(" - Delivery Street: " + order.getDeliveryAddress().getStreet());
        System.out.println(" - Delivery Zip Code: " + order.getDeliveryAddress().getZipCode());
        System.out.println(" - Delivery Town: " + order.getDeliveryAddress().getTown());
        System.out.println(" - Delivery Country: " + order.getDeliveryAddress().getCountry());
        System.out.println(" - Price: " + order.getPrice() + "$");
    }

}
