package org.project.service;

import org.project.common.Validator;
import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.exceptions.DatabaseException;
import org.project.model.*;
import org.project.exceptions.OrderException;
import org.project.repository.*;

import java.util.*;

public class OrderService {

    private DatabaseConnection connection;
    private OrderRepository orderRepository;
    private AddressRepository addressRepository;

    public OrderService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        orderRepository = repositories.getOrderRepository();
        addressRepository = repositories.getAddressRepository();
    }

    public Order getOrderByID (int orderID) throws OrderException {
        if (!orderRepository.getOrderExists(connection, orderID)) {
            throw new OrderException("Order with ID " + orderID + " not exists.");
        }

        return orderRepository.getByID(connection, orderID);
    }

    public Order registerOrder(Client client, int orderID, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date orderDate, Date deliveryDate, int price, Map<Product, Integer> productQuantity) {
        if (deliveryDate.before(orderDate)) {
            throw OrderException.invalidDeliveryDate();
        }

        if (client == null) {
            return null;
        }

        if (orderRepository.getOrderExists(connection, orderID)) {
            throw OrderException.orderAlreadyExists(orderID);
        }

        if (!Validator.isValidZipCode(deliveryZipCode)) {
            throw OrderException.invalidZipCode();
        }

        Address address = addressRepository.findAddress(connection, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
        if (address == null) {
            int id = addressRepository.getAddressCount(connection);
            address = new Address(id, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
            addressRepository.save(connection, address);
        }

        if (price == 0) {
            price = calculatePrice(productQuantity);
        }

        Order order = new Order(orderID, address, orderDate, deliveryDate, price, productQuantity);

        orderRepository.save(connection, order, client);

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
        Order order = getOrderByID(id);

        boolean success = orderRepository.delete(connection, order);
        if (!success) {
            return null;
        }

        return order;
    }

    public Order updateOrder (Order order, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date orderDate, Date deliveryDate, double price) throws OrderException, DatabaseException {
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

    public Order completeOrder(Order order) {
        if (order == null) {
            return null;
        }

        order.setState(ProcessState.CONFIRMED);

        boolean success = orderRepository.updateState(connection, order);
        if (!success) {
            return null;
        }

        return order;
    }
}
