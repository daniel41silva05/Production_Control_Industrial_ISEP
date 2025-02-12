package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.domain.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository implements Persistable<Client, Integer> {

    @Override
    public boolean save(DatabaseConnection connection, Client client) {

        String sql = "INSERT INTO Client (ClientID, AddressID, Name, Vatin, PhoneNumber, EmailAddress, Type, State) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, client.getId());
            statement.setInt(2, client.getAddress().getId());
            statement.setString(3, client.getName());
            statement.setString(4, client.getVatin());
            statement.setInt(5, client.getPhoneNumber());
            statement.setString(6, client.getEmail());
            statement.setString(7, client.getType().toString());
            statement.setString(8, client.getState().toString());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(DatabaseConnection connection, Client client) {

        String sql = "DELETE FROM Client WHERE clientid = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, client.getId());

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Client> getAll(DatabaseConnection connection) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int clientId = resultSet.getInt("clientid");
                Client client = new Client(
                        clientId,
                        getAddressById(connection, resultSet.getInt("addressid")),
                        resultSet.getString("name"),
                        resultSet.getString("vatin"),
                        resultSet.getInt("phonenumber"),
                        resultSet.getString("emailaddress"),
                        CompanyType.valueOf(resultSet.getString("type").toUpperCase()),
                        State.valueOf(resultSet.getString("state").toUpperCase()),
                        getOrdersByClientId(connection, clientId)
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    @Override
    public Client getById(DatabaseConnection connection, Integer id) {
        Client client = null;
        String sql = "SELECT * FROM Client WHERE clientid = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int clientId = resultSet.getInt("clientid");
                client = new Client(
                        clientId,
                        getAddressById(connection, resultSet.getInt("addressid")),
                        resultSet.getString("name"),
                        resultSet.getString("vatin"),
                        resultSet.getInt("phonenumber"),
                        resultSet.getString("emailaddress"),
                        CompanyType.valueOf(resultSet.getString("type").toUpperCase()),
                        State.valueOf(resultSet.getString("state").toUpperCase()),
                        getOrdersByClientId(connection, clientId)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return client;
    }

    public boolean getClientExists(DatabaseConnection connection, int id) {
        String sql = "SELECT COUNT(*) FROM Client WHERE clientid = ?";
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

    public int getClientCount(DatabaseConnection connection) {
        String sql = "SELECT COUNT(*) FROM Client";
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

    private List<Order> getOrdersByClientId(DatabaseConnection connection, int clientId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM \"Order\" WHERE ClientID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, clientId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Order order = new Order(
                        resultSet.getInt("orderid"),
                        getAddressById(connection, resultSet.getInt("deliveryaddressid")),
                        resultSet.getDate("orderdate"),
                        resultSet.getDate("deliverydate"),
                        resultSet.getDouble("price")
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    private Address getAddressById(DatabaseConnection connection, int id) {
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
