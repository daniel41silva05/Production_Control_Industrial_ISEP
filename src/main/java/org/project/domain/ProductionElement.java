package org.project.domain;

import java.util.Objects;

public class ProductionElement {

    private Part part;
    private Operation operation;
    private double quantity;

    public ProductionElement(Part part, Operation operation, double quantity) {
        this.part = part;
        this.operation = operation;
        this.quantity = quantity;
    }

    public ProductionElement (Product product) {
        this.part = product;
        this.operation = null;
        this.quantity = 1;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ProductionElement that = (ProductionElement) object;
        return Double.compare(quantity, that.quantity) == 0 && Objects.equals(part, that.part) && Objects.equals(operation, that.operation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(part, operation, quantity);
    }

    @Override
    public String toString() {
        return "ProductionElement{" +
                "part=" + part +
                ", operation=" + operation +
                ", quantity=" + quantity +
                '}';
    }
}
