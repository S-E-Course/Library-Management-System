package com.library.service;

import com.library.dao.*;
import com.library.model.*;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.util.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for {@link UserService} using simple fake DAO implementations.
 *
 * These tests verify:
 * - login success and failure paths
 * - borrow rules (availability and outstanding balance)
 * - return media calls through to the DAO
 * - paying fines updates the DAO
 * - media search returns a non-empty list
 * - connection getter is non-null
 *
 * The test uses in-class fake DAOs instead of an external mocking framework.
 */
class UserServiceTest {

    /**
     * Minimal fake {@link UserDAO} for controlling user lookups and balances.
     */
    private static class FakeUserDAO extends UserDAO {
        double balance = 0;
        User user;

        @Override
        public User findByUsername(Connection conn, String username) { return user; }

        @Override
        public double getUserBalance(Connection conn, int id) { return balance; }
    }

    /**
     * Minimal fake {@link MediaDAO} for controlling search and find-by-id behavior.
     */
    private static class FakeMediaDAO extends MediaDAO {
        Media m;

        @Override
        public Media findById(Connection conn, int id) { return m; }

        @Override
        public List<Media> searchMedia(Connection conn, String kw, String type) {
            return Arrays.asList(m == null ? new Book() : m);
        }
    }

    /**
     * Minimal fake {@link BorrowingDAO} for recording borrow/return calls.
     */
    private static class FakeBorrowingDAO extends BorrowingDAO {
        boolean borrowed, returned;

        @Override
        public boolean borrowMedia(Connection c, int u, int m) { borrowed = true; return true; }

        @Override
        public boolean returnMedia(Connection c, int u, int m) { returned = true; return true; }
    }

    /**
     * Minimal fake {@link FineDAO} for recording payFine calls.
     */
    private static class FakeFineDAO extends FineDAO {
        boolean paid;

        @Override
        public boolean payFine(Connection c, int f, int u, double a) { paid = true; return true; }
    }

    private UserService service;
    private FakeUserDAO userDAO;
    private FakeMediaDAO mediaDAO;
    private FakeBorrowingDAO borrowingDAO;
    private FakeFineDAO fineDAO;
    private User user;

    /**
     * Initializes the service under test with fake DAOs and a test user.
     *
     * @throws Exception if the service setup fails
     */
    @BeforeEach
    void setup() throws Exception {
        userDAO = new FakeUserDAO();
        mediaDAO = new FakeMediaDAO();
        borrowingDAO = new FakeBorrowingDAO();
        fineDAO = new FakeFineDAO();

        service = new UserService() {
            { // expose internals to fake behavior
                setUserDAO(userDAO);
                setMediaDAO(mediaDAO);
                setBorrowingDAO(borrowingDAO);
                setFineDAO(fineDAO);
                setConnection((Connection) null);
                setLoggedUser(null);
            }
        };

        user = new User();
        user.setUserId(1);
        user.setUsername("mosub");
        user.setPasswordHash("123");
        userDAO.user = user;
    }

    /**
     * Verifies login succeeds with a matching password hash.
     *
     * @throws Exception on unexpected failure
     */
    @Test
    void loginSucceedsWithCorrectPassword() throws Exception {
        boolean ok = service.login("mosub", "123");
        assertThat(ok, is(true));
        assertThat(service.getLoggedUser(), equalTo(user));
    }

    /**
     * Verifies login fails with a wrong password hash.
     *
     * @throws Exception on unexpected failure
     */
    @Test
    void loginFailsWithWrongPassword() throws Exception {
        boolean ok = service.login("mosub", "wrong");
        assertThat(ok, is(false));
    }

    /**
     * Verifies borrowing succeeds when media is available and the user has no balance.
     *
     * @throws Exception on unexpected failure
     */
    @Test
    void borrowMediaSucceedsWhenAvailableAndNoBalance() throws Exception {
        service.setLoggedUser(user);
        Book b = new Book(); b.setId(5); b.setAvailable(true);
        mediaDAO.m = b;
        boolean ok = service.borrowMedia(5);
        assertThat(ok, is(true));
        assertThat(borrowingDAO.borrowed, is(true));
    }

    /**
     * Verifies borrowing fails when media is not available.
     *
     * @throws Exception on unexpected failure
     */
    @Test
    void borrowMediaFailsWhenUnavailable() throws Exception {
        service.setLoggedUser(user);
        Book b = new Book(); b.setId(1); b.setAvailable(false);
        mediaDAO.m = b;
        boolean ok = service.borrowMedia(1);
        assertThat(ok, is(false));
        assertThat(borrowingDAO.borrowed, is(false));
    }

    /**
     * Verifies borrowing fails when the user has an outstanding balance.
     *
     * @throws Exception on unexpected failure
     */
    @Test
    void borrowMediaFailsWhenUserHasBalance() throws Exception {
        service.setLoggedUser(user);
        Book b = new Book(); b.setId(7); b.setAvailable(true);
        mediaDAO.m = b;
        userDAO.balance = 50;
        boolean ok = service.borrowMedia(7);
        assertThat(ok, is(false));
    }

    /**
     * Verifies returning media calls through to the DAO and succeeds.
     *
     * @throws Exception on unexpected failure
     */
    @Test
    void returnMediaCallsDaoAndSucceeds() throws Exception {
        service.setLoggedUser(user);
        boolean ok = service.returnMedia(9);
        assertThat(ok, is(true));
        assertThat(borrowingDAO.returned, is(true));
    }

    /**
     * Verifies paying a fine calls through to the fine DAO and succeeds.
     *
     * @throws Exception on unexpected failure
     */
    @Test
    void payFineUpdatesFineDao() throws Exception {
        service.setLoggedUser(user);
        boolean ok = service.payFine(3, 20.0);
        assertThat(ok, is(true));
        assertThat(fineDAO.paid, is(true));
    }

    /**
     * Verifies media search returns a non-empty list.
     *
     * @throws Exception on unexpected failure
     */
    @Test
    void searchMediaReturnsList() throws Exception {
        List<Media> list = service.searchMedia("abc", "book");
        assertThat(list, is(not(empty())));
    }

    /**
     * Verifies the user service's connection getter is not null.
     * This ensures callers relying on a connection reference can obtain one.
     */
    @Test
    void connectionGetterNotNull() {
        assertThat(service.getUserConnection(), is(notNullValue()));
    }
}
