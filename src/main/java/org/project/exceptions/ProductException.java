package org.project.exceptions;

public class ProductException extends RuntimeException {

    public static final String PRODUCT_ALREADY_EXISTS = "Product with ID %s already exists.";
    public static final String CATEGORY_ALREADY_EXISTS = "Product Category with ID %d already exists.";
    public static final String PRODUCT_NOT_FOUND = "Product with ID %s does not exist.";
    public static final String CATEGORY_NOT_FOUND = "Product Category with ID %d does not exist.";
    public static final String PRODUCT_ALREADY_IN_CATEGORY = "Product with ID %s already belongs to category %s.";
    public static final String CATEGORY_DELETION_MESSAGE = "Product Category with ID %d this is what will be deleted.";
    public static final String PRODUCTION_TREE_NOT_FOUND = "Product with ID %s does not have any production tree in the system.";
    public static final String NOT_ENOUGH_STOCK = "There is not enough stock of the raw material id: %s";

    public ProductException(String message) {
        super(message);
    }

    public static ProductException productAlreadyExists(String id) {
        return new ProductException(String.format(PRODUCT_ALREADY_EXISTS, id));
    }

    public static ProductException categoryAlreadyExists(int id) {
        return new ProductException(String.format(CATEGORY_ALREADY_EXISTS, id));
    }

    public static ProductException productNotFound(String id) {
        return new ProductException(String.format(PRODUCT_NOT_FOUND, id));
    }

    public static ProductException categoryNotFound(int id) {
        return new ProductException(String.format(CATEGORY_NOT_FOUND, id));
    }

    public static ProductException productionTreeNotFound(String id) {
        return new ProductException(String.format(PRODUCTION_TREE_NOT_FOUND, id));
    }

    public static ProductException notEnoughStock(String partId) {
        return new ProductException(String.format(NOT_ENOUGH_STOCK, partId));
    }

    public static ProductException productAlreadyInCategory(String productID, String categoryName) {
        return new ProductException(String.format(PRODUCT_ALREADY_IN_CATEGORY, productID, categoryName));
    }

    public static ProductException categoryDeletionMessage(int categoryID) {
        return new ProductException(String.format(CATEGORY_DELETION_MESSAGE, categoryID));
    }

}
