package org.project.exceptions;

public class ClientException extends Exception {

    public static final String CLIENT_ALREADY_EXISTS = "Client with ID %d already exists.";
    public static final String CLIENT_NOT_FOUND = "Client with ID %d does not exist.";

    public ClientException(String message) {
        super(message);
    }

    public static ClientException clientAlreadyExists(int id) {
        return new ClientException(String.format(CLIENT_ALREADY_EXISTS, id));
    }

    public static ClientException clientNotFound(int id) {
        return new ClientException(String.format(CLIENT_NOT_FOUND, id));
    }

}
