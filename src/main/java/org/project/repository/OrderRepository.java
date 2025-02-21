package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.domain.*;

import java.sql.*;
import java.util.*;

public class OrderRepository implements Persistable<Order> {

    @Override
    public boolean save(DatabaseConnection connection, Order order) {
        String insertOrderSQL = "INSERT INTO \"Order\" (OrderID, ClientID, DeliveryAddressID, OrderDate, DeliveryDate, Price) VALUES (?, ?, ?, ?, ?, ?)";
        String insertProductOrderSQL = "INSERT INTO ProductOrder (OrderID, ProductID, Quantity) VALUES (?, ?, ?)";

        try (PreparedStatement orderStmt = connection.getConnection().prepareStatement(insertOrderSQL);
             PreparedStatement productOrderStmt = connection.getConnection().prepareStatement(insertProductOrderSQL)) {

            orderStmt.setInt(1, order.getId());
            orderStmt.setInt(3, order.getDeliveryAddress().getId());
            orderStmt.setDate(4, new java.sql.Date(order.getOrderDate().getTime()));
            orderStmt.setDate(5, new java.sql.Date(order.getDeliveryDate().getTime()));
            orderStmt.setDouble(6, order.getPrice());

            int rowsInserted = orderStmt.executeUpdate();

            for (Map.Entry<Product, Integer> entry : order.getProductQuantity().entrySet()) {
                productOrderStmt.setInt(1, order.getId());
                productOrderStmt.setString(2, entry.getKey().getId());
                productOrderStmt.setInt(3, entry.getValue());
                productOrderStmt.executeUpdate();
            }
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(DatabaseConnection connection, Order order) {
        String deleteProductOrdersSQL = "DELETE FROM ProductOrder WHERE OrderID = ?;";
        String deleteOrdersSQL = "DELETE FROM \"Order\" WHERE OrderID = ?;";

        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtProductOrders = conn.prepareStatement(deleteProductOrdersSQL);
                 PreparedStatement stmtOrders = conn.prepareStatement(deleteOrdersSQL)) {

                stmtProductOrders.setInt(1, order.getId());
                stmtProductOrders.executeUpdate();

                stmtOrders.setInt(1, order.getId());
                int rowsDeleted = stmtOrders.executeUpdate();

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

    public boolean update(DatabaseConnection connection, Order order) {
        String sql = "UPDATE Order SET DeliveryAddressID = ?, OrderDate = ?, DeliveryDate = ?, Price = ? WHERE OrderID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, order.getDeliveryAddress().getId());
            statement.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
            statement.setDate(3, new java.sql.Date(order.getDeliveryDate().getTime()));
            statement.setDouble(4, order.getPrice());
            statement.setInt(5, order.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Order> getAll(DatabaseConnection connection) {
        List<Order> orders = new ArrayList<>();
        String sql = """
        SELECT o.OrderID, o.OrderDate, o.DeliveryDate, o.Price, 
               a.AddressID, a.Street, a.ZipCode, a.Town, a.Country, 
               p.ProductID, p.CategoryID, p.Capacity, p."Size", p.Color, p.Price AS ProductPrice, 
               pc.ProductCategoryID, pc.Name AS ProductCategoryName, 
               u.UnitID, u.Name AS UnitName, u.Symbol AS UnitSymbol, 
               po.Quantity,
               part.PartID, part.Name AS PartName, part.Description AS PartDescription
        FROM "Order" o 
        JOIN Address a ON o.DeliveryAddressID = a.AddressID 
        JOIN ProductOrder po ON o.OrderID = po.OrderID 
        JOIN Product p ON po.ProductID = p.ProductID
        JOIN ProductCategory pc ON p.CategoryID = pc.ProductCategoryID
        JOIN Unit u ON p.UnitID = u.UnitID
        JOIN Part part ON p.ProductID = part.PartID
        ORDER BY o.OrderID;
    """;

        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Map<Integer, Order> orderMap = new HashMap<>();

            while (rs.next()) {
                int orderId = rs.getInt("OrderID");
                Order order = orderMap.get(orderId);

                if (order == null) {
                    Address address = new Address(
                            rs.getInt("AddressID"),
                            rs.getString("Street"),
                            rs.getString("ZipCode"),
                            rs.getString("Town"),
                            rs.getString("Country")
                    );

                    order = new Order(
                            orderId,
                            address,
                            rs.getDate("OrderDate"),
                            rs.getDate("DeliveryDate"),
                            rs.getDouble("Price")
                    );

                    orderMap.put(orderId, order);
                }

                ProductCategory productCategory = new ProductCategory(
                        rs.getInt("ProductCategoryID"),
                        rs.getString("ProductCategoryName"));

                Unit unit = new Unit(
                        rs.getInt("UnitID"),
                        rs.getString("UnitName"),
                        rs.getString("UnitSymbol"));

                Product product = new Product(
                        rs.getString("ProductID"),
                        unit,
                        rs.getString("PartName"),
                        rs.getString("PartDescription"),
                        productCategory,
                        rs.getInt("Capacity"),
                        rs.getInt("Size"),
                        rs.getString("Color"),
                        rs.getDouble("ProductPrice")
                );

                order.getProductQuantity().put(product, rs.getInt("Quantity"));
            }
            orders.addAll(orderMap.values());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public Order getByID(DatabaseConnection connection, int id) {
        String sql = """
        SELECT o.OrderID, o.OrderDate, o.DeliveryDate, o.Price, 
               a.AddressID, a.Street, a.ZipCode, a.Town, a.Country, 
               p.ProductID, p.CategoryID, p.Capacity, p."Size", p.Color, p.Price AS ProductPrice, 
               pc.ProductCategoryID, pc.Name AS ProductCategoryName, 
               u.UnitID, u.Name AS UnitName, u.Symbol AS UnitSymbol, 
               po.Quantity,
               part.PartID, part.Name AS PartName, part.Description AS PartDescription
        FROM "Order" o 
        JOIN Address a ON o.DeliveryAddressID = a.AddressID 
        JOIN ProductOrder po ON o.OrderID = po.OrderID 
        JOIN Product p ON po.ProductID = p.ProductID
        JOIN ProductCategory pc ON p.CategoryID = pc.ProductCategoryID
        JOIN Unit u ON p.UnitID = u.UnitID
        JOIN Part part ON p.ProductID = part.PartID
        WHERE o.OrderID = ?;
    """;

        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                Order order = null;

                while (rs.next()) {
                    if (order == null) {
                        Address address = new Address(
                                rs.getInt("AddressID"),
                                rs.getString("Street"),
                                rs.getString("ZipCode"),
                                rs.getString("Town"),
                                rs.getString("Country")
                        );

                        order = new Order(
                                id,
                                address,
                                rs.getDate("OrderDate"),
                                rs.getDate("DeliveryDate"),
                                rs.getDouble("Price")
                        );
                    }

                    ProductCategory productCategory = new ProductCategory(
                            rs.getInt("ProductCategoryID"),
                            rs.getString("ProductCategoryName"));

                    Unit unit = new Unit(
                            rs.getInt("UnitID"),
                            rs.getString("UnitName"),
                            rs.getString("UnitSymbol"));

                    Product product = new Product(
                            rs.getString("ProductID"),
                            unit,
                            rs.getString("PartName"),
                            rs.getString("PartDescription"),
                            productCategory,
                            rs.getInt("Capacity"),
                            rs.getInt("Size"),
                            rs.getString("Color"),
                            rs.getDouble("ProductPrice")
                    );

                    order.getProductQuantity().put(product, rs.getInt("Quantity"));
                }
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean getOrderExists(DatabaseConnection connection, int id) {
        String sql = "SELECT COUNT(*) FROM \"Order\" WHERE orderid = ?";
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
