package com.library.dao;

import com.library.util.DatabaseConnection;
import java.sql.*;

public class FineDAO {

    public boolean issueFine(int borrowId, int userId, double amount) throws Exception {
        String sql = "INSERT INTO fines (user_id, borrow_id, amount, paid) VALUES (?, ?, ?, FALSE)";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, borrowId);
            stmt.setDouble(3, amount);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean payFine(int fineId, int userId, double amount) throws Exception {
        String updateSql = "UPDATE fines SET amount = ?, paid = ? WHERE fine_id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            double fineAmount = getFineAmount(conn, fineId, userId);

            double payment = Math.min(amount, fineAmount);
            double newAmount = fineAmount - payment;
            boolean paid = newAmount <= 0;

            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setDouble(1, newAmount);
                stmt.setBoolean(2, paid);
                stmt.setInt(3, fineId);
                stmt.setInt(4, userId);
                return stmt.executeUpdate() > 0;
            }
        }
    }

    
    public double getFineAmount(Connection conn, int fineId, int userId) throws SQLException {
        String sql = "SELECT amount FROM fines WHERE fine_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fineId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("amount");
                } else {
                    throw new SQLException("Fine not found.");
                }
            }
        }
    }

}
