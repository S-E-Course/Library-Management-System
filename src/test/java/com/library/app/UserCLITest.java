package com.library.app;

import com.library.model.Book;
import com.library.model.Borrowing;
import com.library.model.Fine;
import com.library.model.Media;
import com.library.model.User;
import com.library.service.FineSummary;
import com.library.service.UserService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

/**
 * Tests the UserCLI menu flows using Mockito.
 */
public class UserCLITest {

    /**
     * Creates a simple test user with id 1.
     *
     * @return user with id 1
     */
    private User createTestUser() {
        User u = new User();
        u.setUserId(1);
        u.setUsername("u");
        return u;
    }

    /**
     * Tests search media flow and logout.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testSearchMediaAndLogout() throws Exception {
        String input =
                "1\n" +
                "book\n" +
                "how\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);

        UserService userService = mock(UserService.class);
        User user = createTestUser();
        when(userService.getLoggedUser()).thenReturn(user);

        Book book = new Book();
        book.setId(1);
        book.setTitle("How to Test");
        book.setAuthor("Someone");
        book.setIsbn("123");
        book.setAvailable(true);

        List<Media> results = new ArrayList<>();
        results.add(book);
        when(userService.searchMedia("how", "book")).thenReturn(results);

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();

        verify(userService).searchMedia("how", "book");
    }

    /**
     * Tests borrow media flow when borrowing succeeds.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testBorrowMediaSuccessAndLogout() throws Exception {
        String input =
                "2\n" +
                "1\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);

        UserService userService = mock(UserService.class);
        User user = createTestUser();
        when(userService.getLoggedUser()).thenReturn(user);
        when(userService.borrowMedia(1)).thenReturn(true);

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();

        verify(userService).borrowMedia(1);
    }

    /**
     * Tests borrow media flow when borrowing fails.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testBorrowMediaFailAndLogout() throws Exception {
        String input =
                "2\n" +
                "2\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);

        UserService userService = mock(UserService.class);
        User user = createTestUser();
        when(userService.getLoggedUser()).thenReturn(user);
        when(userService.borrowMedia(2)).thenReturn(false);

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();

        verify(userService).borrowMedia(2);
    }

    /**
     * Tests return media flow when returning succeeds.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testReturnMediaSuccessAndLogout() throws Exception {
        String input =
                "3\n" +   // choose "Return Media"
                "1\n" +   // media id
                "\n" +    // press ENTER to continue
                "0\n";    // logout
        Scanner scanner = new Scanner(input);

        UserService userService = mock(UserService.class);
        User user = createTestUser();
        when(userService.getLoggedUser()).thenReturn(user);

        when(userService.findBorrowings(user.getUserId()))
                .thenReturn(new ArrayList<>());

        java.sql.Connection conn = mock(java.sql.Connection.class);
        when(userService.getUserConnection()).thenReturn(conn);

        when(userService.returnMedia(1)).thenReturn(true);

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();

        verify(userService).returnMedia(1);
    }


    /**
     * Tests return media flow when returning fails.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testReturnMediaFailAndLogout() throws Exception {
        String input =
                "3\n" +
                "2\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);

        UserService userService = mock(UserService.class);
        User user = createTestUser();
        when(userService.getLoggedUser()).thenReturn(user);

        List<Borrowing> borrowings = new ArrayList<>();
        when(userService.findBorrowings(user.getUserId())).thenReturn(borrowings);
        when(userService.returnMedia(2)).thenReturn(false);

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();

        verify(userService).findBorrowings(user.getUserId());
        verify(userService).returnMedia(2);
    }

    /**
     * Tests viewing fine summary when it loads successfully.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testViewFineSummarySuccessAndLogout() throws Exception {
        String input =
                "4\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);

        UserService userService = mock(UserService.class);
        User user = createTestUser();
        when(userService.getLoggedUser()).thenReturn(user);

        FineSummary summary = new FineSummary();
        summary.add("book", 10.0);
        when(userService.getFineSummary()).thenReturn(summary);

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();

        verify(userService).getFineSummary();
    }

    /**
     * Tests viewing fine summary when an exception is thrown.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testViewFineSummaryHandlesException() throws Exception {
        String input =
                "4\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);

        UserService userService = mock(UserService.class);
        User user = createTestUser();
        when(userService.getLoggedUser()).thenReturn(user);
        when(userService.getFineSummary()).thenThrow(new RuntimeException("boom"));

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();

        verify(userService).getFineSummary();
    }

    /**
     * Tests paying a fine when payment succeeds.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testPayFineSuccessAndLogout() throws Exception {
        String input =
                "5\n" +
                "18\n" +
                "60\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);

        UserService userService = mock(UserService.class);
        User user = createTestUser();
        when(userService.getLoggedUser()).thenReturn(user);

        Fine fine = new Fine();
        fine.setId(18);
        fine.setUserId(user.getUserId());
        fine.setAmount(60.0);

        List<Fine> fines = new ArrayList<>();
        fines.add(fine);
        when(userService.findFines(user.getUserId())).thenReturn(fines);
        when(userService.payFine(18, 60.0)).thenReturn(true);

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();

        verify(userService).findFines(user.getUserId());
        verify(userService).payFine(18, 60.0);
    }

    /**
     * Tests paying a fine when payment fails.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testPayFineFailAndLogout() throws Exception {
        String input =
                "5\n" +
                "20\n" +
                "30\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);

        UserService userService = mock(UserService.class);
        User user = createTestUser();
        when(userService.getLoggedUser()).thenReturn(user);

        Fine fine = new Fine();
        fine.setId(20);
        fine.setUserId(user.getUserId());
        fine.setAmount(30.0);

        List<Fine> fines = new ArrayList<>();
        fines.add(fine);
        when(userService.findFines(user.getUserId())).thenReturn(fines);
        when(userService.payFine(20, 30.0)).thenReturn(false);

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();

        verify(userService).findFines(user.getUserId());
        verify(userService).payFine(20, 30.0);
    }

    /**
     * Tests logout directly from the menu.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testLogoutDirectly() throws Exception {
        String input = "0\n";
        Scanner scanner = new Scanner(input);

        UserService userService = mock(UserService.class);
        User user = createTestUser();
        when(userService.getLoggedUser()).thenReturn(user);

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();
    }

    /**
     * Tests that the run loop catch block is executed when searchMedia throws.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testRunCatchesExceptionFromSearch() throws Exception {
        String input =
                "1\n" +
                "media\n" +
                "x\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);

        UserService userService = mock(UserService.class);
        User user = createTestUser();
        when(userService.getLoggedUser()).thenReturn(user);
        when(userService.searchMedia("x", "media")).thenThrow(new RuntimeException("search-error"));

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();

        verify(userService).searchMedia("x", "media");
    }
}
