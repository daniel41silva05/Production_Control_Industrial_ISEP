package org.project.domain;

import java.util.Map;
import java.util.Objects;

public class OperationType {

    private int id;
    private String name;
    private Map<WorkstationType, Integer> workstationSetupTime;

    public OperationType(int id, String name, Map<WorkstationType, Integer> workstationSetupTime) {
        this.id = id;
        this.name = name;
        this.workstationSetupTime = workstationSetupTime;
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

    public Map<WorkstationType, Integer> getWorkstationSetupTime() {
        return workstationSetupTime;
    }

    public void setWorkstationSetupTime(Map<WorkstationType, Integer> workstationSetupTime) {
        this.workstationSetupTime = workstationSetupTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationType that = (OperationType) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "OperationType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", workstationSetupTime=" + workstationSetupTime +
                '}';
    }
}
