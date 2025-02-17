package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.domain.Address;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddressRepository implements Persistable<Address> {

    @Override
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
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(DatabaseConnection connection, Address address) {
        String sql = "DELETE FROM Address WHERE addressid = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, address.getId());

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(DatabaseConnection connection, Address address) {
        String sql = "UPDATE Address SET Street = ?, ZipCode = ?, Town = ?, Country = ? WHERE AddressID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, address.getStreet());
            statement.setString(2, address.getZipCode());
            statement.setString(3, address.getTown());
            statement.setString(4, address.getCountry());
            statement.setInt(5, address.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Address> getAll(DatabaseConnection connection) {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM Address";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Address address = new Address(
                        resultSet.getInt("addressid"),
                        resultSet.getString("street"),
                        resultSet.getString("zipcode"),
                        resultSet.getString("town"),
                        resultSet.getString("country")
                );
                addresses.add(address);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return addresses;
    }

    public Address getById(DatabaseConnection connection, Integer id) {
        Address address = null;
        String sql = "SELECT * FROM Address WHERE addressid = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                address = new Address(
                        resultSet.getInt("addressid"),
                        resultSet.getString("street"),
                        resultSet.getString("zipcode"),
                        resultSet.getString("town"),
                        resultSet.getString("country")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return address;
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
            e.printStackTrace();
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
            e.printStackTrace();
        }

        return address;
    }

}
