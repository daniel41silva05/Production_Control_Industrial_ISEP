package org.project.controller;

import org.project.model.Client;
import org.project.model.Order;
import org.project.model.Product;
import org.project.service.OrderService;
import org.project.service.ProductService;
import org.project.service.ProductionTreeService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderController {

    private OrderService orderService;
    private ProductService productService;
    private ProductionTreeService productionTreeService;

    public OrderController() {
        this.orderService = new OrderService();
        this.productService = new ProductService();
        this.productionTreeService = new ProductionTreeService();
    }

    public Order getOrderByID (int orderID) {
        return orderService.getOrderByID(orderID);
    }

    public Order registerOrder(Client client, int orderID, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date orderDate, Date deliveryDate, int price, Map<String, Integer> productIDQuantity) {
        Map<Product, Integer> productQuantity = new HashMap<>();
        for (Map.Entry<String, Integer> entry : productIDQuantity.entrySet()) {
            Product product = productService.getProductByID(entry.getKey());
            productQuantity.put(product, entry.getValue());
        }

        return orderService.registerOrder(client, orderID, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry, orderDate, deliveryDate, price, productQuantity);
    }

    public Order cancelOrder(int orderID) {
        return orderService.deleteOrder(orderID);
    }

    public Order updateOrder (Order order, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date orderDate, Date deliveryDate, double price) {
        return orderService.updateOrder(order, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry, orderDate, deliveryDate, price);
    }

    public List<Order> consultActiveOrders () {
        return orderService.activeOrders();
    }

    public Order completeOrder(int orderID) {
        Order order = orderService.getOrderByID(orderID);
        productionTreeService.discountRawMaterialStock(order);
        return orderService.completeOrder(order);
    }

}
