package org.project.domain;

public enum State {
    ACTIVE,
    INACTIVE;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
