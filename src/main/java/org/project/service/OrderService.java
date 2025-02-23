package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.domain.*;
import org.project.exceptions.ClientException;
import org.project.exceptions.OrderException;
import org.project.exceptions.ProductException;
import org.project.repository.*;

import java.util.*;

public class OrderService {

    private DatabaseConnection connection;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;
    private AddressRepository addressRepository;

    public OrderService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        orderRepository = repositories.getOrderRepository();
        productRepository = repositories.getProductRepository();
        clientRepository = repositories.getClientRepository();
        addressRepository = repositories.getAddressRepository();
    }

    public Order getOrderByID (int orderID) throws OrderException {
        if (orderRepository.getOrderExists(connection, orderID)) {
            throw new OrderException("Order with ID " + orderID + " not exists.");
        }

        return orderRepository.getByID(connection, orderID);
    }

    public Order registerOrder(int clientID, int orderID, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date orderDate, Date deliveryDate, int price, Map<String, Integer> productIDQuantity) throws ClientException, OrderException, ProductException {
        if (deliveryDate.before(orderDate)) {
            throw new OrderException("Delivery date cannot be before Order date.");
        }

        if (orderRepository.getOrderExists(connection, orderID)) {
            throw new OrderException("Order with ID " + orderID + " already exists.");
        }

        if (!clientRepository.getClientExists(connection, clientID)) {
            throw new ClientException("Client with ID " + clientID + " not exists.");
        }
        Client client = clientRepository.getById(connection, clientID);
        if (client == null) {
            return null;
        }

        Address address = addressRepository.findAddress(connection, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
        if (address == null) {
            int id = addressRepository.getAddressCount(connection);
            address = new Address(id, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
            addressRepository.save(connection, address);
        }

        Map<Product, Integer> productQuantity = new HashMap<>();
        for (Map.Entry<String, Integer> productEntry : productIDQuantity.entrySet()) {
            if (!productRepository.getProductExists(connection, productEntry.getKey())) {
                throw new ProductException("Product with ID " + productEntry.getKey() + " not exists.");
            }
            Product product = productRepository.getByID(connection, productEntry.getKey());
            if (product == null) {
                return null;
            }
            productQuantity.put(product, productEntry.getValue());
        }

        if (price == 0) {
            price = calculatePrice(productQuantity);
        }

        Order order = new Order(orderID, address, orderDate, deliveryDate, price, productQuantity);

        boolean success = orderRepository.save(connection, order, client);
        if (!success) {
            return null;
        }

        client.getOrders().add(order);
        return order;
    }

    private int calculatePrice(Map<Product, Integer> productQuantity) {
        double result = 0;

        for (Map.Entry<Product, Integer> productEntry : productQuantity.entrySet()) {
            Product product = productEntry.getKey();
            double quantity = productEntry.getValue();
            result += product.getPrice() * quantity;
        }

        return (int) result;
    }

    public Order deleteOrder (int id) throws OrderException {
        if (!orderRepository.getOrderExists(connection, id)) {
            throw new OrderException("Order with ID " + id + " not exists.");
        }

        Order order = orderRepository.getByID(connection, id);

        boolean success = orderRepository.delete(connection, order);
        if (!success) {
            return null;
        }

        return order;
    }

    public Order updateOrder (Order order, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date orderDate, Date deliveryDate, double price) throws OrderException {
        if (deliveryDate.before(orderDate)) {
            throw new OrderException("Delivery date cannot be before Order date.");
        }

        Address address = order.getDeliveryAddress();
        if (!address.getStreet().equals(deliveryStreet) || !address.getZipCode().equals(deliveryZipCode) || !address.getTown().equals(deliveryTown) || !address.getCountry().equals(deliveryCountry)) {
            address = addressRepository.findAddress(connection, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
            if (address == null) {
                int id = addressRepository.getAddressCount(connection);
                address = new Address(id, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
                addressRepository.save(connection, address);
            }
        }

        order.setDeliveryAddress(address);
        order.setOrderDate(orderDate);
        order.setDeliveryDate(deliveryDate);
        order.setPrice(price);

        boolean success = orderRepository.update(connection, order);
        if (!success) {
            return null;
        }

        return order;
    }

    public List<Order> activeOrders () {
        List<Order> activeOrders = new ArrayList<>();

        List<Order> orders = orderRepository.getAll(connection);

        for (Order order : orders) {
            if (order.getDeliveryDate().after(new Date())) {
                activeOrders.add(order);
            }
        }

        return activeOrders;
    }
}
