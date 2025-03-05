package org.project.exceptions;

public class OperationException extends RuntimeException {

    public static final String OPERATION_TYPE_NOT_FOUND = "Operation Type with ID %d does not exist.";
    public static final String OPERATION_ERROR = "An error occurred while processing the operation.";

    public OperationException(String message) {
        super(message);
    }

    public static OperationException operationTypeNotFound(int id) {
        return new OperationException(String.format(OPERATION_TYPE_NOT_FOUND, id));
    }

    public static OperationException operationError() {
        return new OperationException(OPERATION_ERROR);
    }
}
