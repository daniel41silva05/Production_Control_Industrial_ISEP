package org.project.exceptions;

public class ProductException extends RuntimeException {

    public static final String PRODUCT_NOT_FOUND = "Product with ID %s does not exist.";

    public ProductException(String message) {
        super(message);
    }

    public static ProductException productNotFound(String id) {
        return new ProductException(String.format(PRODUCT_NOT_FOUND, id));
    }

}
