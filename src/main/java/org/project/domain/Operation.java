package org.project.domain;

import java.util.Objects;

public class Operation {

    private int id;
    private OperationType type;
    private String name;
    private int executionTime;

    public Operation(int id, OperationType type, String name, int executionTime) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.executionTime = executionTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
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
        Operation operation = (Operation) o;
        return id == operation.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", executionTime=" + executionTime +
                '}';
    }
}
