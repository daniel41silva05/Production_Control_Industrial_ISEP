package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.exceptions.DatabaseException;
import org.project.model.Address;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressRepository {

    public boolean save(DatabaseConnection connection, Address address) {
        String sql = "INSERT INTO Address (AddressID, Street, ZipCode, Town, Country) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, address.getId());
            statement.setString(2, address.getStreet());
            statement.setString(3, address.getZipCode());
            statement.setString(4, address.getTown());
            statement.setString(5, address.getCountry());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }
    }

    public int getAddressCount(DatabaseConnection connection) {
        String sql = "SELECT COUNT(*) FROM Address";
        int count = 0;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }

        return count;
    }

    public Address findAddress(DatabaseConnection connection, String street, String zipCode, String town, String country) {
        String sql = "SELECT * FROM Address WHERE Street = ? AND ZipCode = ? AND Town = ? AND Country = ?";
        Address address = null;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, street);
            statement.setString(2, zipCode);
            statement.setString(3, town);
            statement.setString(4, country);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                address = new Address(resultSet.getInt("AddressID"),
                        resultSet.getString("Street"),
                        resultSet.getString("ZipCode"),
                        resultSet.getString("Town"),
                        resultSet.getString("Country"));
            }
        } catch (SQLException e) {
            throw DatabaseException.databaseError();
        }

        return address;
    }

}
