package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.exceptions.DatabaseException;
import org.project.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProductionTreeRepository {

    public boolean saveProductionTree(DatabaseConnection connection, String productID, HashMap<ProductionElement, Integer> map) {
        String insertSQL = """
        INSERT INTO ProductionTree (ProductID, OperationID, PartID, ParentOperationID, Quantity)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                for (Map.Entry<ProductionElement, Integer> entry : map.entrySet()) {
                    ProductionElement productionElement = entry.getKey();
                    Integer parentOperationID = entry.getValue();

                    stmt.setString(1, productID);
                    stmt.setInt(2, productionElement.getOperation().getId());
                    stmt.setString(3, productionElement.getPart().getId());

                    if (parentOperationID == null) {
                        stmt.setNull(4, Types.INTEGER);
                    } else {
                        stmt.setInt(4, parentOperationID);
                    }

                    stmt.setDouble(5, productionElement.getQuantity());
                    stmt.addBatch();
                }

                int[] rowsInserted = stmt.executeBatch();

                if (Arrays.stream(rowsInserted).allMatch(row -> row > 0)) {
                    conn.commit();
                    return true;
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

    public HashMap<ProductionElement, Integer> getProductionHierarchy(DatabaseConnection connection, String productId) {
        HashMap<ProductionElement, Integer> hierarchy = new HashMap<>();
        String sql = """
    SELECT 
        pt.OperationID,
        o.Name AS OperationName,
        o.ExecutionTime,
        o.OperationTypeID,
        ot.Name AS OperationTypeName,
        pt.PartID,
        p.Name AS PartName,
        p.Description AS PartDescription,
        pt.ParentOperationID,
        pt.Quantity,
        CASE 
            WHEN EXISTS (SELECT 1 FROM Product WHERE ProductID = pt.PartID) THEN 'Product'
            WHEN EXISTS (SELECT 1 FROM Component WHERE ComponentID = pt.PartID) THEN 'Component'
            WHEN EXISTS (SELECT 1 FROM RawMaterial WHERE RawMaterialID = pt.PartID) THEN 'RawMaterial'
        END AS PartType
    FROM ProductionTree pt
    JOIN Operation o ON pt.OperationID = o.OperationID
    JOIN OperationType ot ON o.OperationTypeID = ot.OperationTypeID
    JOIN Part p ON pt.PartID = p.PartID
    WHERE pt.ProductID = ?
    """;

        Map<Integer, OperationType> operationTypeMap = new HashMap<>();
        String operationTypeSql = """
        SELECT ot.operationtypeid, ot.name AS operationtype_name,
               wt.workstationtypeid, wt.name AS workstationtype_name,
               w.workstationid, w.name AS workstation_name,
               cba.setuptime
        FROM OperationType ot
        LEFT JOIN CanBeDoneAt cba ON ot.operationtypeid = cba.operationtypeid
        LEFT JOIN WorkstationType wt ON cba.workstationtypeid = wt.workstationtypeid
        LEFT JOIN Workstation w ON wt.workstationtypeid = w.workstationtypeid
        WHERE ot.operationtypeid IN (
            SELECT DISTINCT o.OperationTypeID 
            FROM ProductionTree pt
            JOIN Operation o ON pt.OperationID = o.OperationID
            WHERE pt.ProductID = ?
        )
        ORDER BY ot.operationtypeid;
        """;

        try (PreparedStatement operationTypeStmt = connection.getConnection().prepareStatement(operationTypeSql)) {
            operationTypeStmt.setString(1, productId);
            try (ResultSet operationTypeRs = operationTypeStmt.executeQuery()) {
                Map<Integer, WorkstationType> workstationTypeMap = new HashMap<>();

                while (operationTypeRs.next()) {
                    int operationTypeId = operationTypeRs.getInt("operationtypeid");
                    OperationType operationType = operationTypeMap.get(operationTypeId);

                    if (operationType == null) {
                        operationType = new OperationType(
                                operationTypeId,
                                operationTypeRs.getString("operationtype_name"),
                                new HashMap<>()
                        );
                        operationTypeMap.put(operationTypeId, operationType);
                    }

                    int workstationTypeId = operationTypeRs.getInt("workstationtypeid");
                    if (!operationTypeRs.wasNull()) {
                        WorkstationType workstationType = workstationTypeMap.get(workstationTypeId);

                        if (workstationType == null) {
                            workstationType = new WorkstationType(
                                    workstationTypeId,
                                    operationTypeRs.getString("workstationtype_name"),
                                    new ArrayList<>()
                            );
                            workstationTypeMap.put(workstationTypeId, workstationType);
                        }

                        int workstationId = operationTypeRs.getInt("workstationid");
                        if (!operationTypeRs.wasNull()) {
                            Workstation workstation = new Workstation(
                                    workstationId,
                                    operationTypeRs.getString("workstation_name")
                            );
                            workstationType.getWorkstations().add(workstation);
                        }

                        int setupTime = operationTypeRs.getInt("setuptime");
                        operationType.getWorkstationSetupTime().put(workstationType, setupTime);
                    }
                }
            }
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }

        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int operationId = rs.getInt("OperationID");
                    Integer parentOperationId = rs.getInt("ParentOperationID");
                    if (rs.wasNull()) parentOperationId = null;

                    String partType = rs.getString("PartType");

                    int operationTypeId = rs.getInt("OperationTypeID");
                    OperationType operationType = operationTypeMap.get(operationTypeId);

                    if (operationType == null) {
                        operationType = new OperationType(
                                operationTypeId,
                                rs.getString("OperationTypeName"),
                                new HashMap<>()
                        );
                    }

                    Operation operation = new Operation(
                            operationId,
                            operationType,
                            rs.getString("OperationName"),
                            rs.getInt("ExecutionTime")
                    );

                    Part part;
                    String partId = rs.getString("PartID");
                    String partName = rs.getString("PartName");
                    String partDescription = rs.getString("PartDescription");

                    switch (partType) {
                        case "Product":
                            part = new Product(partId, partName, partDescription);
                            break;
                        case "Component":
                            part = new Component(partId, partName, partDescription);
                            break;
                        case "RawMaterial":
                            part = new RawMaterial(partId, partName, partDescription);
                            break;
                        default:
                            throw new IllegalStateException("Unknown part type: " + partType);
                    }

                    ProductionElement element = new ProductionElement(part, operation, rs.getDouble("Quantity"));
                    hierarchy.put(element, parentOperationId);
                }
            }
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }

        return hierarchy;
    }

    public boolean getProductionTreeExists(DatabaseConnection connection, String id) {
        String sql = "SELECT COUNT(*) FROM ProductionTree WHERE ProductID = ?";
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