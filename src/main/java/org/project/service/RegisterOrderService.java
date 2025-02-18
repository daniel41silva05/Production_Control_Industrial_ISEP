package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.domain.Address;
import org.project.domain.Client;
import org.project.domain.Order;
import org.project.domain.Product;
import org.project.exceptions.ClientException;
import org.project.exceptions.OrderException;
import org.project.exceptions.ProductException;
import org.project.repository.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterOrderService {

    private DatabaseConnection connection;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;
    private AddressRepository addressRepository;

    public RegisterOrderService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        orderRepository = repositories.getOrderRepository();
        productRepository = repositories.getProductRepository();
        clientRepository = repositories.getClientRepository();
        addressRepository = repositories.getAddressRepository();
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
}
