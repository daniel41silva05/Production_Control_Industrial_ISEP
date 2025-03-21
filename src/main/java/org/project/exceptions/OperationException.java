package org.project.exceptions;

public class OperationException extends RuntimeException {

    public static final String OPERATION_ALREADY_EXISTS = "Operation Type with ID %d already exists.";
    public static final String OPERATION_TYPE_ALREADY_EXISTS = "Operation Type with ID %d already exists.";
    public static final String OPERATION_NOT_FOUND = "Operation with ID %d does not exist.";
    public static final String OPERATION_TYPE_NOT_FOUND = "Operation Type with ID %d does not exist.";
    public static final String OPERATION_ERROR = "An error occurred while processing the operation.";
    public static final String OPERATION_NOT_FOUND_IN_PRODUCTION = "Operation %d not found in the production elements.";
    public static final String TIME_REMAINS_SAME = "The execution time remains the same.";

    public OperationException(String message) {
        super(message);
    }

    public static OperationException operationAlreadyExists(int id) {
        return new OperationException(String.format(OPERATION_ALREADY_EXISTS, id));
    }

    public static OperationException operationTypeAlreadyExists(int id) {
        return new OperationException(String.format(OPERATION_TYPE_ALREADY_EXISTS, id));
    }

    public static OperationException operationNotFound(int id) {
        return new OperationException(String.format(OPERATION_NOT_FOUND, id));
    }

    public static OperationException operationTypeNotFound(int id) {
        return new OperationException(String.format(OPERATION_TYPE_NOT_FOUND, id));
    }

    public static OperationException operationError() {
        return new OperationException(OPERATION_ERROR);
    }

    public static OperationException operationNotFoundInProduction(int operation) {
        return new OperationException(String.format(OPERATION_NOT_FOUND_IN_PRODUCTION, operation));
    }

    public static OperationException timeRemainsSame() {
        return new OperationException(String.format(TIME_REMAINS_SAME));
    }
}
