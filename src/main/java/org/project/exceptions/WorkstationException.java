package org.project.exceptions;

public class WorkstationException extends RuntimeException {

    public static final String WORKSTATION_ALREADY_EXISTS = "Workstation with ID %d already exists.";
    public static final String WORKSTATION_TYPE_ALREADY_EXISTS = "Workstation Type with ID %d already exists.";
    public static final String WORKSTATION_NOT_FOUND = "Workstation with ID %d does not exist.";
    public static final String WORKSTATION_TYPE_NOT_FOUND = "Workstation Type with ID %d does not exist.";
    public static final String WORKSTATION_TYPE_CAN_NOT_DELETED = "Workstation Type cannot be deleted because it contains active workstations.";
    public static final String WORKSTATION_OPERATION_NOT_ASSIGNED = "Workstation Type with ID %d is not assigned to perform Operation Type with ID %d";

    public WorkstationException(String message) {
        super(message);
    }

    public static WorkstationException workstationAlreadyExists(int id) {
        return new WorkstationException(String.format(WORKSTATION_ALREADY_EXISTS, id));
    }

    public static WorkstationException workstationTypeAlreadyExists(int id) {
        return new WorkstationException(String.format(WORKSTATION_TYPE_ALREADY_EXISTS, id));
    }

    public static WorkstationException workstationNotFound(int id) {
        return new WorkstationException(String.format(WORKSTATION_NOT_FOUND, id));
    }

    public static WorkstationException workstationTypeNotFound(int id) {
        return new WorkstationException(String.format(WORKSTATION_TYPE_NOT_FOUND, id));
    }

    public static WorkstationException workstationTypeCanNotDeleted() {
        return new WorkstationException(String.format(WORKSTATION_TYPE_CAN_NOT_DELETED));
    }

    public static WorkstationException workstationOperationNotAssigned(int workstationId, int operationId) {
        return new WorkstationException(String.format(WORKSTATION_OPERATION_NOT_ASSIGNED, workstationId, operationId));
    }
}
