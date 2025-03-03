package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.model.*;

import java.sql.*;
import java.util.*;

public class ProductRepository {

    public boolean saveProduct(DatabaseConnection connection, Product product) {
        String insertPartSQL = """
            INSERT INTO Part (PartID, Name, Description)
            VALUES (?, ?, ?)
        """;
        String insertProductSQL = """
            INSERT INTO Product (ProductID, CategoryID, Capacity, "Size", Color, Price)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtPart = conn.prepareStatement(insertPartSQL);
                 PreparedStatement stmtProduct = conn.prepareStatement(insertProductSQL)) {

                stmtPart.setString(1, product.getId());
                stmtPart.setString(2, product.getName());
                stmtPart.setString(3, product.getDescription());
                int rowsInsertedPart = stmtPart.executeUpdate();

                if (rowsInsertedPart > 0) {
                    stmtProduct.setString(1, product.getId());
                    stmtProduct.setInt(2, product.getCategory().getId());
                    stmtProduct.setInt(3, product.getCapacity());
                    stmtProduct.setInt(4, product.getSize());
                    stmtProduct.setString(5, product.getColor());
                    stmtProduct.setDouble(6, product.getPrice());
                    int rowsInsertedProduct = stmtProduct.executeUpdate();

                    if (rowsInsertedProduct > 0) {
                        conn.commit();
                        return true;
                    }
                }

                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveComponent(DatabaseConnection connection, Component component) {
        String insertPartSQL = """
            INSERT INTO Part (PartID, Name, Description)
            VALUES (?, ?, ?)
        """;
        String insertProductSQL = """
            INSERT INTO Component (ComponentID)
            VALUES (?)
        """;

        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtPart = conn.prepareStatement(insertPartSQL);
                 PreparedStatement stmtProduct = conn.prepareStatement(insertProductSQL)) {

                stmtPart.setString(1, component.getId());
                stmtPart.setString(2, component.getName());
                stmtPart.setString(3, component.getDescription());
                int rowsInsertedPart = stmtPart.executeUpdate();

                if (rowsInsertedPart > 0) {
                    stmtProduct.setString(1, component.getId());
                    int rowsInsertedProduct = stmtProduct.executeUpdate();

                    if (rowsInsertedProduct > 0) {
                        conn.commit();
                        return true;
                    }
                }

                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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
                return false;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> getAllProducts(DatabaseConnection connection) {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.ProductID, p.Capacity, p."Size", p.Color, p.Price, 
                   part.PartID, part.Name AS PartName, part.Description AS PartDescription,
                   pc.ProductCategoryID, pc.Name AS ProductCategoryName
            FROM Product p
            JOIN Part part ON p.ProductID = part.PartID
            JOIN ProductCategory pc ON p.CategoryID = pc.ProductCategoryID
        """;

        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                ProductCategory productCategory = new ProductCategory(
                        rs.getInt("ProductCategoryID"),
                        rs.getString("ProductCategoryName")
                );

                Product product = new Product(
                        rs.getString("ProductID"),
                        rs.getString("PartName"),
                        rs.getString("PartDescription"),
                        productCategory,
                        rs.getInt("Capacity"),
                        rs.getInt("Size"),
                        rs.getString("Color"),
                        rs.getDouble("Price")
                );

                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Component> getAllComponents(DatabaseConnection connection) {
        List<Component> components = new ArrayList<>();
        String sql = """
        SELECT part.PartID, part.Name AS PartName, part.Description AS PartDescription
        FROM Component c
        JOIN Part part ON c.ComponentID = part.PartID;
    """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                components.add(new Component(
                        rs.getString("PartID"),
                        rs.getString("PartName"),
                        rs.getString("PartDescription")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return components;
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
                            EntityState.valueOf(rs.getString("State"))
                    );
                    rawMaterial.getRawMaterialCost().put(supplier, rs.getDouble("ExpectedUnitCost"));
                }
            }

            rawMaterials.addAll(rawMaterialMap.values());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rawMaterials;
    }


    public Product getProductByID(DatabaseConnection connection, String id) {
        Product product = null;
        String sql = """
            SELECT p.ProductID, p.Capacity, p."Size", p.Color, p.Price, 
                   part.PartID, part.Name AS PartName, part.Description AS PartDescription,
                   pc.ProductCategoryID, pc.Name AS ProductCategoryName
            FROM Product p
            JOIN Part part ON p.ProductID = part.PartID
            JOIN ProductCategory pc ON p.CategoryID = pc.ProductCategoryID
            WHERE p.ProductID = ?;
        """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {

                ProductCategory productCategory = new ProductCategory(
                        rs.getInt("ProductCategoryID"),
                        rs.getString("ProductCategoryName")
                );

                product = new Product(
                        rs.getString("ProductID"),
                        rs.getString("PartName"),
                        rs.getString("PartDescription"),
                        productCategory,
                        rs.getInt("Capacity"),
                        rs.getInt("Size"),
                        rs.getString("Color"),
                        rs.getDouble("Price")
                );

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    public Component getComponentByID(DatabaseConnection connection, String id) {
        Component component = null;
        String sql = """
            SELECT part.PartID, part.Name AS PartName, part.Description AS PartDescription
            FROM Component c
            JOIN Part part ON c.ComponentID = part.PartID
            WHERE c.ComponentID = ?;
        """;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {

                component = new Component(
                        rs.getString("PartID"),
                        rs.getString("PartName"),
                        rs.getString("PartDescription")
                );

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return component;
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
                            EntityState.valueOf(rs.getString("State"))
                    );
                    rawMaterialCostMap.put(supplier, rs.getDouble("ExpectedUnitCost"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rawMaterial;
    }

    public boolean getProductExists(DatabaseConnection connection, String id) {
        String sql = "SELECT COUNT(*) FROM Product WHERE productid = ?";
        int count = 0;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);

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

    public boolean getComponentExists(DatabaseConnection connection, String id) {
        String sql = "SELECT COUNT(*) FROM Component WHERE ComponentID = ?";
        int count = 0;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);

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
            e.printStackTrace();
        }

        return count > 0;
    }

    public boolean getPartExists(DatabaseConnection connection, String id) {
        String sql = "SELECT COUNT(*) FROM Part WHERE PartID = ?";
        int count = 0;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);

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

    public boolean updateCategory(DatabaseConnection connection, Product product) {
        String sql = "UPDATE Product SET CategoryID = ? WHERE ProductID = ?";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, product.getCategory().getId());
            statement.setString(2, product.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
            e.printStackTrace();
            return false;
        }
    }

}
