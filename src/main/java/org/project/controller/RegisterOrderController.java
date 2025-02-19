package org.project.controller;

import org.project.domain.Client;
import org.project.domain.Order;
import org.project.domain.Product;
import org.project.exceptions.ClientException;
import org.project.exceptions.OrderException;
import org.project.exceptions.ProductException;
import org.project.service.ClientService;
import org.project.service.RegisterOrderService;
import org.project.service.RegisterProductService;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class RegisterOrderController {

    private RegisterOrderService orderService;
    private ClientService clientService;
    private RegisterProductService productService;

    public RegisterOrderController() {
        this.orderService = new RegisterOrderService();
        this.clientService = new ClientService();
        this.productService = new RegisterProductService();
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
