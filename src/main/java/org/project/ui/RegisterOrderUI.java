package org.project.ui;

import org.project.controller.OrderController;
import org.project.domain.Client;
import org.project.domain.Order;
import org.project.domain.Product;
import org.project.exceptions.ClientException;
import org.project.exceptions.OrderException;
import org.project.exceptions.ProductException;
import org.project.ui.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.*;

public class RegisterOrderUI implements Runnable {

    private final OrderController controller;

    public RegisterOrderUI() {
        this.controller = new OrderController();
    }

    public void run() {
        try {
            showClients();

            List<Product> products = controller.getProducts();
            if (products.isEmpty()) {
                System.out.println("\nNo products available to order.");
            }

            int clientID = Utils.readIntegerFromConsole("Enter Client ID: ");

            Client client = controller.getClient(clientID);
            if (client == null) {
                System.out.println("\nClient acquisition failed.");
                return;
            }

            showOrdersClient(client);
            boolean register = Utils.confirm("Do you want to register a new order?");
            if (!register) {
                return;
            }

            int orderID = Utils.readIntegerFromConsole("Enter Order ID: ");
            Date orderDate = Utils.readDateFromConsole("Enter Order Date: ");
            Date deliveryDate = Utils.readDateFromConsole("Enter Delivery Date: ");
            String street = Utils.readLineFromConsole("Enter Delivery Street: ");
            String zipCode = Utils.readLineFromConsole("Enter Delivery Zip Code: ");
            String town = Utils.readLineFromConsole("Enter Delivery Town: ");
            String country = Utils.readLineFromConsole("Enter Delivery Country: ");
            boolean setPrice = Utils.confirm("Do you want to set the order price? (otherwise it will be calculated automatically)");
            int price = 0;
            if (setPrice) {
                price = Utils.readIntegerFromConsole("Enter Price: ");
            }

            Order order = controller.registerOrder(clientID, orderID, street, zipCode, town, country, orderDate, deliveryDate, price, orderProducts(products));
            if (order == null) {
                System.out.println("\nOrder registration failed.");
            } else {
                System.out.println("\nOrder registered successfully.");
                showOrder(order);
            }
        } catch (ClientException | OrderException | ProductException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nOrder registration failed.");
        }
    }

    private void showClients() {
        List<Client> clients = controller.getClients();
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
        System.out.println(" - Client ID: " + client.getId() + " | Name: " + client.getName() + " | VATIN: " + client.getVatin());
        List<Order> orders = client.getOrders();
        if (!orders.isEmpty()) {
            System.out.println(" - Orders: ");
            for (Order order : orders) {
                System.out.println(" -- Order ID: " + order.getId());
                for (Map.Entry<Product, Integer> entry : order.getProductQuantity().entrySet()) {
                    System.out.println(" --- Product ID: " + entry.getKey().getId() + " | Product Name: " + entry.getKey().getName() + " | Quantity: " + entry.getValue());
                }
            }
        }
    }

    private Map<String, Integer> orderProducts(List<Product> productList) {
        System.out.println("\nProducts:");
        for (Product product : productList) {
            System.out.println(" - Product ID: " + product.getId() + " | Name: " + product.getName());
        }

        Map<String, Integer> products = new HashMap<>();
        boolean addMoreProducts = true;

        while (addMoreProducts) {
            String productID = Utils.readLineFromConsole("Enter Product ID: ");
            int quantity = Utils.readIntegerFromConsole("Enter Quantity: ");

            products.put(productID, quantity);

            addMoreProducts = Utils.confirm("Do you want to add another product?");
        }

        return products;
    }

    private void showOrder(Order order) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println(" - Order ID: " + order.getId());
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
    }
}
