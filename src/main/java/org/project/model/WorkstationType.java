package org.project.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorkstationType {

    private int id;
    private String name;
    private List<Workstation> workstations;

    public WorkstationType(int id, String name, List<Workstation> workstations) {
        this.id = id;
        this.name = name;
        this.workstations = workstations;
    }

    public WorkstationType(int id, String name) {
        this.id = id;
        this.name = name;
        this.workstations = new ArrayList<>();
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

    public List<Workstation> getWorkstations() {
        return workstations;
    }

    public void setWorkstations(List<Workstation> workstations) {
        this.workstations = workstations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkstationType that = (WorkstationType) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "WorkstationType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", workstations=" + workstations +
                '}';
    }

}
