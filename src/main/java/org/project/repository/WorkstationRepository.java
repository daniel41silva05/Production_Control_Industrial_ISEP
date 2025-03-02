package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkstationRepository {

    public boolean save(DatabaseConnection connection, Workstation workstation, WorkstationType workstationType) {
        String sql = "INSERT INTO Workstation (WorkstationID, WorkstationTypeID, Name) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, workstation.getId());
            statement.setInt(2, workstationType.getId());
            statement.setString(3, workstation.getName());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(DatabaseConnection connection, Workstation workstation) {
        String sql = "DELETE FROM Workstation WHERE WorkstationID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, workstation.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Workstation> getAll(DatabaseConnection connection) {
        List<Workstation> workstations = new ArrayList<>();
        String sql = """
            SELECT w.workstationid, w.name AS workstation_name
            FROM Workstation w
            ORDER BY w.workstationid;
            """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Workstation workstation = new Workstation(
                        resultSet.getInt("workstationid"),
                        resultSet.getString("workstation_name")
                );

                workstations.add(workstation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workstations;
    }

    public Workstation getById(DatabaseConnection connection, int workstationId) {
        Workstation workstation = null;
        String sql = """
            SELECT w.workstationid, w.name AS workstation_name
            FROM Workstation w
            WHERE w.workstationid = ?
            ORDER BY w.workstationid;
            """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, workstationId); // Define o ID da workstation na consulta
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                workstation = new Workstation(
                        resultSet.getInt("workstationid"),
                        resultSet.getString("workstation_name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workstation;
    }

    public boolean getWorkstationExists(DatabaseConnection connection, int id) {
        String sql = "SELECT COUNT(*) FROM Workstation WHERE WorkstationID = ?";
        int count = 0;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count > 0;
    }
}