package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.domain.ProductCategory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryRepository {

    public boolean save(DatabaseConnection connection, ProductCategory category) {
        String sql = "INSERT INTO ProductCategory (ProductCategoryID, Name) VALUES (?, ?)";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, category.getId());
            statement.setString(2, category.getName());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(DatabaseConnection connection, ProductCategory category) {
        String sql = "DELETE FROM ProductCategory WHERE ProductCategoryID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, category.getId());

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ProductCategory> getAll(DatabaseConnection connection) {
        List<ProductCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM ProductCategory";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ProductCategory category = new ProductCategory(
                        resultSet.getInt("productcategoryid"),
                        resultSet.getString("name")
                );
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public ProductCategory getByID (DatabaseConnection connection, int id) {
        ProductCategory category = null;
        String sql = "SELECT * FROM ProductCategory WHERE productcategoryid = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                category = new ProductCategory(
                        resultSet.getInt("productcategoryid"),
                        resultSet.getString("name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    public boolean getCategoryExists(DatabaseConnection connection, int id) {
        String sql = "SELECT COUNT(*) FROM ProductCategory WHERE productcategoryid = ?";
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
