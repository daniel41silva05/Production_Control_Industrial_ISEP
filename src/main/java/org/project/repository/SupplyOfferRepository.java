package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.exceptions.DatabaseException;
import org.project.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplyOfferRepository {

    public boolean save(DatabaseConnection connection, SupplyOffer supplyOffer, Supplier supplier) {
        String insertSupplyOfferSQL = "INSERT INTO SupplyOffer (SupplyOfferID, SupplierID, DeliveryAddressID, StartDate, EndDate, State) VALUES (?, ?, ?, ?, ?, ?)";
        String insertRawMaterialSupplySQL = "INSERT INTO RawMaterialSupply (SupplyOfferID, RawMaterialID, Quantity, UnitCost) VALUES (?, ?, ?, ?)";

        try (PreparedStatement supplyOfferStmt = connection.getConnection().prepareStatement(insertSupplyOfferSQL);
             PreparedStatement rawMaterialSupplyStmt = connection.getConnection().prepareStatement(insertRawMaterialSupplySQL)) {

            supplyOfferStmt.setInt(1, supplyOffer.getId());
            supplyOfferStmt.setInt(2, supplier.getId());
            supplyOfferStmt.setInt(3, supplyOffer.getDeliveryAddress().getId());
            supplyOfferStmt.setDate(4, new java.sql.Date(supplyOffer.getStartDate().getTime()));
            supplyOfferStmt.setDate(5, supplyOffer.getEndDate() != null ? new java.sql.Date(supplyOffer.getEndDate().getTime()) : null);
            supplyOfferStmt.setString(6, supplyOffer.getState().toString());

            int rowsInserted = supplyOfferStmt.executeUpdate();

            for (Map.Entry<RawMaterial, Map<Integer, Double>> entry : supplyOffer.getRawMaterialsQuantityCost().entrySet()) {
                RawMaterial rawMaterial = entry.getKey();
                Map<Integer, Double> quantityCost = entry.getValue();

                for (Map.Entry<Integer, Double> qcEntry : quantityCost.entrySet()) {
                    rawMaterialSupplyStmt.setInt(1, supplyOffer.getId());
                    rawMaterialSupplyStmt.setString(2, rawMaterial.getId());
                    rawMaterialSupplyStmt.setInt(3, qcEntry.getKey());
                    rawMaterialSupplyStmt.setDouble(4, qcEntry.getValue());
                    rawMaterialSupplyStmt.executeUpdate();
                }
            }
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public boolean delete(DatabaseConnection connection, SupplyOffer supplyOffer) {
        String deleteRawMaterialSupplySQL = "DELETE FROM RawMaterialSupply WHERE SupplyOfferID = ?;";
        String deleteSupplyOfferSQL = "DELETE FROM SupplyOffer WHERE SupplyOfferID = ?;";

        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtRawMaterialSupply = conn.prepareStatement(deleteRawMaterialSupplySQL);
                 PreparedStatement stmtSupplyOffer = conn.prepareStatement(deleteSupplyOfferSQL)) {

                stmtRawMaterialSupply.setInt(1, supplyOffer.getId());
                stmtRawMaterialSupply.executeUpdate();

                stmtSupplyOffer.setInt(1, supplyOffer.getId());
                int rowsDeleted = stmtSupplyOffer.executeUpdate();

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

    public boolean update(DatabaseConnection connection, SupplyOffer supplyOffer) {
        String sql = "UPDATE SupplyOffer SET DeliveryAddressID = ?, StartDate = ?, EndDate = ? WHERE SupplyOfferID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, supplyOffer.getDeliveryAddress().getId());
            statement.setDate(2, new java.sql.Date(supplyOffer.getStartDate().getTime()));
            statement.setDate(3, supplyOffer.getEndDate() != null ? new java.sql.Date(supplyOffer.getEndDate().getTime()) : null);
            statement.setInt(4, supplyOffer.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public boolean updateState(DatabaseConnection connection, SupplyOffer supplyOffer) {
        String sql = "UPDATE SupplyOffer SET State = ? WHERE SupplyOfferID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, supplyOffer.getState().toString());
            statement.setInt(2, supplyOffer.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public List<SupplyOffer> getAll(DatabaseConnection connection) {
        List<SupplyOffer> supplyOffers = new ArrayList<>();
        String sql = """
        SELECT so.SupplyOfferID, so.SupplierID, so.DeliveryAddressID, so.StartDate, so.EndDate, so.State,
               a.AddressID, a.Street, a.ZipCode, a.Town, a.Country,
               rms.RawMaterialID, rms.Quantity, rms.UnitCost,
               rm.RawMaterialID, rm.CurrentStock, rm.MinimumStock,
               p.PartID, p.Name AS PartName, p.Description AS PartDescription
        FROM SupplyOffer so
        JOIN Address a ON so.DeliveryAddressID = a.AddressID
        LEFT JOIN RawMaterialSupply rms ON so.SupplyOfferID = rms.SupplyOfferID
        LEFT JOIN RawMaterial rm ON rms.RawMaterialID = rm.RawMaterialID
        LEFT JOIN Part p ON rm.RawMaterialID = p.PartID
        ORDER BY so.SupplyOfferID;
    """;

        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Map<Integer, SupplyOffer> supplyOfferMap = new HashMap<>();

            while (rs.next()) {
                int supplyOfferId = rs.getInt("SupplyOfferID");
                SupplyOffer supplyOffer = supplyOfferMap.get(supplyOfferId);

                if (supplyOffer == null) {
                    Address address = new Address(
                            rs.getInt("DeliveryAddressID"),
                            rs.getString("Street"),
                            rs.getString("ZipCode"),
                            rs.getString("Town"),
                            rs.getString("Country")
                    );

                    supplyOffer = new SupplyOffer(
                            supplyOfferId,
                            address,
                            rs.getDate("StartDate"),
                            rs.getDate("EndDate"),
                            ProcessState.valueOf(rs.getString("State").toUpperCase()),
                            new HashMap<>()
                    );
                    supplyOfferMap.put(supplyOfferId, supplyOffer);
                }

                if (rs.getObject("RawMaterialID") != null) {

                    RawMaterial rawMaterial = new RawMaterial(
                            rs.getString("RawMaterialID"),
                            rs.getString("PartName"),
                            rs.getString("PartDescription"),
                            rs.getInt("CurrentStock"),
                            rs.getInt("MinimumStock")
                    );

                    int quantity = rs.getInt("Quantity");
                    double unitCost = rs.getDouble("UnitCost");

                    supplyOffer.getRawMaterialsQuantityCost()
                            .computeIfAbsent(rawMaterial, k -> new HashMap<>())
                            .put(quantity, unitCost);
                }
            }
            supplyOffers.addAll(supplyOfferMap.values());
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
        return supplyOffers;
    }

    public SupplyOffer getByID(DatabaseConnection connection, int id) {
        String sql = """
        SELECT so.SupplyOfferID, so.SupplierID, so.DeliveryAddressID, so.StartDate, so.EndDate, so.State,
               a.AddressID, a.Street, a.ZipCode, a.Town, a.Country,
               rms.RawMaterialID, rms.Quantity, rms.UnitCost,
               rm.RawMaterialID, rm.CurrentStock, rm.MinimumStock,
               p.PartID, p.Name AS PartName, p.Description AS PartDescription
        FROM SupplyOffer so
        JOIN Address a ON so.DeliveryAddressID = a.AddressID
        LEFT JOIN RawMaterialSupply rms ON so.SupplyOfferID = rms.SupplyOfferID
        LEFT JOIN RawMaterial rm ON rms.RawMaterialID = rm.RawMaterialID
        LEFT JOIN Part p ON rm.RawMaterialID = p.PartID
        WHERE so.SupplyOfferID = ?;
    """;

        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                SupplyOffer supplyOffer = null;

                while (rs.next()) {
                    if (supplyOffer == null) {
                        Address address = new Address(
                                rs.getInt("DeliveryAddressID"),
                                rs.getString("Street"),
                                rs.getString("ZipCode"),
                                rs.getString("Town"),
                                rs.getString("Country")
                        );

                        supplyOffer = new SupplyOffer(
                                id,
                                address,
                                rs.getDate("StartDate"),
                                rs.getDate("EndDate"),
                                ProcessState.valueOf(rs.getString("State").toUpperCase()),
                                new HashMap<>()
                        );
                    }

                    if (rs.getObject("RawMaterialID") != null) {

                        RawMaterial rawMaterial = new RawMaterial(
                                rs.getString("RawMaterialID"),
                                rs.getString("PartName"),
                                rs.getString("PartDescription"),
                                rs.getInt("CurrentStock"),
                                rs.getInt("MinimumStock")
                        );

                        int quantity = rs.getInt("Quantity");
                        double unitCost = rs.getDouble("UnitCost");

                        supplyOffer.getRawMaterialsQuantityCost()
                                .computeIfAbsent(rawMaterial, k -> new HashMap<>())
                                .put(quantity, unitCost);
                    }
                }
                return supplyOffer;
            }
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public boolean getSupplyOfferExists(DatabaseConnection connection, int id) {
        String sql = "SELECT COUNT(*) FROM SupplyOffer WHERE SupplyOfferID = ?";
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