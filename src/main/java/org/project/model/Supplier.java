package org.project.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Supplier {

    private int id;
    private String name;
    private int phoneNumber;
    private String email;
    private EntityState state;
    private List<SupplyOffer> supplyOffers;

    public Supplier(int id, String name, int phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.state = EntityState.INACTIVE;
        this.supplyOffers = new ArrayList<>();
    }

    public Supplier(int id, String name, int phoneNumber, String email, EntityState state) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.state = state;
        this.supplyOffers = new ArrayList<>();
    }

    public Supplier(int id, String name, int phoneNumber, String email, EntityState state, List<SupplyOffer> supplyOffers) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.state = state;
        this.supplyOffers = supplyOffers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public EntityState getState() {
        return state;
    }

    public void setState(EntityState state) {
        this.state = state;
    }

    public List<SupplyOffer> getSupplyOffers() {
        return supplyOffers;
    }

    public void setSupplyOffers(List<SupplyOffer> supplyOffers) {
        this.supplyOffers = supplyOffers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return id == supplier.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", email='" + email + '\'' +
                ", state=" + state +
                ", supplyOffers=" + supplyOffers +
                '}';
    }
}
