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

public class SupplierRepository {

    public boolean save(DatabaseConnection connection, Supplier supplier) {
        String sql = "INSERT INTO Supplier (SupplierID, Name, PhoneNumber, EmailAddress, State) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, supplier.getId());
            statement.setString(2, supplier.getName());
            statement.setInt(3, supplier.getPhoneNumber());
            statement.setString(4, supplier.getEmail());
            statement.setString(5, supplier.getState().toString());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public boolean delete(DatabaseConnection connection, Supplier supplier) {
        String deleteRawMaterialSupplySQL = "DELETE FROM RawMaterialSupply WHERE SupplyOfferID = ?;";
        String deleteSupplyOffersSQL = "DELETE FROM SupplyOffer WHERE SupplierID = ?;";
        String deleteSupplierSQL = "DELETE FROM Supplier WHERE SupplierID = ?;";

        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtRawMaterialSupply = conn.prepareStatement(deleteRawMaterialSupplySQL);
                 PreparedStatement stmtSupplyOffers = conn.prepareStatement(deleteSupplyOffersSQL);
                 PreparedStatement stmtSupplier = conn.prepareStatement(deleteSupplierSQL)) {

                for (SupplyOffer offer : supplier.getSupplyOffers()) {
                    stmtRawMaterialSupply.setInt(1, offer.getId());
                    stmtRawMaterialSupply.executeUpdate();
                }

                stmtSupplyOffers.setInt(1, supplier.getId());
                stmtSupplyOffers.executeUpdate();

                stmtSupplier.setInt(1, supplier.getId());
                int rowsDeleted = stmtSupplier.executeUpdate();

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

    public boolean update(DatabaseConnection connection, Supplier supplier) {
        String sql = "UPDATE Supplier SET PhoneNumber = ?, EmailAddress = ? WHERE SupplierID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, supplier.getPhoneNumber());
            statement.setString(2, supplier.getEmail());
            statement.setInt(3, supplier.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public boolean updateStatus(DatabaseConnection connection, Supplier supplier) {
        String sql = "UPDATE Supplier SET State = ? WHERE SupplierID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, supplier.getState().toString());
            statement.setInt(2, supplier.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public List<Supplier> getAll(DatabaseConnection connection) {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = """
    SELECT s.SupplierID, s.Name, s.PhoneNumber, s.EmailAddress, s.State AS supplier_state,
           so.SupplyOfferID, so.DeliveryAddressID, so.StartDate, so.EndDate, so.State AS so_state,
           a.AddressID AS delivery_addressid, a.Street AS delivery_street, a.ZipCode AS delivery_zipcode,
           a.Town AS delivery_town, a.Country AS delivery_country
    FROM Supplier s
    LEFT JOIN SupplyOffer so ON s.SupplierID = so.SupplierID
    LEFT JOIN Address a ON so.DeliveryAddressID = a.AddressID
    ORDER BY s.SupplierID, so.SupplyOfferID;
    """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            Map<Integer, Supplier> supplierMap = new HashMap<>();

            while (resultSet.next()) {
                int supplierId = resultSet.getInt("SupplierID");
                Supplier supplier = supplierMap.get(supplierId);

                if (supplier == null) {
                    supplier = new Supplier(
                            supplierId,
                            resultSet.getString("Name"),
                            resultSet.getInt("PhoneNumber"),
                            resultSet.getString("EmailAddress"),
                            EntityState.valueOf(resultSet.getString("supplier_state").toUpperCase()),
                            new ArrayList<>()
                    );
                    supplierMap.put(supplierId, supplier);
                }

                if (resultSet.getObject("SupplyOfferID") != null) {
                    Address deliveryAddress = new Address(
                            resultSet.getInt("delivery_addressid"),
                            resultSet.getString("delivery_street"),
                            resultSet.getString("delivery_zipcode"),
                            resultSet.getString("delivery_town"),
                            resultSet.getString("delivery_country")
                    );

                    SupplyOffer offer = new SupplyOffer(
                            resultSet.getInt("SupplyOfferID"),
                            deliveryAddress,
                            resultSet.getDate("StartDate"),
                            resultSet.getDate("EndDate"),
                            ProcessState.valueOf(resultSet.getString("so_state").toUpperCase())
                    );
                    supplier.getSupplyOffers().add(offer);
                }
            }
            suppliers.addAll(supplierMap.values());
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }

        return suppliers;
    }

    public Supplier getById(DatabaseConnection connection, Integer id) {
        Supplier supplier = null;
        String sql = """
    SELECT s.SupplierID, s.Name, s.PhoneNumber, s.EmailAddress, s.State AS supplier_state,
           so.SupplyOfferID, so.DeliveryAddressID, so.StartDate, so.EndDate, so.State AS so_state,
           a.AddressID AS delivery_addressid, a.Street AS delivery_street, a.ZipCode AS delivery_zipcode,
           a.Town AS delivery_town, a.Country AS delivery_country
    FROM Supplier s
    LEFT JOIN SupplyOffer so ON s.SupplierID = so.SupplierID
    LEFT JOIN Address a ON so.DeliveryAddressID = a.AddressID
    WHERE s.SupplierID = ?;
    """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                if (supplier == null) {
                    supplier = new Supplier(
                            resultSet.getInt("SupplierID"),
                            resultSet.getString("Name"),
                            resultSet.getInt("PhoneNumber"),
                            resultSet.getString("EmailAddress"),
                            EntityState.valueOf(resultSet.getString("supplier_state").toUpperCase()),
                            new ArrayList<>()
                    );
                }

                if (resultSet.getObject("SupplyOfferID") != null) {
                    Address deliveryAddress = new Address(
                            resultSet.getInt("delivery_addressid"),
                            resultSet.getString("delivery_street"),
                            resultSet.getString("delivery_zipcode"),
                            resultSet.getString("delivery_town"),
                            resultSet.getString("delivery_country")
                    );

                    SupplyOffer offer = new SupplyOffer(
                            resultSet.getInt("SupplyOfferID"),
                            deliveryAddress,
                            resultSet.getDate("StartDate"),
                            resultSet.getDate("EndDate"),
                            ProcessState.valueOf(resultSet.getString("so_state").toUpperCase())
                    );
                    supplier.getSupplyOffers().add(offer);
                }
            }
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }

        return supplier;
    }

    public boolean getSupplierExists(DatabaseConnection connection, int id) {
        String sql = "SELECT COUNT(*) FROM Supplier WHERE SupplierID = ?";
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