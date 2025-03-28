package org.project.model;

import java.util.Objects;

public class Workstation {

    private int id;
    private String name;
    private boolean available;

    public Workstation(int id, String name) {
        this.id = id;
        this.name = name;
        this.available = true;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workstation that = (Workstation) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Workstation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
