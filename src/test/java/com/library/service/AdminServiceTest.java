package com.library.service;

import com.library.dao.BorrowingDAO;
import com.library.dao.MediaDAO;
import com.library.dao.UserDAO;
import com.library.model.Borrowing;
import com.library.model.Media;
import com.library.model.User;
import com.library.util.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdminService using Mockito mocks.
 */
class AdminServiceTest {

    private AdminService service;
    private UserDAO userDAO;
    private MediaDAO mediaDAO;
    private BorrowingDAO borrowingDAO;
    private Connection conn;

    /**
     * Prepares an AdminService instance with mocked DAOs.
     *
     * @throws Exception if setup fails
     */
    @BeforeEach
    void setUp() throws Exception {
        conn = mock(Connection.class);
        DatabaseConnection.setMockConnection(conn);

        service = new AdminService();

        userDAO = mock(UserDAO.class);
        mediaDAO = mock(MediaDAO.class);
        borrowingDAO = mock(BorrowingDAO.class);

        setField(service, "userDAO", userDAO);
        setField(service, "mediaDAO", mediaDAO);
        setField(service, "borrowingDAO", borrowingDAO);
        setField(service, "loggedAdmin", null);
    }

    /**
     * Sets a private field by reflection.
     *
     * @param target object to modify
     * @param name   field name
     * @param value  value to set
     * @throws Exception if reflection fails
     */
    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * Marks an admin as logged in.
     *
     * @throws Exception if reflection fails
     */
    private void setLoggedAdmin() throws Exception {
        User admin = new User();
        admin.setUserId(1);
        admin.setUsername("admin");
        admin.setPasswordHash("123");
        setField(service, "loggedAdmin", admin);
    }

    /**
     * Tests that login succeeds when password matches.
     *
     * @throws Exception if call fails
     */
    @Test
    void loginSucceedsWithCorrectPassword() throws Exception {
        User admin = new User();
        admin.setUserId(1);
        admin.setUsername("admin");
        admin.setPasswordHash("123");

        when(userDAO.findByUsername(conn, "admin")).thenReturn(admin);

        boolean ok = service.login("admin", "123");

        assertTrue(ok);
    }

    /**
     * Tests that login fails with wrong password.
     *
     * @throws Exception if call fails
     */
    @Test
    void loginFailsWithWrongPassword() throws Exception {
        User admin = new User();
        admin.setUserId(1);
        admin.setUsername("admin");
        admin.setPasswordHash("123");

        when(userDAO.findByUsername(conn, "admin")).thenReturn(admin);

        boolean ok = service.login("admin", "wrong");

        assertFalse(ok);
    }

    /**
     * Tests that isLoggedIn is true after a successful login.
     *
     * @throws Exception if call fails
     */
    @Test
    void isLoggedInReturnsTrueAfterLogin() throws Exception {
        User admin = new User();
        admin.setUserId(1);
        admin.setUsername("admin");
        admin.setPasswordHash("123");

        when(userDAO.findByUsername(conn, "admin")).thenReturn(admin);

        service.login("admin", "123");

        assertTrue(service.isLoggedIn());
    }

    /**
     * Tests that isLoggedIn is false when no admin is logged in.
     */
    @Test
    void isLoggedInReturnsFalseWhenNotLoggedIn() {
        assertFalse(service.isLoggedIn());
    }

