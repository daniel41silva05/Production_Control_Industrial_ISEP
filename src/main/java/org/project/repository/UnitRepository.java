package org.project.repository;

import org.project.data.DatabaseConnection;
import org.project.domain.Unit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UnitRepository {

    public boolean save(DatabaseConnection connection, Unit unit) {
        String sql = "INSERT INTO Unit (UnitID, Name, Symbol) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, unit.getId());
            statement.setString(2, unit.getName());
            statement.setString(3, unit.getSymbol());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUnitCount(DatabaseConnection connection) {
        String sql = "SELECT COUNT(*) FROM Unit";
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

    public Unit findUnit(DatabaseConnection connection, String name, String symbol) {
        String sql = "SELECT * FROM Unit WHERE name = ? AND symbol = ?";
        Unit unit = null;

        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, symbol);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                unit = new Unit(
                        resultSet.getInt("unitid"),
                        resultSet.getString("name"),
                        resultSet.getString("symbol")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unit;
    }

}
