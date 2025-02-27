package org.project.dto;

import java.util.Objects;

public class OperationDTO {

    private int id;
    private int typeId;
    private String name;
    private int executionTime;

    public OperationDTO(int id, int typeId, String name, int executionTime) {
        this.id = id;
        this.typeId = typeId;
        this.name = name;
        this.executionTime = executionTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationDTO operation = (OperationDTO) o;
        return id == operation.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
