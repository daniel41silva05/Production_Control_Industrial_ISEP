package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.data.Persistable;
import org.project.domain.Address;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddressRepository implements Persistable {

    @Override
    public boolean save(DatabaseConnection connection, Object object) {
        Address address = (Address) object;
        String sql = "INSERT INTO Address (Street, ZipCode, Town, Country) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, address.getStreet());
            statement.setString(2, address.getZipCode());
            statement.setString(3, address.getTown());
            statement.setString(4, address.getCountry());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(DatabaseConnection connection, Object object) {
        if (!(object instanceof Address)) {
            return false;
        }

        Address address = (Address) object;
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

    @Override
    public List<Object> getAll(DatabaseConnection connection) {
        List<Object> addresses = new ArrayList<>();
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

    public Address getById(DatabaseConnection connection, int id) {
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

}
