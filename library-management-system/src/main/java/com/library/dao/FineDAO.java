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
    	String sql =
    		    "UPDATE fines " +
    		    "SET amount = amount - ?, paid = (amount - ?) <= 0 " +
    		    "WHERE fine_id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setDouble(2, amount);
            stmt.setInt(3, fineId);
            stmt.setInt(4, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}
