package org.project.exceptions;

public class ClientException extends RuntimeException {

    public static final String CLIENT_ALREADY_EXISTS = "Client with ID %d already exists.";
    public static final String CLIENT_NOT_FOUND = "Client with ID %d does not exist.";
    public static final String INVALID_PHONE_NUMBER = "Invalid phone number. It must have 9 digits.";
    public static final String INVALID_ZIP_CODE = "Invalid zip code. It must follow the format xxxx-xxx.";
    public static final String INVALID_EMAIL_FORMAT = "Invalid email format. It should contain something before and after @.";

    public ClientException(String message) {
        super(message);
    }

    public static ClientException clientAlreadyExists(int id) {
        return new ClientException(String.format(CLIENT_ALREADY_EXISTS, id));
    }

    public static ClientException clientNotFound(int id) {
        return new ClientException(String.format(CLIENT_NOT_FOUND, id));
    }

    public static ClientException invalidPhoneNumber() {
        return new ClientException(INVALID_PHONE_NUMBER);
    }

    public static ClientException invalidZipCode() {
        return new ClientException(INVALID_ZIP_CODE);
    }

    public static ClientException invalidEmailFormat() {
        return new ClientException(INVALID_EMAIL_FORMAT);
    }

}
