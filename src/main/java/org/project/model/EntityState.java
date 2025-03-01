package org.project.model;

public enum EntityState {
    ACTIVE,
    INACTIVE;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
