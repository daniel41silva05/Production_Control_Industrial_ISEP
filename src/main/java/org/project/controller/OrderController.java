package org.project.controller;

import org.project.domain.Client;
import org.project.domain.Order;
import org.project.domain.Product;
import org.project.exceptions.ClientException;
import org.project.exceptions.OrderException;
import org.project.exceptions.ProductException;
import org.project.service.ClientService;
import org.project.service.OrderService;
import org.project.service.ProductService;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrderController {

    private OrderService orderService;
    private ClientService clientService;
    private ProductService productService;

    public OrderController() {
        this.orderService = new OrderService();
        this.clientService = new ClientService();
        this.productService = new ProductService();
    }

    public List<Client> getClients() {
        return clientService.getClients();
    }

    public Client getClient(int id) throws ClientException {
        return clientService.getClientByID(id);
    }

    public List<Product> getProducts() {
        return productService.getProducts();
    }

    public Order registerOrder(int clientID, int orderID, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date orderDate, Date deliveryDate, int price, Map<String, Integer> productIDQuantity) throws ClientException, OrderException, ProductException {
        return orderService.registerOrder(clientID, orderID, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry, orderDate, deliveryDate, price, productIDQuantity);
    }
}
