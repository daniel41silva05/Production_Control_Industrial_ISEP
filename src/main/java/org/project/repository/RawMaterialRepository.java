package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.exceptions.DatabaseException;
import org.project.model.EntityState;
import org.project.model.RawMaterial;
import org.project.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RawMaterialRepository {

    public boolean saveRawMaterial(DatabaseConnection connection, RawMaterial rawMaterial) {
        String insertPartSQL = """
            INSERT INTO Part (PartID, Name, Description)
            VALUES (?, ?, ?)
        """;
        String insertProductSQL = """
            INSERT INTO RawMaterial (RawMaterialID, CurrentStock, MinimumStock)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtPart = conn.prepareStatement(insertPartSQL);
                 PreparedStatement stmtProduct = conn.prepareStatement(insertProductSQL)) {

                stmtPart.setString(1, rawMaterial.getId());
                stmtPart.setString(2, rawMaterial.getName());
                stmtPart.setString(3, rawMaterial.getDescription());
                int rowsInsertedPart = stmtPart.executeUpdate();

                if (rowsInsertedPart > 0) {
                    stmtProduct.setString(1, rawMaterial.getId());
                    stmtProduct.setInt(2, rawMaterial.getCurrentStock());
                    stmtProduct.setInt(3, rawMaterial.getMinimumStock());
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

    public List<RawMaterial> getAllRawMaterials(DatabaseConnection connection) {
        List<RawMaterial> rawMaterials = new ArrayList<>();
        String sql = """
        SELECT r.CurrentStock, r.MinimumStock,
               part.PartID, part.Name AS PartName, part.Description AS PartDescription,
               s.SupplierID, s.Name AS SupplierName, s.PhoneNumber, s.EmailAddress, s.State,
               rms.ExpectedUnitCost
        FROM RawMaterial r
        JOIN Part part ON r.RawMaterialID = part.PartID
        LEFT JOIN RawMaterialSupplier rms ON r.RawMaterialID = rms.RawMaterialID
        LEFT JOIN Supplier s ON rms.SupplierID = s.SupplierID;
    """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            Map<String, RawMaterial> rawMaterialMap = new HashMap<>();

            while (rs.next()) {
                String partID = rs.getString("PartID");

                RawMaterial rawMaterial = rawMaterialMap.get(partID);
                if (rawMaterial == null) {
                    rawMaterial = new RawMaterial(
                            partID,
                            rs.getString("PartName"),
                            rs.getString("PartDescription"),
                            rs.getInt("CurrentStock"),
                            rs.getInt("MinimumStock"),
                            new HashMap<>()
                    );
                    rawMaterialMap.put(partID, rawMaterial);
                }

                if (rs.getObject("SupplierID") != null) {
                    Supplier supplier = new Supplier(
                            rs.getInt("SupplierID"),
                            rs.getString("SupplierName"),
                            rs.getInt("PhoneNumber"),
                            rs.getString("EmailAddress"),
                            EntityState.valueOf(rs.getString("State").toUpperCase())
                    );
                    rawMaterial.getRawMaterialCost().put(supplier, rs.getDouble("ExpectedUnitCost"));
                }
            }

            rawMaterials.addAll(rawMaterialMap.values());
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
        return rawMaterials;
    }

    public RawMaterial getRawMaterialByID(DatabaseConnection connection, String id) {
        RawMaterial rawMaterial = null;
        String sql = """
        SELECT r.CurrentStock, r.MinimumStock,
               part.PartID, part.Name AS PartName, part.Description AS PartDescription,
               s.SupplierID, s.Name AS SupplierName, s.PhoneNumber, s.EmailAddress, s.State,
               rms.ExpectedUnitCost
        FROM RawMaterial r
        JOIN Part part ON r.RawMaterialID = part.PartID
        LEFT JOIN RawMaterialSupplier rms ON r.RawMaterialID = rms.RawMaterialID
        LEFT JOIN Supplier s ON rms.SupplierID = s.SupplierID
        WHERE r.RawMaterialID = ?;
    """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();

            Map<Supplier, Double> rawMaterialCostMap = new HashMap<>();

            while (rs.next()) {
                if (rawMaterial == null) {
                    rawMaterial = new RawMaterial(
                            rs.getString("PartID"),
                            rs.getString("PartName"),
                            rs.getString("PartDescription"),
                            rs.getInt("CurrentStock"),
                            rs.getInt("MinimumStock"),
                            rawMaterialCostMap
                    );
                }

                if (rs.getObject("SupplierID") != null) {
                    Supplier supplier = new Supplier(
                            rs.getInt("SupplierID"),
                            rs.getString("SupplierName"),
                            rs.getInt("PhoneNumber"),
                            rs.getString("EmailAddress"),
                            EntityState.valueOf(rs.getString("State").toUpperCase())
                    );
                    rawMaterialCostMap.put(supplier, rs.getDouble("ExpectedUnitCost"));
                }
            }
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
        return rawMaterial;
    }

    public boolean getRawMaterialExists(DatabaseConnection connection, String id) {
        String sql = "SELECT COUNT(*) FROM RawMaterial WHERE RawMaterialID = ?";
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

    public boolean updateRawMaterial(DatabaseConnection connection, RawMaterial rawMaterial) {
        String sql = "UPDATE RawMaterial SET CurrentStock = ?, MinimumStock = ? WHERE RawMaterialID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, rawMaterial.getCurrentStock());
            statement.setInt(2, rawMaterial.getMinimumStock());
            statement.setString(3, rawMaterial.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public boolean saveRawMaterialSupplier(DatabaseConnection connection, Supplier supplier, RawMaterial rawMaterial, double expectedUnitCost) {
        String sql = "INSERT INTO RawMaterialSupplier (SupplierID, RawMaterialID, ExpectedUnitCost) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, supplier.getId());
            statement.setString(2, rawMaterial.getId());
            statement.setDouble(3, expectedUnitCost);

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public boolean deleteRawMaterialSupplier(DatabaseConnection connection, Supplier supplier, RawMaterial rawMaterial) {
        String sql = "DELETE FROM RawMaterialSupplier WHERE SupplierID = ? AND RawMaterialID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, supplier.getId());
            statement.setString(2, rawMaterial.getId());

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public boolean updateRawMaterialSupplier(DatabaseConnection connection, Supplier supplier, RawMaterial rawMaterial, double newExpectedUnitCost) {
        String sql = "UPDATE RawMaterialSupplier SET ExpectedUnitCost = ? WHERE SupplierID = ? AND RawMaterialID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setDouble(1, newExpectedUnitCost);
            statement.setInt(2, supplier.getId());
            statement.setString(3, rawMaterial.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

}
