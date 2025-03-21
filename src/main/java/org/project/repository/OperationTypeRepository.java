package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.exceptions.DatabaseException;
import org.project.model.*;

import java.sql.*;
import java.util.*;

public class OperationTypeRepository {

    public boolean save(DatabaseConnection connection, OperationType operationType) {
        String sql = "INSERT INTO OperationType (OperationTypeID, Name) VALUES (?, ?)";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, operationType.getId());
            statement.setString(2, operationType.getName());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public List<OperationType> getAll(DatabaseConnection connection) {
        List<OperationType> operationTypes = new ArrayList<>();
        String sql = """
        SELECT ot.operationtypeid, ot.name AS operationtype_name,
               wt.workstationtypeid, wt.name AS workstationtype_name,
               w.workstationid, w.name AS workstation_name,
               cba.setuptime
        FROM OperationType ot
        LEFT JOIN CanBeDoneAt cba ON ot.operationtypeid = cba.operationtypeid
        LEFT JOIN WorkstationType wt ON cba.workstationtypeid = wt.workstationtypeid
        LEFT JOIN Workstation w ON wt.workstationtypeid = w.workstationtypeid
        ORDER BY ot.operationtypeid;
        """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            Map<Integer, OperationType> operationTypeMap = new HashMap<>();
            Map<Integer, WorkstationType> workstationTypeMap = new HashMap<>();

            while (resultSet.next()) {
                int operationTypeId = resultSet.getInt("operationtypeid");
                OperationType operationType = operationTypeMap.get(operationTypeId);

                if (operationType == null) {
                    operationType = new OperationType(
                            operationTypeId,
                            resultSet.getString("operationtype_name"),
                            new HashMap<>()
                    );
                    operationTypeMap.put(operationTypeId, operationType);
                }

                int workstationTypeId = resultSet.getInt("workstationtypeid");
                if (!resultSet.wasNull()) {
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

                    int setupTime = resultSet.getInt("setuptime");
                    operationType.getWorkstationSetupTime().put(workstationType, setupTime);
                }
            }

            operationTypes.addAll(operationTypeMap.values());
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }

        return operationTypes;
    }

    public OperationType getById(DatabaseConnection connection, int operationTypeId) {
        OperationType operationType = null;
        String sql = """
        SELECT ot.operationtypeid, ot.name AS operationtype_name,
               wt.workstationtypeid, wt.name AS workstationtype_name,
               w.workstationid, w.name AS workstation_name,
               cba.setuptime
        FROM OperationType ot
        LEFT JOIN CanBeDoneAt cba ON ot.operationtypeid = cba.operationtypeid
        LEFT JOIN WorkstationType wt ON cba.workstationtypeid = wt.workstationtypeid
        LEFT JOIN Workstation w ON wt.workstationtypeid = w.workstationtypeid
        WHERE ot.operationtypeid = ?
        ORDER BY ot.operationtypeid;
        """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, operationTypeId); // Define o ID do tipo de operação na consulta
            ResultSet resultSet = statement.executeQuery();

            Map<WorkstationType, Integer> workstationSetupTimeMap = new HashMap<>();
            Map<Integer, WorkstationType> workstationTypeMap = new HashMap<>();

            while (resultSet.next()) {
                if (operationType == null) {
                    operationType = new OperationType(
                            resultSet.getInt("operationtypeid"),
                            resultSet.getString("operationtype_name"),
                            new HashMap<>()
                    );
                }

                int workstationTypeId = resultSet.getInt("workstationtypeid");
                if (!resultSet.wasNull()) {
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

                    int setupTime = resultSet.getInt("setuptime");
                    workstationSetupTimeMap.put(workstationType, setupTime);
                }
            }

            if (operationType != null) {
                operationType.setWorkstationSetupTime(workstationSetupTimeMap);
            }

        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }

        return operationType;
    }

    public boolean getOperationTypeExists(DatabaseConnection connection, int id) {
        String sql = "SELECT COUNT(*) FROM OperationType WHERE OperationTypeID = ?";
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

}