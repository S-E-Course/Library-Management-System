package com.library.service;

import com.library.dao.BorrowingDAO;
import com.library.dao.FineDAO;
import com.library.dao.MediaDAO;
import com.library.dao.UserDAO;
import com.library.model.Book;
import com.library.model.Borrowing;
import com.library.model.Fine;
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
 * Unit tests for LibrarianService with Mockito mocks.
 */
class LibrarianServiceTest {

    private LibrarianService service;
    private BorrowingDAO borrowingDAO;
    private FineDAO fineDAO;
    private UserDAO userDAO;
    private MediaDAO mediaDAO;
    private Connection conn;

    /**
     * Creates a LibrarianService and replaces its DAOs with mocks.
     *
     * @throws Exception if mock setup fails
     */
    @BeforeEach
    void setUp() throws Exception {
        conn = mock(Connection.class);
        DatabaseConnection.setMockConnection(conn);

        service = new LibrarianService();

        borrowingDAO = mock(BorrowingDAO.class);
        fineDAO = mock(FineDAO.class);
        userDAO = mock(UserDAO.class);
        mediaDAO = mock(MediaDAO.class);

        setField(service, "borrowingDAO", borrowingDAO);
        setField(service, "fineDAO", fineDAO);
        setField(service, "userDAO", userDAO);
        setField(service, "mediaDAO", mediaDAO);
    }

    /**
     * Sets a private field on an object using reflection.
     *
     * @param target    object that owns the field
     * @param fieldName name of the field
     * @param value     new value for the field
     * @throws Exception if the field cannot be set
     */
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * Marks a test librarian as logged in.
     *
     * @throws Exception if the field cannot be set
     */
    private void setLoggedLibrarian() throws Exception {
        User librarian = new User();
        librarian.setUserId(10);
        librarian.setUsername("lib");
        librarian.setPasswordHash("pass");

        Field f = LibrarianService.class.getDeclaredField("loggedLibrarian");
        f.setAccessible(true);
        f.set(service, librarian);
    }

    /**
     * Login should succeed when the password is correct.
     *
     * @throws Exception if login fails unexpectedly
     */
    @Test
    void loginSucceedsWithCorrectPassword() throws Exception {
        User librarian = new User();
        librarian.setUserId(1);
        librarian.setUsername("lib");
        librarian.setPasswordHash("123");

        when(userDAO.findByUsername(conn, "lib")).thenReturn(librarian);

        boolean ok = service.login("lib", "123");

        assertTrue(ok);
    }

    /**
     * Login should fail when the password is wrong.
     *
     * @throws Exception if login throws unexpectedly
     */
    @Test
    void loginFailsWithWrongPassword() throws Exception {
        User librarian = new User();
        librarian.setUserId(1);
        librarian.setUsername("lib");
        librarian.setPasswordHash("123");

        when(userDAO.findByUsername(conn, "lib")).thenReturn(librarian);

        boolean ok = service.login("lib", "wrong");

        assertFalse(ok);
    }

