package org.project.data;

import java.sql.*;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://ravenously-equal-anglerfish.data-1.use1.tembo.io:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Nn2E8HKxGMXDj5fT";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
