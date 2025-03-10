package org.project.exceptions;

public class ProductException extends RuntimeException {

    public static final String PRODUCT_NOT_FOUND = "Product with ID %s does not exist.";
    public static final String PRODUCTION_TREE_NOT_FOUND = "Product with ID %s does not have any production tree in the system.";
    public static final String NOT_ENOUGH_STOCK = "There is not enough stock of the raw material id: %s";

    public ProductException(String message) {
        super(message);
    }

    public static ProductException productNotFound(String id) {
        return new ProductException(String.format(PRODUCT_NOT_FOUND, id));
    }

    public static ProductException productionTreeNotFound(String id) {
        return new ProductException(String.format(PRODUCTION_TREE_NOT_FOUND, id));
    }

    public static ProductException notEnoughStock(String partId) {
        return new ProductException(String.format(NOT_ENOUGH_STOCK, partId));
    }

}