    /**
     * detectOverdueMedia should throw if no librarian is logged in.
     *
     * @throws Exception if call behaves unexpectedly
     */
    @Test
    void detectOverdueMediaThrowsWhenNotLoggedIn() throws Exception {
        boolean thrown = false;

        try {
            service.detectOverdueMedia();
        } catch (IllegalStateException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    /**
     * detectOverdueMedia should do nothing when the list is empty.
     *
     * @throws Exception if call fails
     */
    @Test
    void detectOverdueMediaDoesNothingWhenListEmpty() throws Exception {
        setLoggedLibrarian();
        when(borrowingDAO.findOverdueMedia(conn)).thenReturn(null);

        service.detectOverdueMedia();

        verifyNoInteractions(fineDAO);
    }

    /**
     * Existing overdue fine is updated when days have passed.
     *
     * @throws Exception if call fails
     */
    @Test
    void detectOverdueMediaUpdatesExistingFine() throws Exception {
        setLoggedLibrarian();

        Borrowing b = new Borrowing();
        b.setBorrowId(5);
        b.setUserId(20);
        b.setMediaId(3);
        b.setStatus("overdue");
        b.setDueDate(LocalDate.now().minusDays(10));

        List<Borrowing> list = Arrays.asList(b);
        when(borrowingDAO.findOverdueMedia(conn)).thenReturn(list);

        Fine existing = new Fine();
        existing.setId(100);
        existing.setFineDate(LocalDate.now().minusDays(2));

        when(fineDAO.getBorrowingFine(conn, 5)).thenReturn(existing);

        service.detectOverdueMedia();

        verify(fineDAO).updateFineBalance(conn, 100, 2);
        verify(fineDAO).updateFineDate(conn, 100);
        verify(userDAO).updateUserBalance(conn, 20, 2.0);
        verify(fineDAO, never()).issueFine(any(Connection.class), anyInt(), anyInt(), anyDouble());
    }

    /**
     * New fine is issued for an overdue book.
     *
     * @throws Exception if call fails
     */
    @Test
    void detectOverdueMediaIssuesNewFineForBook() throws Exception {
        setLoggedLibrarian();

        Borrowing b = new Borrowing();
        b.setBorrowId(7);
        b.setUserId(30);
        b.setMediaId(11);
        b.setStatus("borrowed");
        b.setDueDate(LocalDate.now().minusDays(2));

        List<Borrowing> list = Arrays.asList(b);
        when(borrowingDAO.findOverdueMedia(conn)).thenReturn(list);

        when(fineDAO.getBorrowingFine(conn, 7)).thenReturn(null);

        Book book = new Book();
        book.setId(11);
        when(mediaDAO.findById(conn, 11)).thenReturn(book);

        when(fineDAO.issueFine(conn, 7, 30, 20.0)).thenReturn(true);

        service.detectOverdueMedia();

        verify(fineDAO).issueFine(conn, 7, 30, 20.0);
        verify(borrowingDAO).updateBorrowingStatus(conn, 7, "overdue");
        verify(userDAO).updateUserBalance(conn, 30, 20.0);
    }

    /**
     * New fine is issued for an overdue CD.
     *
     * @throws Exception if call fails
     */
    @Test
    void detectOverdueMediaIssuesNewFineForCd() throws Exception {
        setLoggedLibrarian();

        Borrowing b = new Borrowing();
        b.setBorrowId(8);
        b.setUserId(40);
        b.setMediaId(12);
        b.setStatus("borrowed");
        b.setDueDate(LocalDate.now().minusDays(1));

        List<Borrowing> list = Arrays.asList(b);
        when(borrowingDAO.findOverdueMedia(conn)).thenReturn(list);

        when(fineDAO.getBorrowingFine(conn, 8)).thenReturn(null);

        Media cd = new Media() {
            public String getType() { return "cd"; }
            public int getBorrowDurationDays() { return 7; }
        };
        cd.setId(12);
        when(mediaDAO.findById(conn, 12)).thenReturn(cd);

        when(fineDAO.issueFine(conn, 8, 40, 20.0)).thenReturn(true);

        service.detectOverdueMedia();

        verify(fineDAO).issueFine(conn, 8, 40, 20.0);
        verify(borrowingDAO).updateBorrowingStatus(conn, 8, "overdue");
        verify(userDAO).updateUserBalance(conn, 40, 20.0);
    }

    /**
     * New fine is issued for an overdue journal.
     *
     * @throws Exception if call fails
     */
    @Test
    void detectOverdueMediaIssuesNewFineForJournal() throws Exception {
        setLoggedLibrarian();

        Borrowing b = new Borrowing();
        b.setBorrowId(9);
        b.setUserId(50);
        b.setMediaId(13);
        b.setStatus("borrowed");
        b.setDueDate(LocalDate.now().minusDays(1));

        List<Borrowing> list = Arrays.asList(b);
        when(borrowingDAO.findOverdueMedia(conn)).thenReturn(list);

        when(fineDAO.getBorrowingFine(conn, 9)).thenReturn(null);

        Media journal = new Media() {
            public String getType() { return "journal"; }
            public int getBorrowDurationDays() { return 14; }
        };
        journal.setId(13);
        when(mediaDAO.findById(conn, 13)).thenReturn(journal);

        when(fineDAO.issueFine(conn, 9, 50, 15.0)).thenReturn(true);

        service.detectOverdueMedia();

        verify(fineDAO).issueFine(conn, 9, 50, 15.0);
        verify(borrowingDAO).updateBorrowingStatus(conn, 9, "overdue");
        verify(userDAO).updateUserBalance(conn, 50, 15.0);
    }
}
