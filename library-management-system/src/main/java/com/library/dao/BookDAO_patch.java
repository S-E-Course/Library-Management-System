
package com.library.dao;

import com.library.model.Book;
import com.library.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO_patch {

    public List<Book> listAllBooks() throws Exception {
        String sql = "SELECT book_id, title, author, isbn, available FROM books ORDER BY book_id";
        List<Book> out = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Book b = new Book();
                b.setBookId(rs.getInt("book_id"));
                b.setTitle(rs.getString("title"));
                b.setAuthor(rs.getString("author"));
                b.setIsbn(rs.getString("isbn"));
                b.setAvailable(rs.getBoolean("available"));
                out.add(b);
            }
        }
        return out;
    }
}
