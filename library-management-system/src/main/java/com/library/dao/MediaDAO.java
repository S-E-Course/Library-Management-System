package com.library.dao;

import com.library.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * DAO for creation,read,update,deletion, and lookup operations on media. Supports Book, CD, and Journal.
 *
 * @author
 * @version 1.0
 */
public class MediaDAO {

    /**
     * Inserts a new media item.
     *
     * @param conn active database connection
     * @param media media to insert
     * @return true if the row was inserted
     * @throws Exception if a database error occurs
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
     * Deletes a media item by id.
     *
     * @param conn active database connection
     * @param mediaId media identifier
     * @return true if a row was deleted
     * @throws Exception if a database error occurs
     */
    public boolean removeMedia(Connection conn, int mediaId) throws Exception {
        String sql = "DELETE FROM media WHERE media_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Searches media by keyword in title, author, or ISBN. Optionally filters by type.
     *
     * @param conn active database connection
     * @param keyword search fragment
     * @param type media type value or media for all
     * @return list of matching media
     * @throws Exception if a database error occurs
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
     * Updates the availability of a media item.
     *
     * @param conn active database connection
     * @param mediaId media identifier
     * @param available true to set available, false to set borrowed
     * @return true if a row was updated
     * @throws Exception if a database error occurs
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
     * Checks if a media item is available.
     *
     * @param conn active database connection
     * @param mediaId media identifier
     * @return true if available, false if not available
     * @throws Exception if the media is not found or a database error occurs
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
     * Lists all media, optionally filtered by type.
     *
     * @param conn active database connection
     * @param type media type value or media for all
     * @return list of media
     * @throws Exception if a database error occurs
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
     * Finds a media item by id.
     *
     * @param conn active database connection
     * @param mediaId media identifier
     * @return the media item or null if not found
     * @throws Exception if a database error occurs
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

    /**
     * Returns active borrowed media for a user.
     *
     * @param conn active database connection
     * @param userId user identifier
     * @return list of active media for the user
     * @throws Exception if a database error occurs
     */
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
     * Maps a result set row to a Media subclass instance.
     *
     * @param rs result set positioned on a media row
     * @return a Media instance
     * @throws SQLException if a column read fails
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