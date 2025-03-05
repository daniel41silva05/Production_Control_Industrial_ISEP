package org.project.exceptions;

public class DatabaseException extends Exception {

    public static final String DATABASE_ERROR = "Database issues, please try again later.";

    public DatabaseException(String message) {
        super(message);
    }

    public static DatabaseException databaseError() {
        return new DatabaseException(String.format(DATABASE_ERROR));
    }

}
