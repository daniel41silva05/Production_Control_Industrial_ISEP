package org.project.exceptions;

public class ProductException extends RuntimeException {

    public static final String PRODUCT_ALREADY_EXISTS = "Product with ID %s already exists.";
    public static final String CATEGORY_ALREADY_EXISTS = "Product Category with ID %d already exists.";
    public static final String COMPONENT_ALREADY_EXISTS = "Component with ID %s already exists.";
    public static final String RAWMATERIAL_ALREADY_EXISTS = "Raw Material with ID %s already exists.";
    public static final String PART_NOT_FOUND = "Part with ID %s does not exist.";
    public static final String PRODUCT_NOT_FOUND = "Product with ID %s does not exist.";
    public static final String CATEGORY_NOT_FOUND = "Product Category with ID %d does not exist.";
    public static final String RAWMATERIAL_NOT_FOUND = "Raw Material with ID %s does not exist.";
    public static final String PRODUCT_ALREADY_IN_CATEGORY = "Product with ID %s already belongs to category %s.";
    public static final String CATEGORY_DELETION_MESSAGE = "Product Category with ID %d this is what will be deleted.";
    public static final String PRODUCTION_TREE_NOT_FOUND = "Product with ID %s does not have any production tree in the system.";
    public static final String NOT_ENOUGH_STOCK = "There is not enough stock of the raw material id: %s";
    public static final String MINIMUM_STOCK_REMAINS_SAME = "The minimum stock remains the same";
    public static final String RAW_MATERIAL_NO_PRODUCTION_TREE = "Part ID %s - Only the raw materials have nothing to form them in the production tree.";
    public static final String PRODUCT_NO_PRODUCTION_TREE = "Product with ID %s does not have any production tree in the system.";
    public static final String ALL_RAW_MATERIALS_NO_PRODUCTION_TREE = "Part ID %s - All raw materials have nothing to form them in the production tree.";
    public static final String PRODUCT_NOT_IN_CSV = "Product with ID %s does not exist in csv file.";
    public static final String SUPPLIER_ALREADY_REGISTERED = "Supplier with ID %d has already been registered as a supplier of raw material with ID %s";
    public static final String SUPPLIER_NOT_REGISTERED = "Supplier with ID %d was never registered as a supplier of raw material with ID %s";
    public static final String EXPECTED_UNIT_COST_REMAINS_SAME = "The expected unit cost remains the same";

    public ProductException(String message) {
        super(message);
    }

    public static ProductException productAlreadyExists(String id) {
        return new ProductException(String.format(PRODUCT_ALREADY_EXISTS, id));
    }

    public static ProductException categoryAlreadyExists(int id) {
        return new ProductException(String.format(CATEGORY_ALREADY_EXISTS, id));
    }

    public static ProductException componentAlreadyExists(String id) {
        return new ProductException(String.format(COMPONENT_ALREADY_EXISTS, id));
    }

    public static ProductException rawMaterialAlreadyExists(String id) {
        return new ProductException(String.format(RAWMATERIAL_ALREADY_EXISTS, id));
    }

    public static ProductException partNotFound(String id) {
        return new ProductException(String.format(PART_NOT_FOUND, id));
    }

    public static ProductException productNotFound(String id) {
        return new ProductException(String.format(PRODUCT_NOT_FOUND, id));
    }

    public static ProductException categoryNotFound(int id) {
        return new ProductException(String.format(CATEGORY_NOT_FOUND, id));
    }

    public static ProductException rawMaterialNotFound(String id) {
        return new ProductException(String.format(RAWMATERIAL_NOT_FOUND, id));
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

    public static ProductException minimumStockRemainsSame() {
        return new ProductException(String.format(MINIMUM_STOCK_REMAINS_SAME));
    }

    public static ProductException rawMaterialNoProductionTree(String partID) {
        return new ProductException(String.format(RAW_MATERIAL_NO_PRODUCTION_TREE, partID));
    }

    public static ProductException productNoProductionTree(String productID) {
        return new ProductException(String.format(PRODUCT_NO_PRODUCTION_TREE, productID));
    }

    public static ProductException allRawMaterialsNoProductionTree(String partID) {
        return new ProductException(String.format(ALL_RAW_MATERIALS_NO_PRODUCTION_TREE, partID));
    }

    public static ProductException productNotInCsv(String productID) {
        return new ProductException(String.format(PRODUCT_NOT_IN_CSV, productID));
    }

    public static ProductException supplierAlreadyRegistered(int supplierID, String rawMaterialID) {
        return new ProductException(String.format(SUPPLIER_ALREADY_REGISTERED, supplierID, rawMaterialID));
    }

    public static ProductException supplierNotRegistered(int supplierID, String rawMaterialID) {
        return new ProductException(String.format(SUPPLIER_NOT_REGISTERED, supplierID, rawMaterialID));
    }

    public static ProductException expectedUnitCostRemainsSame() {
        return new ProductException(EXPECTED_UNIT_COST_REMAINS_SAME);
    }

}
