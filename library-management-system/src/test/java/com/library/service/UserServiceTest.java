package com.library.service;

import com.library.dao.*;
import com.library.model.*;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.util.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class UserServiceTest {

    private static class FakeUserDAO extends UserDAO {
        double balance = 0;
        User user;
        @Override
        public User findByUsername(Connection conn, String username) { return user; }
        @Override
        public double getUserBalance(Connection conn, int id) { return balance; }
    }

    private static class FakeMediaDAO extends MediaDAO {
        Media m;
        @Override
        public Media findById(Connection conn, int id) { return m; }
        @Override
        public List<Media> searchMedia(Connection conn, String kw, String type) {
            return Arrays.asList(m == null ? new Book() : m);
        }
    }

    private static class FakeBorrowingDAO extends BorrowingDAO {
        boolean borrowed, returned;
        @Override
        public boolean borrowMedia(Connection c, int u, int m) { borrowed = true; return true; }
        @Override
        public boolean returnMedia(Connection c, int u, int m) { returned = true; return true; }
    }

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

    @Test
    void loginSucceedsWithCorrectPassword() throws Exception {
        boolean ok = service.login("mosub", "123");
        assertThat(ok, is(true));
        assertThat(service.getLoggedUser(), equalTo(user));
    }

    @Test
    void loginFailsWithWrongPassword() throws Exception {
        boolean ok = service.login("mosub", "wrong");
        assertThat(ok, is(false));
    }

    @Test
    void borrowMediaSucceedsWhenAvailableAndNoBalance() throws Exception {
        service.setLoggedUser(user);
        Book b = new Book(); b.setId(5); b.setAvailable(true);
        mediaDAO.m = b;
        boolean ok = service.borrowMedia(5);
        assertThat(ok, is(true));
        assertThat(borrowingDAO.borrowed, is(true));
    }

    @Test
    void borrowMediaFailsWhenUnavailable() throws Exception {
        service.setLoggedUser(user);
        Book b = new Book(); b.setId(1); b.setAvailable(false);
        mediaDAO.m = b;
        boolean ok = service.borrowMedia(1);
        assertThat(ok, is(false));
        assertThat(borrowingDAO.borrowed, is(false));
    }

    @Test
    void borrowMediaFailsWhenUserHasBalance() throws Exception {
        service.setLoggedUser(user);
        Book b = new Book(); b.setId(7); b.setAvailable(true);
        mediaDAO.m = b;
        userDAO.balance = 50;
        boolean ok = service.borrowMedia(7);
        assertThat(ok, is(false));
    }

    @Test
    void returnMediaCallsDaoAndSucceeds() throws Exception {
        service.setLoggedUser(user);
        boolean ok = service.returnMedia(9);
        assertThat(ok, is(true));
        assertThat(borrowingDAO.returned, is(true));
    }

    @Test
    void payFineUpdatesFineDao() throws Exception {
        service.setLoggedUser(user);
        boolean ok = service.payFine(3, 20.0);
        assertThat(ok, is(true));
        assertThat(fineDAO.paid, is(true));
    }

    @Test
    void searchMediaReturnsList() throws Exception {
        List<Media> list = service.searchMedia("abc", "book");
        assertThat(list, is(not(empty())));
    }

    @Test
    void connectionGetterNotNull() {
        assertThat(service.getUserConnection(), is(notNullValue()));
    }
}
