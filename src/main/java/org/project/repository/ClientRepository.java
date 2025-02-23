package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.domain.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientRepository implements Persistable<Client> {

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
        String deleteProductOrdersSQL = "DELETE FROM ProductOrder WHERE OrderID = ?;";
        String deleteOrdersSQL = "DELETE FROM \"Order\" WHERE OrderID = ?;";
        String deleteClientSQL = "DELETE FROM Client WHERE ClientID = ?;";

        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtProductOrders = conn.prepareStatement(deleteProductOrdersSQL);
                 PreparedStatement stmtOrders = conn.prepareStatement(deleteOrdersSQL);
                 PreparedStatement stmtClient = conn.prepareStatement(deleteClientSQL)) {

                for (Order order : client.getOrders()) {
                    stmtProductOrders.setInt(1, order.getId());
                    stmtProductOrders.executeUpdate();

                    stmtOrders.setInt(1, order.getId());
                    stmtOrders.executeUpdate();
                }

                stmtClient.setInt(1, client.getId());
                int rowsDeleted = stmtClient.executeUpdate();

                conn.commit();

                return rowsDeleted > 0;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(DatabaseConnection connection, Client client) {
        String sql = "UPDATE Client SET Name = ?, Vatin = ?, PhoneNumber = ?, EmailAddress = ?, Type = ?, AddressID = ? WHERE ClientID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, client.getName());
            statement.setString(2, client.getVatin());
            statement.setInt(3, client.getPhoneNumber());
            statement.setString(4, client.getEmail());
            statement.setString(5, client.getType().toString());
            statement.setInt(6, client.getAddress().getId());
            statement.setInt(7, client.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(DatabaseConnection connection, Client client) {
        String sql = "UPDATE Client SET State = ? WHERE ClientID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, client.getState().toString());
            statement.setInt(2, client.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Client> getAll(DatabaseConnection connection) {
        List<Client> clients = new ArrayList<>();
        String sql = """
    SELECT c.clientid, c.name, c.vatin, c.phonenumber, c.emailaddress, c.type, c.state, 
           a.addressid AS client_addressid, a.street AS client_street, a.zipcode AS client_zipcode, 
           a.town AS client_town, a.country AS client_country,
           o.orderid, o.orderdate, o.deliverydate, o.price,
           oa.addressid AS order_addressid, oa.street AS order_street, oa.zipcode AS order_zipcode,
           oa.town AS order_town, oa.country AS order_country
    FROM Client c
    JOIN Address a ON c.addressid = a.addressid
    LEFT JOIN "Order" o ON c.clientid = o.clientid
    LEFT JOIN Address oa ON o.deliveryaddressid = oa.addressid
    ORDER BY c.clientid, o.orderid;
    """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            Map<Integer, Client> clientMap = new HashMap<>();

            while (resultSet.next()) {
                int clientId = resultSet.getInt("clientid");
                Client client = clientMap.get(clientId);

                if (client == null) {
                    client = new Client(
                            clientId,
                            new Address(
                                    resultSet.getInt("client_addressid"),
                                    resultSet.getString("client_street"),
                                    resultSet.getString("client_zipcode"),
                                    resultSet.getString("client_town"),
                                    resultSet.getString("client_country")
                            ),
                            resultSet.getString("name"),
                            resultSet.getString("vatin"),
                            resultSet.getInt("phonenumber"),
                            resultSet.getString("emailaddress"),
                            CompanyType.valueOf(resultSet.getString("type").toUpperCase()),
                            State.valueOf(resultSet.getString("state").toUpperCase()),
                            new ArrayList<>()
                    );
                    clientMap.put(clientId, client);
                }

                if (resultSet.getObject("orderid") != null) {
                    Order order = new Order(
                            resultSet.getInt("orderid"),
                            new Address(  // Agora o endereço é o da Order (DeliveryAddress)
                                    resultSet.getInt("order_addressid"),
                                    resultSet.getString("order_street"),
                                    resultSet.getString("order_zipcode"),
                                    resultSet.getString("order_town"),
                                    resultSet.getString("order_country")
                            ),
                            resultSet.getDate("orderdate"),
                            resultSet.getDate("deliverydate"),
                            resultSet.getDouble("price")
                    );
                    client.getOrders().add(order);
                }
            }
            clients.addAll(clientMap.values());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    public Client getById(DatabaseConnection connection, Integer id) {
        Client client = null;
        String sql = """
    SELECT c.clientid, c.name, c.vatin, c.phonenumber, c.emailaddress, c.type, c.state, 
           a.addressid AS client_addressid, a.street AS client_street, a.zipcode AS client_zipcode, 
           a.town AS client_town, a.country AS client_country,
           o.orderid, o.orderdate, o.deliverydate, o.price,
           oa.addressid AS order_addressid, oa.street AS order_street, oa.zipcode AS order_zipcode,
           oa.town AS order_town, oa.country AS order_country
    FROM Client c
    JOIN Address a ON c.addressid = a.addressid
    LEFT JOIN "Order" o ON c.clientid = o.clientid
    LEFT JOIN Address oa ON o.deliveryaddressid = oa.addressid
    WHERE c.clientid = ?;
    """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                if (client == null) {
                    client = new Client(
                            resultSet.getInt("clientid"),
                            new Address(
                                    resultSet.getInt("client_addressid"),
                                    resultSet.getString("client_street"),
                                    resultSet.getString("client_zipcode"),
                                    resultSet.getString("client_town"),
                                    resultSet.getString("client_country")
                            ),
                            resultSet.getString("name"),
                            resultSet.getString("vatin"),
                            resultSet.getInt("phonenumber"),
                            resultSet.getString("emailaddress"),
                            CompanyType.valueOf(resultSet.getString("type").toUpperCase()),
                            State.valueOf(resultSet.getString("state").toUpperCase()),
                            new ArrayList<>()
                    );
                }

                if (resultSet.getObject("orderid") != null) {
                    Order order = new Order(
                            resultSet.getInt("orderid"),
                            new Address( // Agora o endereço da Order é único
                                    resultSet.getInt("order_addressid"),
                                    resultSet.getString("order_street"),
                                    resultSet.getString("order_zipcode"),
                                    resultSet.getString("order_town"),
                                    resultSet.getString("order_country")
                            ),
                            resultSet.getDate("orderdate"),
                            resultSet.getDate("deliverydate"),
                            resultSet.getDouble("price")
                    );
                    client.getOrders().add(order);
                }
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

}
