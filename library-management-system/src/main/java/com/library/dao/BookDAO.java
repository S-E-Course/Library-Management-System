package com.library.dao;

import com.library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.library.util.DatabaseConnection;

public class BookDAO {

    public boolean addBook(Book book) throws Exception {
        String sql = "INSERT INTO books (title, author, isbn, available) VALUES (?, ?, ?, TRUE)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean removeBook(int bookId) throws Exception {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public List<Book> searchBooks(String keyword) throws Exception {
        List<Book> results = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title ILIKE ? OR author ILIKE ? OR isbn ILIKE ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book b = new Book();
                b.setBookId(rs.getInt("book_id"));
                b.setTitle(rs.getString("title"));
                b.setAuthor(rs.getString("author"));
                b.setIsbn(rs.getString("isbn"));
                b.setAvailable(rs.getBoolean("available"));
                results.add(b);
            }
        }
        return results;
    }
    
    public boolean setBookStatus(Connection conn, int bookId, boolean available) throws Exception {
        String sql = "UPDATE books SET available = ? WHERE book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, available);
            stmt.setInt(2, bookId);
            return stmt.executeUpdate() > 0;
        }
    }

    
    public boolean bookAvailable(int bookId) throws Exception {
        String sql = "SELECT available FROM books WHERE book_id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("available");
            }
        }
        throw new Exception("book not found");
    }
    
    public List<Book> listAllBooks() throws Exception {
        String sql = "SELECT book_id, title, author, isbn, available FROM books ORDER BY book_id";
        List<Book> out = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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

    public Book findById(int bookId) throws Exception {
        String sql = "SELECT book_id, title, author, isbn, available FROM books WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Book b = new Book();
                    b.setBookId(rs.getInt("book_id"));
                    b.setTitle(rs.getString("title"));
                    b.setAuthor(rs.getString("author"));
                    b.setIsbn(rs.getString("isbn"));
                    b.setAvailable(rs.getBoolean("available"));
                    return b;
                }
            }
        }
        return null; 
    }
}
