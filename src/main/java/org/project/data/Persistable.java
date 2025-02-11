package org.project.data;

import java.util.List;

public interface Persistable {

    boolean save(DatabaseConnection databaseConnection, Object object);

    boolean delete(DatabaseConnection databaseConnection, Object object);

    List<Object> getAll(DatabaseConnection databaseConnection);
}
