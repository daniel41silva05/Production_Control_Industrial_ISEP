package org.project.domain;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class SupplyOffer {

    private int id;
    private Address deliveryAddress;
    private Date startDate;
    private Date endDate;
    private Map<RawMaterial, Map<Integer, Double>> rawMaterialsQuantityCost;

    public SupplyOffer(int id, Address deliveryAddress, Date startDate, Date endDate, Map<RawMaterial, Map<Integer, Double>> rawMaterialsQuantityCost) {
        this.id = id;
        this.deliveryAddress = deliveryAddress;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rawMaterialsQuantityCost = rawMaterialsQuantityCost;
    }

    public SupplyOffer(int id, Address deliveryAddress, Date startDate, Map<RawMaterial, Map<Integer, Double>> rawMaterialsQuantityCost) {
        this.id = id;
        this.deliveryAddress = deliveryAddress;
        this.startDate = startDate;
        this.endDate = null;
        this.rawMaterialsQuantityCost = rawMaterialsQuantityCost;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Map<RawMaterial, Map<Integer, Double>> getRawMaterialsQuantityCost() {
        return rawMaterialsQuantityCost;
    }

    public void setRawMaterialsQuantityCost(Map<RawMaterial, Map<Integer, Double>> rawMaterialsQuantityCost) {
        this.rawMaterialsQuantityCost = rawMaterialsQuantityCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplyOffer that = (SupplyOffer) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SupplyOffer{" +
                "id=" + id +
                ", deliveryAddress=" + deliveryAddress +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", rawMaterials=" + rawMaterialsQuantityCost +
                '}';
    }
}
