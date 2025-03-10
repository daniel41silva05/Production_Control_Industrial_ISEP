package org.project.ui;

import org.project.controller.ClientController;
import org.project.controller.OrderController;
import org.project.controller.ProductController;
import org.project.exceptions.DatabaseException;
import org.project.model.Client;
import org.project.model.Order;
import org.project.model.Product;
import org.project.exceptions.ClientException;
import org.project.exceptions.OrderException;
import org.project.exceptions.ProductException;
import org.project.ui.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.*;

public class RegisterOrderUI implements Runnable {

    private final OrderController orderController;
    private final ClientController clientController;
    private final ProductController productController;

    public RegisterOrderUI() {
        this.orderController = new OrderController();
        this.clientController = new ClientController();
        this.productController = new ProductController();
    }

    public void run() {
        try {
            showClients(clientController.getAllClients());

            int clientID = Utils.readIntegerFromConsole("Enter Client ID: ");
            Client client = clientController.getClientById(clientID);

            showOrdersClient(client);
            boolean register = Utils.confirm("Do you want to register a new order?");
            if (!register) {
                return;
            }

            int orderID = Utils.readIntegerFromConsole("Enter Order ID: ");
            Date orderDate = Utils.readDateFromConsole("Enter Order Date: ");
            Date deliveryDate = Utils.readDateFromConsole("Enter Delivery Date: ");
            String street = Utils.readLineFromConsole("Enter Delivery Street: ");
            String zipCode = Utils.readZipCodeFromConsole("Enter Delivery Zip Code: ");
            String town = Utils.readLineFromConsole("Enter Delivery Town: ");
            String country = Utils.readLineFromConsole("Enter Delivery Country: ");

            showProducts(productController.getProducts());

            Map<String, Integer> products = new HashMap<>();
            boolean addMoreProducts = true;

            while (addMoreProducts) {
                String productID = Utils.readLineFromConsole("Enter Product ID: ");
                int quantity = Utils.readIntegerFromConsole("Enter Quantity: ");

                products.put(productID, quantity);

                addMoreProducts = Utils.confirm("Do you want to add another product?");
            }

            boolean setPrice = Utils.confirm("Do you want to set the order price? (otherwise it will be calculated automatically)");
            int price = 0;
            if (setPrice) {
                price = Utils.readIntegerFromConsole("Enter Price: ");
            }

            Order order = orderController.registerOrder(client, orderID, street, zipCode, town, country, orderDate, deliveryDate, price, products);
            if (order == null) {
                System.out.println("\nOrder registration failed.");
            } else {
                System.out.println("\nOrder registered successfully.");
                showOrder(order);
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
        System.out.println("\nClient ID: " + client.getId() + " | Name: " + client.getName() + " | VATIN: " + client.getVatin());
        List<Order> orders = client.getOrders();
        if (!orders.isEmpty()) {
            System.out.println("Orders: ");
            for (Order order : orders) {
                System.out.println(" - Order ID: " + order.getId());
                for (Map.Entry<Product, Integer> entry : order.getProductQuantity().entrySet()) {
                    System.out.println(" -- Product ID: " + entry.getKey().getId() + " | Product Name: " + entry.getKey().getName() + " | Quantity: " + entry.getValue());
                }
            }
        }
    }

    private void showProducts(List<Product> products) {
        System.out.println("\nProducts:");
        if (products.isEmpty()) {
            System.out.println("No products registered.");
        } else {
            for (Product product : products) {
                System.out.println(" - Product ID: " + product.getId() + " | Name: " + product.getName());
            }
        }
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
        System.out.println(" - Price: " + order.getPrice() + "$");
        System.out.println(" - Products: ");
        for (Map.Entry<Product, Integer> entry : order.getProductQuantity().entrySet()) {
            System.out.println(" --- Product ID: " + entry.getKey().getId() + " | Product Name: " + entry.getKey().getName() + " | Quantity: " + entry.getValue());
        }
    }
}
