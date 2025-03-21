package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.exceptions.DatabaseException;
import org.project.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperationRepository {

    public boolean save(DatabaseConnection connection, Operation operation) {

        String sql = "INSERT INTO Operation (OperationID, OperationTypeID, Name, ExecutionTime) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, operation.getId());
            statement.setInt(2, operation.getType().getId());
            statement.setString(3, operation.getName());
            statement.setInt(4, operation.getExecutionTime());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public List<Operation> getAll(DatabaseConnection connection) {
        List<Operation> operations = new ArrayList<>();
        String sql = """
        SELECT o.operationid, o.name AS operation_name, o.executiontime,
               ot.operationtypeid, ot.name AS operationtype_name,
               wt.workstationtypeid, wt.name AS workstationtype_name,
               w.workstationid, w.name AS workstation_name,
               cba.setuptime
        FROM Operation o
        JOIN OperationType ot ON o.operationtypeid = ot.operationtypeid
        LEFT JOIN CanBeDoneAt cba ON ot.operationtypeid = cba.operationtypeid
        LEFT JOIN WorkstationType wt ON cba.workstationtypeid = wt.workstationtypeid
        LEFT JOIN Workstation w ON wt.workstationtypeid = w.workstationtypeid
        ORDER BY o.operationid;
        """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            Map<Integer, Operation> operationMap = new HashMap<>();
            Map<Integer, OperationType> operationTypeMap = new HashMap<>();
            Map<Integer, WorkstationType> workstationTypeMap = new HashMap<>();

            while (resultSet.next()) {
                int operationId = resultSet.getInt("operationid");
                Operation operation = operationMap.get(operationId);

                if (operation == null) {
                    operation = new Operation(
                            operationId,
                            null,
                            resultSet.getString("operation_name"),
                            resultSet.getInt("executiontime")
                    );
                    operationMap.put(operationId, operation);
                }

                int operationTypeId = resultSet.getInt("operationtypeid");
                OperationType operationType = operationTypeMap.get(operationTypeId);

                if (operationType == null) {
                    operationType = new OperationType(
                            operationTypeId,
                            resultSet.getString("operationtype_name"),
                            new HashMap<>()
                    );
                    operationTypeMap.put(operationTypeId, operationType);
                    operation.setType(operationType);
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

            operations.addAll(operationMap.values());
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }

        return operations;
    }

    public Operation getById(DatabaseConnection connection, int operationId) {
        Operation operation = null;
        String sql = """
        SELECT o.operationid, o.name AS operation_name, o.executiontime,
               ot.operationtypeid, ot.name AS operationtype_name,
               wt.workstationtypeid, wt.name AS workstationtype_name,
               w.workstationid, w.name AS workstation_name,
               cba.setuptime
        FROM Operation o
        JOIN OperationType ot ON o.operationtypeid = ot.operationtypeid
        LEFT JOIN CanBeDoneAt cba ON ot.operationtypeid = cba.operationtypeid
        LEFT JOIN WorkstationType wt ON cba.workstationtypeid = wt.workstationtypeid
        LEFT JOIN Workstation w ON wt.workstationtypeid = w.workstationtypeid
        WHERE o.operationid = ?
        ORDER BY o.operationid;
        """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, operationId);
            ResultSet resultSet = statement.executeQuery();

            OperationType operationType = null;
            Map<WorkstationType, Integer> workstationSetupTimeMap = new HashMap<>();
            Map<Integer, WorkstationType> workstationTypeMap = new HashMap<>();

            while (resultSet.next()) {
                if (operation == null) {
                    operation = new Operation(
                            resultSet.getInt("operationid"),
                            null,
                            resultSet.getString("operation_name"),
                            resultSet.getInt("executiontime")
                    );
                }

                if (operationType == null) {
                    operationType = new OperationType(
                            resultSet.getInt("operationtypeid"),
                            resultSet.getString("operationtype_name"),
                            new HashMap<>()
                    );
                    operation.setType(operationType);
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

        return operation;
    }

    public boolean updateOperation(DatabaseConnection connection, Operation operation) {
        String sql = "UPDATE Operation SET Name = ?, OperationTypeID = ?, ExecutionTime = ? WHERE OperationID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, operation.getName());
            statement.setInt(2, operation.getType().getId());
            statement.setInt(3, operation.getExecutionTime());
            statement.setInt(4, operation.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public boolean getOperationExists(DatabaseConnection connection, int id) {
        String sql = "SELECT COUNT(*) FROM Operation WHERE OperationID = ?";
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
