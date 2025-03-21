package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.exceptions.DatabaseException;
import org.project.model.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComponentRepository {

    public boolean saveComponent(DatabaseConnection connection, Component component) {
        String insertPartSQL = """
            INSERT INTO Part (PartID, Name, Description)
            VALUES (?, ?, ?)
        """;
        String insertProductSQL = """
            INSERT INTO Component (ComponentID)
            VALUES (?)
        """;

        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtPart = conn.prepareStatement(insertPartSQL);
                 PreparedStatement stmtProduct = conn.prepareStatement(insertProductSQL)) {

                stmtPart.setString(1, component.getId());
                stmtPart.setString(2, component.getName());
                stmtPart.setString(3, component.getDescription());
                int rowsInsertedPart = stmtPart.executeUpdate();

                if (rowsInsertedPart > 0) {
                    stmtProduct.setString(1, component.getId());
                    int rowsInsertedProduct = stmtProduct.executeUpdate();

                    if (rowsInsertedProduct > 0) {
                        conn.commit();
                        return true;
                    }
                }

                conn.rollback();
                throw DatabaseException.databaseError();
            } catch (SQLException e) {
                conn.rollback();
                throw DatabaseException.databaseError();
            }
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public List<Component> getAllComponents(DatabaseConnection connection) {
        List<Component> components = new ArrayList<>();
        String sql = """
        SELECT part.PartID, part.Name AS PartName, part.Description AS PartDescription
        FROM Component c
        JOIN Part part ON c.ComponentID = part.PartID;
    """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                components.add(new Component(
                        rs.getString("PartID"),
                        rs.getString("PartName"),
                        rs.getString("PartDescription")
                ));
            }
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
        return components;
    }

    public Component getComponentByID(DatabaseConnection connection, String id) {
        Component component = null;
        String sql = """
            SELECT part.PartID, part.Name AS PartName, part.Description AS PartDescription
            FROM Component c
            JOIN Part part ON c.ComponentID = part.PartID
            WHERE c.ComponentID = ?;
        """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {

                component = new Component(
                        rs.getString("PartID"),
                        rs.getString("PartName"),
                        rs.getString("PartDescription")
                );

            }
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
        return component;
    }

    public boolean getComponentExists(DatabaseConnection connection, String id) {
        String sql = "SELECT COUNT(*) FROM Component WHERE ComponentID = ?";
        int count = 0;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }

        return count > 0;
    }

}
