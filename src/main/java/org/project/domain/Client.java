package org.project.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Client {

    private int id;
    private Address address;
    private String name;
    private String vatin;
    private int phoneNumber;
    private String email;
    private CompanyType type;
    private State state;
    private List<Order> orders;

    public Client(int id, Address address, String name, String vatin, int phoneNumber, String email, CompanyType type) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.vatin = vatin;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.type = type;
        this.state = State.INACTIVE;
        this.orders = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVatin() {
        return vatin;
    }

    public void setVatin(String vatin) {
        this.vatin = vatin;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CompanyType getType() {
        return type;
    }

    public void setType(CompanyType type) {
        this.type = type;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", address=" + address +
                ", name='" + name + '\'' +
                ", vatin='" + vatin + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", email='" + email + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", orders=" + orders +
                '}';
    }

}
