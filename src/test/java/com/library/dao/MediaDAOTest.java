package com.library.dao;

import com.library.model.Book;
import com.library.model.CD;
import com.library.model.Journal;
import com.library.model.Media;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for MediaDAO using Mockito mocks.
 */
class MediaDAOTest {

    /**
     * Tests that addMedia returns true when the insert works.
     */
    @Test
    void addMedia_returnsTrueWhenInsertSucceeds() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        MediaDAO dao = new MediaDAO();
        Media m = new Book();
        m.setTitle("How to Know");
        m.setAuthor("MO Sam");
        m.setIsbn("1403529763");

        boolean ok = dao.addMedia(conn, m);

        assertTrue(ok);
    }

    /**
     * Tests that removeMedia returns true when a row is deleted.
     */
    @Test
    void removeMedia_returnsTrueWhenRowDeleted() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        MediaDAO dao = new MediaDAO();
        boolean ok = dao.removeMedia(conn, 5);

        assertTrue(ok);
    }

    /**
     * Tests that searchMedia returns a list with a Book item.
     */
    @Test
    void searchMedia_returnsBookResults() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getString("type")).thenReturn("book");
        when(rs.getInt("media_id")).thenReturn(1);
        when(rs.getString("title")).thenReturn("Algorithms");
        when(rs.getString("author")).thenReturn("CLRS");
        when(rs.getString("isbn")).thenReturn("123456");
        when(rs.getBoolean("available")).thenReturn(true);

        MediaDAO dao = new MediaDAO();
        List<Media> list = dao.searchMedia(conn, "algo", "book");

        assertNotNull(list);
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof Book);
        assertEquals("Algorithms", list.get(0).getTitle());
    }

    /**
     * Tests updating the availability status.
     */
    @Test
    void setMediaStatus_returnsTrueWhenUpdated() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        MediaDAO dao = new MediaDAO();
        boolean ok = dao.setMediaStatus(conn, 3, false);

        assertTrue(ok);
    }

    /**
     * Tests that mediaAvailable returns true when the item is available.
     */
    @Test
    void mediaAvailable_returnsTrueWhenAvailable() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getBoolean("available")).thenReturn(true);

        MediaDAO dao = new MediaDAO();
        boolean available = dao.mediaAvailable(conn, 7);

        assertTrue(available);
    }

    /**
     * Tests that mediaAvailable throws an exception when no row exists.
     */
    @Test
    void mediaAvailable_throwsWhenNotFound() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        MediaDAO dao = new MediaDAO();

        boolean exceptionThrown = false;

        try {
            dao.mediaAvailable(conn, 999);
        } catch (Exception e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    /**
     * Tests that listAllMedia returns three different media types.
     */
    @Test
    void listAllMedia_returnsMixedTypes() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, true, true, false);

        when(rs.getString("type"))
                .thenReturn("book", "cd", "journal");
        when(rs.getInt("media_id"))
                .thenReturn(1, 2, 3);
        when(rs.getString("title"))
                .thenReturn("Book T", "CD T", "Journal T");
        when(rs.getString("author"))
                .thenReturn("Book A", "CD A", "Journal A");
        when(rs.getString("isbn"))
                .thenReturn("BISBN", "CISBN", "JISBN");
        when(rs.getBoolean("available"))
                .thenReturn(true, false, true);

        MediaDAO dao = new MediaDAO();
        List<Media> list = dao.listAllMedia(conn, "media");

        assertNotNull(list);
        assertEquals(3, list.size());
        assertTrue(list.get(0) instanceof Book);
        assertTrue(list.get(1) instanceof CD);
        assertTrue(list.get(2) instanceof Journal);
    }

    /**
     * Tests that findById returns a media item when found.
     */
    @Test
    void findById_returnsMediaWhenExists() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getString("type")).thenReturn("cd");
        when(rs.getInt("media_id")).thenReturn(10);
        when(rs.getString("title")).thenReturn("Cool CD");
        when(rs.getString("author")).thenReturn("DJ");
        when(rs.getString("isbn")).thenReturn("CDISBN");
        when(rs.getBoolean("available")).thenReturn(true);

        MediaDAO dao = new MediaDAO();
        Media m = dao.findById(conn, 10);

        assertNotNull(m);
        assertTrue(m instanceof CD);
        assertEquals(10, m.getId());
        assertEquals("Cool CD", m.getTitle());
    }

    /**
     * Tests that findById returns null when no row exists.
     */
    @Test
    void findById_returnsNullWhenNoRow() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        MediaDAO dao = new MediaDAO();
        Media m = dao.findById(conn, 999);

        assertNull(m);
    }

    /**
     * Tests that findActiveMedia returns borrowed media for a user.
     */
    @Test
    void findActiveMedia_returnsBorrowedMediaForUser() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getString("type")).thenReturn("journal");
        when(rs.getInt("media_id")).thenReturn(22);
        when(rs.getString("title")).thenReturn("Journal of Testing");
        when(rs.getString("author")).thenReturn("Tester");
        when(rs.getString("isbn")).thenReturn("JTEST");
        when(rs.getBoolean("available")).thenReturn(false);

        MediaDAO dao = new MediaDAO();
        List<Media> list = dao.findActiveMedia(conn, 101);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof Journal);
        assertEquals(22, list.get(0).getId());
    }
}
