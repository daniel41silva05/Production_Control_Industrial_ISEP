package org.project.model;

import java.util.Objects;

public class WorkstationType {

    private int id;
    private String name;
    private Workstation workstation;

    public WorkstationType(int id, String name, Workstation workstation) {
        this.id = id;
        this.name = name;
        this.workstation = workstation;
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

    public Workstation getWorkstation() {
        return workstation;
    }

    public void setWorkstation(Workstation workstation) {
        this.workstation = workstation;
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
                ", workstation=" + workstation +
                '}';
    }
}