    /**
     * Tests that addMedia throws when admin is not logged in.
     *
     * @throws Exception if call fails
     */
    @Test
    void addMediaThrowsWhenNotLoggedIn() throws Exception {
        Media m = mock(Media.class);
        boolean thrown = false;

        try {
            service.addMedia(m);
        } catch (IllegalStateException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    /**
     * Tests that addMedia calls the DAO when admin is logged in.
     *
     * @throws Exception if call fails
     */
    @Test
    void addMediaCallsDaoWhenLoggedIn() throws Exception {
        setLoggedAdmin();
        Media m = mock(Media.class);

        when(mediaDAO.addMedia(conn, m)).thenReturn(true);

        boolean ok = service.addMedia(m);

        assertTrue(ok);
        verify(mediaDAO).addMedia(conn, m);
    }

    /**
     * Tests that searchMedia delegates to MediaDAO.
     *
     * @throws Exception if call fails
     */
    @Test
    void searchMediaDelegatesToDao() throws Exception {
        Media m = mock(Media.class);
        List<Media> list = Arrays.asList(m);

        when(mediaDAO.searchMedia(conn, "java", "book")).thenReturn(list);

        List<Media> result = service.searchMedia("java", "book");

        assertEquals(1, result.size());
        assertEquals(m, result.get(0));
    }

    /**
     * Tests that removeMedia throws when admin is not logged in.
     *
     * @throws Exception if call fails
     */
    @Test
    void removeMediaThrowsWhenNotLoggedIn() throws Exception {
        boolean thrown = false;

        try {
            service.removeMedia(5);
        } catch (IllegalStateException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    /**
     * Tests that removeMedia returns false when media is not available.
     *
     * @throws Exception if call fails
     */
    @Test
    void removeMediaReturnsFalseWhenNotAvailable() throws Exception {
        setLoggedAdmin();

        when(mediaDAO.mediaAvailable(conn, 5)).thenReturn(false);

        boolean ok = service.removeMedia(5);

        assertFalse(ok);
        verify(mediaDAO, never()).removeMedia(conn, 5);
    }

    /**
     * Tests that removeMedia removes media when available.
     *
     * @throws Exception if call fails
     */
    @Test
    void removeMediaRemovesWhenAvailable() throws Exception {
        setLoggedAdmin();

        when(mediaDAO.mediaAvailable(conn, 5)).thenReturn(true);
        when(mediaDAO.removeMedia(conn, 5)).thenReturn(true);

        boolean ok = service.removeMedia(5);

        assertTrue(ok);
        verify(mediaDAO).removeMedia(conn, 5);
    }

    /**
     * Tests that listAllMedia delegates to MediaDAO.
     *
     * @throws Exception if call fails
     */
    @Test
    void listAllMediaDelegatesToDao() throws Exception {
        Media m = mock(Media.class);
        List<Media> list = Arrays.asList(m);

        when(mediaDAO.listAllMedia(conn, "media")).thenReturn(list);

        List<Media> result = service.listAllMedia("media");

        assertEquals(1, result.size());
    }

    /**
     * Tests that addUser throws when admin is not logged in.
     *
     * @throws Exception if call fails
     */
    @Test
    void addUserThrowsWhenNotLoggedIn() throws Exception {
        boolean thrown = false;

        try {
            service.addUser("u", "u@example.com", "123", "user");
        } catch (IllegalStateException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    /**
     * Tests that addUser calls the DAO when admin is logged in.
     *
     * @throws Exception if call fails
     */
    @Test
    void addUserCallsDaoWhenLoggedIn() throws Exception {
        setLoggedAdmin();

        when(userDAO.addUser(any(Connection.class), any(User.class))).thenReturn(true);

        boolean ok = service.addUser("u", "u@example.com", "123", "user");

        assertTrue(ok);
        verify(userDAO).addUser(eq(conn), any(User.class));
    }

    /**
     * Tests that removeUser throws when admin is not logged in.
     *
     * @throws Exception if call fails
     */
    @Test
    void removeUserThrowsWhenNotLoggedIn() throws Exception {
        boolean thrown = false;

        try {
            service.removeUser(5);
        } catch (IllegalStateException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    /**
     * Tests that removeUser returns false when user has a balance.
     *
     * @throws Exception if call fails
     */
    @Test
    void removeUserReturnsFalseWhenUserHasBalance() throws Exception {
        setLoggedAdmin();

        when(userDAO.getUserBalance(conn, 10)).thenReturn(30.0);

        boolean ok = service.removeUser(10);

        assertFalse(ok);
        verify(userDAO, never()).deleteUser(conn, 10);
    }

    /**
     * Tests that removeUser resets active media and deletes the user.
     *
     * @throws Exception if call fails
     */
    @Test
    void removeUserResetsMediaAndDeletesUser() throws Exception {
        setLoggedAdmin();

        Media m1 = mock(Media.class);
        Media m2 = mock(Media.class);
        when(m1.getId()).thenReturn(1);
        when(m2.getId()).thenReturn(2);
        List<Media> medialist = Arrays.asList(m1, m2);

        when(userDAO.getUserBalance(conn, 10)).thenReturn(0.0);
        when(mediaDAO.findActiveMedia(conn, 10)).thenReturn(medialist);
        when(userDAO.deleteUser(conn, 10)).thenReturn(true);

        boolean ok = service.removeUser(10);

        assertTrue(ok);
        verify(mediaDAO).setMediaStatus(conn, 1, true);
        verify(mediaDAO).setMediaStatus(conn, 2, true);
        verify(userDAO).deleteUser(conn, 10);
    }

    /**
     * Tests that removeUser works when there is no active media.
     *
     * @throws Exception if call fails
     */
    @Test
    void removeUserDeletesWhenNoActiveMedia() throws Exception {
        setLoggedAdmin();

        when(userDAO.getUserBalance(conn, 11)).thenReturn(0.0);
        when(mediaDAO.findActiveMedia(conn, 11)).thenReturn(null);
        when(userDAO.deleteUser(conn, 11)).thenReturn(true);

        boolean ok = service.removeUser(11);

        assertTrue(ok);
        verify(userDAO).deleteUser(conn, 11);
    }

    /**
     * Tests that listUsers throws when admin is not logged in.
     *
     * @throws Exception if call fails
     */
    @Test
    void listUsersThrowsWhenNotLoggedIn() throws Exception {
        boolean thrown = false;

        try {
            service.listUsers();
        } catch (IllegalStateException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    /**
     * Tests that listUsers delegates to UserDAO when logged in.
     *
     * @throws Exception if call fails
     */
    @Test
    void listUsersDelegatesToDaoWhenLoggedIn() throws Exception {
        setLoggedAdmin();

        User u = new User();
        u.setUserId(1);
        List<User> users = Arrays.asList(u);

        when(userDAO.getAllUsers(conn)).thenReturn(users);

        List<User> result = service.listUsers();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUserId());
    }

    /**
     * Tests sendOverdueReminders when there are overdue borrowings.
     *
     * @throws Exception if call fails
     */
    @Test
    void sendOverdueRemindersCountsUsersAndSendsEmails() throws Exception {
        Borrowing b1 = new Borrowing();
        b1.setBorrowId(1);
        b1.setUserId(10);
        b1.setStatus("overdue");
        b1.setDueDate(LocalDate.now().minusDays(3));

        Borrowing b2 = new Borrowing();
        b2.setBorrowId(2);
        b2.setUserId(10);
        b2.setStatus("overdue");
        b2.setDueDate(LocalDate.now().minusDays(1));

        Borrowing b3 = new Borrowing();
        b3.setBorrowId(3);
        b3.setUserId(20);
        b3.setStatus("borrowed");
        b3.setDueDate(LocalDate.now().minusDays(2));

        List<Borrowing> list = Arrays.asList(b1, b2, b3);
        when(borrowingDAO.findOverdueMedia(conn)).thenReturn(list);

        User u1 = new User();
        u1.setUserId(10);
        u1.setEmail("u1@example.com");
        User u2 = new User();
        u2.setUserId(20);
        u2.setEmail("u2@example.com");

        when(userDAO.findById(conn, 10)).thenReturn(u1);
        when(userDAO.findById(conn, 20)).thenReturn(u2);

        EmailServer emailServer = mock(EmailServer.class);

        int count = service.sendOverdueReminders(emailServer);

        assertEquals(2, count);
        verify(emailServer).send("u1@example.com", "Overdue Reminder", "You have 2 overdue book(s).");
        verify(emailServer).send("u2@example.com", "Overdue Reminder", "You have 1 overdue book(s).");
    }

    /**
     * Tests sendOverdueReminders when there are no overdue borrowings.
     *
     * @throws Exception if call fails
     */
    @Test
    void sendOverdueRemindersReturnsZeroWhenNone() throws Exception {
        when(borrowingDAO.findOverdueMedia(conn)).thenReturn(null);

        EmailServer emailServer = mock(EmailServer.class);

        int count = service.sendOverdueReminders(emailServer);

        assertEquals(0, count);
        verifyNoInteractions(emailServer);
    }
}
