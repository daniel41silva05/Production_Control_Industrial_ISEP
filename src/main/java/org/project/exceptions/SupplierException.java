package org.project.exceptions;

public class SupplierException extends RuntimeException {

    public static final String SUPPLIER_ALREADY_EXISTS = "Supplier with ID %d already exists.";
    public static final String SUPPLIER_NOT_FOUND = "Supplier with ID %d does not exist.";

    public SupplierException(String message) {
        super(message);
    }

    public static SupplierException supplierAlreadyExists(int id) {
        return new SupplierException(String.format(SUPPLIER_ALREADY_EXISTS, id));
    }

    public static SupplierException supplierNotFound(int id) {
        return new SupplierException(String.format(SUPPLIER_NOT_FOUND, id));
    }

}
