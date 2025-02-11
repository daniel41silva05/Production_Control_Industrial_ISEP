package org.project.data;

import java.sql.Connection;

public interface Persistable {

    boolean save(Connection databaseConnection, Object object);

    boolean delete(Connection databaseConnection, Object object);

    //TODO: não faltará aqui uma operação para obter um objeto da base de dados?
}

