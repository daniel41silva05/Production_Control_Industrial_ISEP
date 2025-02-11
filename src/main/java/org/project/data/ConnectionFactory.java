package org.project.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionFactory {

    private static ConnectionFactory instance = null;
    private final Integer connectionPoolCount = 1; // Tamanho do pool de conexões
    private final List<DatabaseConnection> databaseConnectionList = new ArrayList<>();
    private Integer connectionPoolRequest = 0;

    private ConnectionFactory() {
        // Inicializa o pool de conexões
        for (int i = 0; i < connectionPoolCount; i++) {
            databaseConnectionList.add(new DatabaseConnection());
        }
    }

    public static synchronized ConnectionFactory getInstance() {
        if (instance == null) {
            instance = new ConnectionFactory();
        }
        return instance;
    }

    public Connection getDatabaseConnection() throws SQLException {
        DatabaseConnection databaseConnection = databaseConnectionList.get(connectionPoolRequest);
        connectionPoolRequest = (connectionPoolRequest + 1) % connectionPoolCount; // Round-robin
        return databaseConnection.getConnection();
    }
}