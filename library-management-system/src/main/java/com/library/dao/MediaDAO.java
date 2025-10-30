package com.library.dao;

import com.library.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MediaDAO {

    /**
     * Inserts a new media item (Book, CD, or Journal) into the database.
     */
    public boolean addMedia(Connection conn, Media media) throws Exception {
        String sql = "INSERT INTO media (title, author, isbn, type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getAuthor());
            stmt.setString(3, media.getIsbn());
            stmt.setString(4, media.getType());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a media record by its ID.
     */
    public boolean removeMedia(Connection conn, int mediaId) throws Exception {
        String sql = "DELETE FROM media WHERE media_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Searches media records by keyword (title, author, ISBN),
     * filtered by media type (book/cd/journal).
     * 
     * @param conn    database connection
     * @param keyword text fragment to search
     * @param type    "book", "cd", "journal", or "media" for all
     */
    public List<Media> searchMedia(Connection conn, String keyword, String type) throws Exception {
        List<Media> results = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT * FROM media WHERE (title ILIKE ? OR author ILIKE ? OR isbn ILIKE ?)"
        );
        if (type != null && !type.equalsIgnoreCase("media")) {
            sql.append(" AND type = ?");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            String like = "%" + keyword + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);

            if (type != null && !type.equalsIgnoreCase("media")) {
                stmt.setString(4, type.toLowerCase());
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapRowToMedia(rs));
            }
        }
        return results;
    }

    /**
     * Updates the availability status of a media item.
     */
    public boolean setMediaStatus(Connection conn, int mediaId, boolean available) throws Exception {
        String sql = "UPDATE media SET available = ? WHERE media_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, available);
            stmt.setInt(2, mediaId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Checks if a media item is available for borrowing.
     */
    public boolean mediaAvailable(Connection conn, int mediaId) throws Exception {
        String sql = "SELECT available FROM media WHERE media_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getBoolean("available");
        }
        throw new Exception("Media not found");
    }
    
    /**
     * Lists all media items, filtered by type.
     * 
     * @param conn database connection
     * @param type "book", "cd", "journal", or "media" for all
     */
    public List<Media> listAllMedia(Connection conn, String type) throws Exception {
        List<Media> mediaList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM media");

        if (type != null && !type.equalsIgnoreCase("media")) {
            sql.append(" WHERE type = ?");
        }
        sql.append(" ORDER BY media_id");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (type != null && !type.equalsIgnoreCase("media")) {
                ps.setString(1, type.toLowerCase());
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	mediaList.add(mapRowToMedia(rs));
            }
        }
        return mediaList;
    }

    /**
     * Finds a specific media item by ID.
     */
    public Media findById(Connection conn, int mediaId) throws Exception {
        String sql = "SELECT * FROM media WHERE media_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mediaId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToMedia(rs);
            }
        }
        return null;
    }
    
    public List<Media> findActiveMedia(Connection conn, int userId) throws Exception {
        List<Media> mediaList = new ArrayList<>();

        String sql = "SELECT m.* FROM media m " +
                     "JOIN borrowings b ON m.media_id = b.media_id " +
                     "WHERE b.user_id = ? AND b.status = 'borrowed'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mediaList.add(mapRowToMedia(rs));
                }
            }
        }
        return mediaList;
    }


    /**
     * Maps a database row to the correct Media subclass (Book/CD/Journal).
     */
    private Media mapRowToMedia(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        Media m;

        switch (type) {
        case "cd":
            m = new CD();
            break;
        case "journal":
            m = new Journal();
            break;
        default:
            m = new Book();
            break;
    }

        m.setId(rs.getInt("media_id"));
        m.setTitle(rs.getString("title"));
        m.setAuthor(rs.getString("author"));
        m.setIsbn(rs.getString("isbn"));
        m.setAvailable(rs.getBoolean("available"));
        return m;
    }
}
