package org.project.exceptions;

public class OrderException extends RuntimeException {

    public static final String ORDER_ALREADY_EXISTS = "Order with ID %d already exists.";
    public static final String ORDER_NOT_FOUND = "Order with ID %d does not exist.";
    public static final String INVALID_DELIVERY_DATE = "Delivery date cannot be before Order date.";
    public static final String INVALID_ZIP_CODE = "Invalid zip code. It must follow the format xxxx-xxx.";

    public OrderException(String message) {
        super(message);
    }

    public static OrderException orderAlreadyExists(int orderID) {
        return new OrderException(String.format(ORDER_ALREADY_EXISTS, orderID));
    }

    public static OrderException orderNotFound(int id) {
        return new OrderException(String.format(ORDER_NOT_FOUND, id));
    }

    public static OrderException invalidDeliveryDate() {
        return new OrderException(INVALID_DELIVERY_DATE);
    }

    public static OrderException invalidZipCode() {
        return new OrderException(INVALID_ZIP_CODE);
    }

}
