package org.project.controller;

import org.project.model.Client;
import org.project.model.Order;
import org.project.model.Product;
import org.project.exceptions.ClientException;
import org.project.exceptions.OrderException;
import org.project.exceptions.ProductException;
import org.project.service.ClientService;
import org.project.service.OrderService;
import org.project.service.ProductService;
import org.project.service.ProductionTreeService;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrderController {

    private OrderService orderService;
    private ClientService clientService;
    private ProductService productService;
    private ProductionTreeService productionTreeService;

    public OrderController() {
        this.orderService = new OrderService();
        this.clientService = new ClientService();
        this.productService = new ProductService();
        this.productionTreeService = new ProductionTreeService();
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

    public Order getOrderByID (int orderID) throws OrderException {
        return orderService.getOrderByID(orderID);
    }

    public Order registerOrder(int clientID, int orderID, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date orderDate, Date deliveryDate, int price, Map<String, Integer> productIDQuantity) throws ClientException, OrderException, ProductException {
        return orderService.registerOrder(clientID, orderID, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry, orderDate, deliveryDate, price, productIDQuantity);
    }

    public Order cancelOrder(int orderID) throws OrderException {
        return orderService.deleteOrder(orderID);
    }

    public Order updateOrder (Order order, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date orderDate, Date deliveryDate, double price) throws OrderException {
        return orderService.updateOrder(order, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry, orderDate, deliveryDate, price);
    }

    public List<Order> consultActiveOrders () {
        return orderService.activeOrders();
    }

    public Order completeOrder(int orderID) throws OrderException, ProductException {
        Order order = orderService.getOrderByID(orderID);
        productionTreeService.discountRawMaterialStock(order);
        return orderService.completeOrder(order);
    }
}
