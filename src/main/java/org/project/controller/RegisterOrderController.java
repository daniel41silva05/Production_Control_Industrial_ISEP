package org.project.controller;

import org.project.domain.Client;
import org.project.domain.Order;
import org.project.exceptions.ClientException;
import org.project.exceptions.OrderException;
import org.project.exceptions.ProductException;
import org.project.service.ClientService;
import org.project.service.OrderService;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class RegisterOrderController {

    private OrderService orderService;
    private ClientService clientService;

    public RegisterOrderController() {
        this.orderService = new OrderService();
        this.clientService = new ClientService();
    }

    public List<Client> getClients() {
        return clientService.getClients();
    }

    public Client getClient(int id) throws ClientException {
        return clientService.getClientByID(id);
    }

    public Order registerOrder(int clientID, int orderID, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date orderDate, Date deliveryDate, int price, Map<String, Double> productIDQuantity) throws ClientException, OrderException, ProductException {
        return orderService.registerOrder(clientID, orderID, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry, orderDate, deliveryDate, price, productIDQuantity);
    }
}
