package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.exceptions.DatabaseException;
import org.project.model.*;

import java.sql.*;
import java.util.*;

public class WorkstationTypeRepository {

    public boolean save(DatabaseConnection connection, WorkstationType workstationType) {
        String sql = "INSERT INTO WorkstationType (WorkstationTypeID, Name) VALUES (?, ?)";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, workstationType.getId());
            statement.setString(2, workstationType.getName());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public boolean delete(DatabaseConnection connection, WorkstationType workstationType) {
        String deleteTypeSql = "DELETE FROM WorkstationType WHERE WorkstationTypeID = ?";
        String deleteCanBeDoneAtSQL = "DELETE FROM CanBeDoneAt WHERE WorkstationTypeID = ?";

        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtCanBeDoneAt = conn.prepareStatement(deleteCanBeDoneAtSQL);
                 PreparedStatement stmtType = conn.prepareStatement(deleteTypeSql)) {

                stmtCanBeDoneAt.setInt(1, workstationType.getId());
                stmtCanBeDoneAt.executeUpdate();

                stmtType.setInt(1, workstationType.getId());
                int rowsDeleted = stmtType.executeUpdate();

                conn.commit();
                return rowsDeleted > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw DatabaseException.databaseError();
            }
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public List<WorkstationType> getAll(DatabaseConnection connection) {
        List<WorkstationType> workstationTypes = new ArrayList<>();
        String sql = """
        SELECT wt.workstationtypeid, wt.name AS workstationtype_name,
               w.workstationid, w.name AS workstation_name
        FROM WorkstationType wt
        LEFT JOIN Workstation w ON wt.workstationtypeid = w.workstationtypeid
        ORDER BY wt.workstationtypeid;
        """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            Map<Integer, WorkstationType> workstationTypeMap = new HashMap<>();

            while (resultSet.next()) {
                int workstationTypeId = resultSet.getInt("workstationtypeid");
                WorkstationType workstationType = workstationTypeMap.get(workstationTypeId);

                if (workstationType == null) {
                    workstationType = new WorkstationType(
                            workstationTypeId,
                            resultSet.getString("workstationtype_name"),
                            new ArrayList<>()
                    );
                    workstationTypeMap.put(workstationTypeId, workstationType);
                }

                int workstationId = resultSet.getInt("workstationid");
                if (!resultSet.wasNull()) {
                    Workstation workstation = new Workstation(
                            workstationId,
                            resultSet.getString("workstation_name")
                    );
                    workstationType.getWorkstations().add(workstation);
                }
            }

            workstationTypes.addAll(workstationTypeMap.values());
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }

        return workstationTypes;
    }

    public WorkstationType getById(DatabaseConnection connection, int workstationTypeId) {
        WorkstationType workstationType = null;
        String sql = """
        SELECT wt.workstationtypeid, wt.name AS workstationtype_name,
               w.workstationid, w.name AS workstation_name
        FROM WorkstationType wt
        LEFT JOIN Workstation w ON wt.workstationtypeid = w.workstationtypeid
        WHERE wt.workstationtypeid = ?
        ORDER BY wt.workstationtypeid;
        """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, workstationTypeId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                if (workstationType == null) {
                    workstationType = new WorkstationType(
                            resultSet.getInt("workstationtypeid"),
                            resultSet.getString("workstationtype_name"),
                            new ArrayList<>()
                    );
                }

                int workstationId = resultSet.getInt("workstationid");
                if (!resultSet.wasNull()) {
                    Workstation workstation = new Workstation(
                            workstationId,
                            resultSet.getString("workstation_name")
                    );
                    workstationType.getWorkstations().add(workstation);
                }
            }

        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }

        return workstationType;
    }

    public boolean getWorkstationTypeExists(DatabaseConnection connection, int id) {
        String sql = "SELECT COUNT(*) FROM WorkstationType WHERE WorkstationTypeID = ?";
        int count = 0;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);

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

    public boolean saveOperationWorkstationTime(DatabaseConnection connection, OperationType operationType, WorkstationType workstationType) {
        String sql = "INSERT INTO CanBeDoneAt (OperationTypeID, WorkstationTypeID, SetupTime) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, operationType.getId());
            statement.setInt(2, workstationType.getId());
            statement.setInt(3, operationType.getWorkstationSetupTime().get(workstationType));

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public boolean updateOperationWorkstationTime(DatabaseConnection connection, OperationType operationType, WorkstationType workstationType) {
        String sql = "UPDATE CanBeDoneAt SET SetupTime = ? WHERE OperationTypeID = ? AND WorkstationTypeID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, operationType.getWorkstationSetupTime().get(workstationType));
            statement.setInt(2, operationType.getId());
            statement.setInt(3, workstationType.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

}