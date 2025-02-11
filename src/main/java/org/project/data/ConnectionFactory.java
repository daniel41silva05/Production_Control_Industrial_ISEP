package org.project.data;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConnectionFactory {

    private static final Logger LOGGER = Logger.getLogger("MainLog");

    private static ConnectionFactory instance = null;

    private final Integer connectionPoolCount = 1;

    private final List<DatabaseConnection> databaseConnectionList =
            new ArrayList<>();

    private Integer connectionPoolRequest = 0;

    private static final String URL = "jdbc:postgresql://ravenously-equal-anglerfish.data-1.use1.tembo.io:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Nn2E8HKxGMXDj5fT";

    private ConnectionFactory() {
    }

    public static synchronized ConnectionFactory getInstance() {
        if (instance == null) {
            instance = new ConnectionFactory();
        }
        return instance;
    }

    public DatabaseConnection getDatabaseConnection() {
        DatabaseConnection databaseConnection;
        if (++connectionPoolRequest > connectionPoolCount) {
            connectionPoolRequest = 1;
        }
        if (connectionPoolRequest > databaseConnectionList.size()) {
            databaseConnection =
                    new DatabaseConnection(URL, USER, PASSWORD);
            databaseConnectionList.add(databaseConnection);
        } else {
            databaseConnection =
                    databaseConnectionList.get(connectionPoolRequest - 1);
        }
        return databaseConnection;
    }
}