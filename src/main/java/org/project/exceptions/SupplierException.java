package org.project.exceptions;

public class SupplierException extends RuntimeException {

    public static final String SUPPLIER_ALREADY_EXISTS = "Supplier with ID %d already exists.";
    public static final String SUPPLIER_NOT_FOUND = "Supplier with ID %d does not exist.";
    public static final String INVALID_PHONE_NUMBER = "Invalid phone number. It must have 9 digits.";
    public static final String INVALID_EMAIL_FORMAT = "Invalid email format. It should contain something before and after @.";

    public SupplierException(String message) {
        super(message);
    }

    public static SupplierException supplierAlreadyExists(int id) {
        return new SupplierException(String.format(SUPPLIER_ALREADY_EXISTS, id));
    }

    public static SupplierException supplierNotFound(int id) {
        return new SupplierException(String.format(SUPPLIER_NOT_FOUND, id));
    }

    public static SupplierException invalidPhoneNumber() {
        return new SupplierException(INVALID_PHONE_NUMBER);
    }

    public static SupplierException invalidEmailFormat() {
        return new SupplierException(INVALID_EMAIL_FORMAT);
    }

}
