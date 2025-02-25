package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.domain.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRepository {

    public boolean save(DatabaseConnection connection, Product product) {
        String insertPartSQL = """
            INSERT INTO Part (PartID, Name, Description)
            VALUES (?, ?, ?)
        """;
        String insertProductSQL = """
            INSERT INTO Product (ProductID, CategoryID, Capacity, "Size", Color, Price)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtPart = conn.prepareStatement(insertPartSQL);
                 PreparedStatement stmtProduct = conn.prepareStatement(insertProductSQL)) {

                stmtPart.setString(1, product.getId());
                stmtPart.setString(2, product.getName());
                stmtPart.setString(3, product.getDescription());
                int rowsInsertedPart = stmtPart.executeUpdate();

                if (rowsInsertedPart > 0) {
                    stmtProduct.setString(1, product.getId());
                    stmtProduct.setInt(2, product.getCategory().getId());
                    stmtProduct.setInt(3, product.getCapacity());
                    stmtProduct.setInt(4, product.getSize());
                    stmtProduct.setString(5, product.getColor());
                    stmtProduct.setDouble(6, product.getPrice());
                    int rowsInsertedProduct = stmtProduct.executeUpdate();

                    if (rowsInsertedProduct > 0) {
                        conn.commit();
                        return true;
                    }
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

    public List<Product> getAll(DatabaseConnection connection) {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.ProductID, p.Capacity, p."Size", p.Color, p.Price, 
                   part.PartID, part.Name AS PartName, part.Description AS PartDescription,
                   pc.ProductCategoryID, pc.Name AS ProductCategoryName
            FROM Product p
            JOIN Part part ON p.ProductID = part.PartID
            JOIN ProductCategory pc ON p.CategoryID = pc.ProductCategoryID
        """;

        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                ProductCategory productCategory = new ProductCategory(
                        rs.getInt("ProductCategoryID"),
                        rs.getString("ProductCategoryName")
                );

                Product product = new Product(
                        rs.getString("ProductID"),
                        rs.getString("PartName"),
                        rs.getString("PartDescription"),
                        productCategory,
                        rs.getInt("Capacity"),
                        rs.getInt("Size"),
                        rs.getString("Color"),
                        rs.getDouble("Price")
                );

                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public Product getByID (DatabaseConnection connection, String id) {
        Product product = null;
        String sql = """
            SELECT p.ProductID, p.Capacity, p."Size", p.Color, p.Price, 
                   part.PartID, part.Name AS PartName, part.Description AS PartDescription,
                   pc.ProductCategoryID, pc.Name AS ProductCategoryName
            FROM Product p
            JOIN Part part ON p.ProductID = part.PartID
            JOIN ProductCategory pc ON p.CategoryID = pc.ProductCategoryID
            WHERE p.ProductID = ?;
        """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {

                ProductCategory productCategory = new ProductCategory(
                        rs.getInt("ProductCategoryID"),
                        rs.getString("ProductCategoryName")
                );

                product = new Product(
                        rs.getString("ProductID"),
                        rs.getString("PartName"),
                        rs.getString("PartDescription"),
                        productCategory,
                        rs.getInt("Capacity"),
                        rs.getInt("Size"),
                        rs.getString("Color"),
                        rs.getDouble("Price")
                );

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    public boolean getProductExists(DatabaseConnection connection, String id) {
        String sql = "SELECT COUNT(*) FROM Product WHERE productid = ?";
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

    public HashMap<ProductionElement, Integer> getProductionHierarchy(DatabaseConnection connection, String productId) {
        HashMap<ProductionElement, Integer> hierarchy = new HashMap<>();
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

}
