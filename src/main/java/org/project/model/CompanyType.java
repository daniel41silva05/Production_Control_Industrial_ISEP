package org.project.model;

public enum CompanyType {
    INDIVIDUAL,
    COMPANY;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
