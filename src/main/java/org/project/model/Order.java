package org.project.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Order {

    private int id;
    private Address deliveryAddress;
    private Date orderDate;
    private Date deliveryDate;
    private double price;
    private ProcessState state;
    private Map<Product, Integer> productQuantity;

    public Order(int id, Address deliveryAddress, Date orderDate, Date deliveryDate, double price, ProcessState state) {
        this.id = id;
        this.deliveryAddress = deliveryAddress;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.price = price;
        this.state = state;
        this.productQuantity = new HashMap<>();
    }

    public Order(int id, Address deliveryAddress, Date orderDate, Date deliveryDate, double price, Map<Product, Integer> productQuantity) {
        this.id = id;
        this.deliveryAddress = deliveryAddress;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.price = price;
        this.state = ProcessState.PENDING;
        this.productQuantity = productQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public Map<Product, Integer> getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Map<Product, Integer> productQuantity) {
        this.productQuantity = productQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", deliveryAddress=" + deliveryAddress +
                ", orderDate=" + orderDate +
                ", deliveryDate=" + deliveryDate +
                ", price=" + price +
                ", state=" + state +
                ", productQuantity=" + productQuantity +
                '}';
    }
}
