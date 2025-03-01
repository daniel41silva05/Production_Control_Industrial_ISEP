package org.project.model;

public enum ProcessState {
    PENDING,
    CONFIRMED;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
