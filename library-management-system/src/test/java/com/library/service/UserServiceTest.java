package com.library.service;

import com.library.dao.*;
import com.library.model.Book;
import com.library.model.Media;
import com.library.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests UserService using mocked DAO objects and a mocked database connection.
 */
class UserServiceTest {

    private UserService service;
    private UserDAO userDAO;
    private MediaDAO mediaDAO;
    private BorrowingDAO borrowingDAO;
    private FineDAO fineDAO;
    private Connection conn;
    private User user;

    /**
     * Sets up the service and mocks before each test.
     *
     * @throws Exception if mock setup fails
     */
    @BeforeEach
    void setup() throws Exception {
        userDAO = mock(UserDAO.class);
        mediaDAO = mock(MediaDAO.class);
        borrowingDAO = mock(BorrowingDAO.class);
        fineDAO = mock(FineDAO.class);
        conn = mock(Connection.class);

        service = new UserService();
        service.setUserDAO(userDAO);
        service.setMediaDAO(mediaDAO);
        service.setBorrowingDAO(borrowingDAO);
        service.setFineDAO(fineDAO);
        service.setConnection(conn);
        service.setLoggedUser(null);

        user = new User();
        user.setUserId(1);
        user.setUsername("mosub");
        user.setPasswordHash("123");
    }

    /**
     * Tests successful login when the password matches.
     */
    @Test
    void loginSucceedsWithCorrectPassword() throws Exception {
        when(userDAO.findByUsername(conn, "mosub")).thenReturn(user);

        boolean ok = service.login("mosub", "123");

        assertTrue(ok);
        assertEquals(user, service.getLoggedUser());
    }

    /**
     * Tests failed login when the password is wrong.
     */
    @Test
    void loginFailsWithWrongPassword() throws Exception {
        when(userDAO.findByUsername(conn, "mosub")).thenReturn(user);

        boolean ok = service.login("mosub", "wrong");

        assertFalse(ok);
        assertNull(service.getLoggedUser());
    }

    /**
     * Tests borrowing media when conditions are valid.
     */
    @Test
    void borrowMediaSucceedsWhenNoBalanceNoOverdueAndAvailable() throws Exception {
        service.setLoggedUser(user);

        Book b = new Book();
        b.setId(5);
        b.setAvailable(true);

        when(userDAO.getUserBalance(conn, 1)).thenReturn(0.0);
        when(borrowingDAO.hasOverdueForUser(conn, 1)).thenReturn(false);
        when(mediaDAO.findById(conn, 5)).thenReturn(b);
        when(borrowingDAO.borrowMedia(conn, 1, 5)).thenReturn(true);

        boolean ok = service.borrowMedia(5);

        assertTrue(ok);
    }

    /**
     * Tests borrowing media when the item is not available.
     */
    @Test
    void borrowMediaFailsWhenMediaUnavailable() throws Exception {
        service.setLoggedUser(user);

        Book b = new Book();
        b.setId(1);
        b.setAvailable(false);

        when(userDAO.getUserBalance(conn, 1)).thenReturn(0.0);
        when(borrowingDAO.hasOverdueForUser(conn, 1)).thenReturn(false);
        when(mediaDAO.findById(conn, 1)).thenReturn(b);

        boolean ok = service.borrowMedia(1);

        assertFalse(ok);
    }

    /**
     * Tests borrowing media when the user has unpaid balance.
     */
    @Test
    void borrowMediaFailsWhenUserHasBalance() throws Exception {
        service.setLoggedUser(user);

        Book b = new Book();
        b.setId(7);
        b.setAvailable(true);

        when(userDAO.getUserBalance(conn, 1)).thenReturn(50.0);
        when(mediaDAO.findById(conn, 7)).thenReturn(b);

        boolean ok = service.borrowMedia(7);

        assertFalse(ok);
    }

    /**
     * Tests borrowing media when the user has overdue borrowings.
     */
    @Test
    void borrowMediaFailsWhenUserHasOverdueBorrowings() throws Exception {
        service.setLoggedUser(user);

        when(userDAO.getUserBalance(conn, 1)).thenReturn(0.0);
        when(borrowingDAO.hasOverdueForUser(conn, 1)).thenReturn(true);

        boolean ok = service.borrowMedia(5);

        assertFalse(ok);
    }

    /**
     * Tests returning media when the DAO reports success.
     */
    @Test
    void returnMediaSucceedsWhenDaoReturnsTrue() throws Exception {
        service.setLoggedUser(user);
        when(borrowingDAO.returnMedia(conn, 1, 9)).thenReturn(true);

        boolean ok = service.returnMedia(9);

        assertTrue(ok);
    }

    /**
     * Tests paying a fine when the DAO reports success.
     */
    @Test
    void payFineSucceedsWhenDaoReturnsTrue() throws Exception {
        service.setLoggedUser(user);
        when(fineDAO.payFine(conn, 3, 1, 20.0)).thenReturn(true);

        boolean ok = service.payFine(3, 20.0);

        assertTrue(ok);
    }

    /**
     * Tests searching for media and receiving a non-empty list.
     */
    @Test
    void searchMediaReturnsNonEmptyList() throws Exception {
        Media m = new Book();
        when(mediaDAO.searchMedia(conn, "abc", "book")).thenReturn(Arrays.asList(m));

        List<Media> list = service.searchMedia("abc", "book");

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    /**
     * Tests that the user connection getter is not null.
     */
    @Test
    void connectionGetterNotNull() {
        assertNotNull(service.getUserConnection());
    }
}
