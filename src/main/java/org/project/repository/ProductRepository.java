package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.domain.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public boolean updateCategory(DatabaseConnection connection, Product product) {
        String sql = "UPDATE Product SET CategoryID = ? WHERE ProductID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, product.getCategory().getId());
            statement.setString(2, product.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
