package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.data.Persistable;
import org.project.domain.Address;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddressRepository implements Persistable {

    public boolean save(Connection connection, Object object) {
        Address address = (Address) object;
        String sql = "INSERT INTO Address (Street, ZipCode, Town, Country) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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
    public boolean delete(Connection databaseConnection, Object object) {
        if (!(object instanceof Address)) {
            return false;
        }

        Address address = (Address) object;
        String sql = "DELETE FROM addresses WHERE id = ?";

        return true;

//        try (PreparedStatement statement = databaseConnection.getConnection().prepareStatement(sql)) {
//            statement.setInt(1, address.getId());
//
//            int rowsDeleted = statement.executeUpdate();
//            return rowsDeleted > 0;
//        } catch (SQLException e) {
//            databaseConnection.registerError(e);
//            return false;
//        }
    }

//    public List<Address> getAllAddresses(DatabaseConnection databaseConnection) {
//        List<Address> addresses = new ArrayList<>();
//        String sql = "SELECT * FROM addresses";
//
//        try (PreparedStatement statement = databaseConnection.getConnection().prepareStatement(sql);
//             ResultSet resultSet = statement.executeQuery()) {
//
//            while (resultSet.next()) {
//                Address address = new Address(
//                        resultSet.getInt("id"),
//                        resultSet.getString("street"),
//                        resultSet.getString("zip_code"),
//                        resultSet.getString("town"),
//                        resultSet.getString("country")
//                );
//                addresses.add(address);
//            }
//        } catch (SQLException e) {
//            databaseConnection.registerError(e);
//        }
//
//        return addresses;
//    }
}