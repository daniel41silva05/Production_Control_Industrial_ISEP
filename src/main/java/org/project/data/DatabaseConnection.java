package org.project.data;

import org.postgresql.ds.PGSimpleDataSource;
import org.project.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection implements AutoCloseable {

    private PGSimpleDataSource dataSource;
    private Connection connection;
    private SQLException error;

    public DatabaseConnection(String url, String username, String password) {
        try {
            dataSource = new PGSimpleDataSource();
            dataSource.setUrl(url);
            dataSource.setUser(username);
            dataSource.setPassword(password);

            connection = dataSource.getConnection();

        } catch (SQLException e) {
            DatabaseException.databaseError();
        }
    }

    public Connection getConnection() {
        if (connection == null) {
            throw new RuntimeException("Connection does not exist");
        }
        return connection;
    }

    public void registerError(SQLException error) {
        this.error = error;
    }

    public SQLException getLastError() {
        SQLException lastError = this.error;
        registerError(null);
        return lastError;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
                connection.close();
            } catch (SQLException e) {
                Logger.getLogger(DatabaseConnection.class.getName())
                        .log(Level.SEVERE, "Erro ao fechar conexão", e);
            }
        }
    }
}