package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.model.*;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
                return false;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public LinkedHashMap<ProductionElement, Integer> getProductionHierarchy(DatabaseConnection connection, String productId) {
        LinkedHashMap<ProductionElement, Integer> hierarchy = new LinkedHashMap<>();
        String sql = """
        WITH RECURSIVE ProductionHierarchy AS (
            SELECT 
                pt.ProductID,
                pt.OperationID,
                o.Name AS OperationName,
                o.ExecutionTime,
                o.OperationTypeID,
                ot.Name AS OperationTypeName,
                COALESCE(p.ProductID, c.ComponentID, r.RawMaterialID) AS PartID,
                COALESCE(p.Name, c.Name, r.Name) AS PartName,
                COALESCE(p.Description, c.Description, r.Description) AS PartDescription,
                pt.ParentOperationID,
                pt.Quantity,
                CASE 
                    WHEN p.ProductID IS NOT NULL THEN 'Product'
                    WHEN c.ComponentID IS NOT NULL THEN 'Component'
                    WHEN r.RawMaterialID IS NOT NULL THEN 'RawMaterial'
                END AS PartType
            FROM ProductionTree pt
            JOIN Operation o ON pt.OperationID = o.OperationID
            JOIN OperationType ot ON o.OperationTypeID = ot.OperationTypeID
            LEFT JOIN Product p ON pt.PartID = p.ProductID
            LEFT JOIN Component c ON pt.PartID = c.ComponentID
            LEFT JOIN RawMaterial r ON pt.PartID = r.RawMaterialID
            WHERE pt.ProductID = ?

            UNION ALL

            SELECT 
                pt.ProductID,
                pt.OperationID,
                o.Name AS OperationName,
                o.ExecutionTime,
                o.OperationTypeID,
                ot.Name AS OperationTypeName,
                COALESCE(p.ProductID, c.ComponentID, r.RawMaterialID) AS PartID,
                COALESCE(p.Name, c.Name, r.Name) AS PartName,
                COALESCE(p.Description, c.Description, r.Description) AS PartDescription,
                pt.ParentOperationID,
                pt.Quantity,
                CASE 
                    WHEN p.ProductID IS NOT NULL THEN 'Product'
                    WHEN c.ComponentID IS NOT NULL THEN 'Component'
                    WHEN r.RawMaterialID IS NOT NULL THEN 'RawMaterial'
                END AS PartType
            FROM ProductionTree pt
            JOIN Operation o ON pt.OperationID = o.OperationID
            JOIN OperationType ot ON o.OperationTypeID = ot.OperationTypeID
            LEFT JOIN Product p ON pt.PartID = p.ProductID
            LEFT JOIN Component c ON pt.PartID = c.ComponentID
            LEFT JOIN RawMaterial r ON pt.PartID = r.RawMaterialID
            JOIN ProductionHierarchy ph ON pt.ParentOperationID = ph.OperationID
        )
        SELECT * FROM ProductionHierarchy;
    """;

        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int operationId = rs.getInt("OperationID");
                    Integer parentOperationId = rs.getInt("ParentOperationID");
                    if (rs.wasNull()) parentOperationId = null;

                    String partType = rs.getString("PartType");

                    OperationType operationType = new OperationType(
                            rs.getInt("OperationTypeID"),
                            rs.getString("OperationTypeName"),
                            new HashMap<>()
                    );

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
                            throw new IllegalStateException("Tipo de parte desconhecido: " + partType);
                    }

                    ProductionElement element = new ProductionElement(part, operation, rs.getDouble("Quantity"));
                    hierarchy.put(element, parentOperationId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }

        return count > 0;
    }

}
