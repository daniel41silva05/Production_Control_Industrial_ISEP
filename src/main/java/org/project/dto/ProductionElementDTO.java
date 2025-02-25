package org.project.dto;

import java.util.List;

public class ProductionElementDTO {

    private int operationId;
    private String partId;
    private double quantity;

    public ProductionElementDTO(int operationId, String partId, double quantity) {
        this.operationId = operationId;
        this.partId = partId;
        this.quantity = quantity;
    }

    public int getOperationId() {
        return operationId;
    }

    public String getPartId() {
        return partId;
    }

    public double getQuantity() {
        return quantity;
    }

}
