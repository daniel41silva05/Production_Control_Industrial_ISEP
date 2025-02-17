package org.project.repository;

import org.project.data.DatabaseConnection;

import java.util.List;

public interface Persistable<T> {

    boolean save(DatabaseConnection databaseConnection, T object);

    boolean delete(DatabaseConnection databaseConnection, T object);

    List<T> getAll(DatabaseConnection databaseConnection);

}
