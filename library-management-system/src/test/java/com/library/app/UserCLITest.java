package com.library.app;

import com.library.model.Book;
import com.library.model.Fine;
import com.library.model.Media;
import com.library.model.User;
import com.library.model.Borrowing;

import com.library.service.FineSummary;
import com.library.service.UserService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests UserCLI menu flows
 */
public class UserCLITest {

    /**
     * Creates a simple test user.
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
     * Tests searching media and then logging out.
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

        List<Media> results = new ArrayList<Media>();
        results.add(book);
        when(userService.searchMedia("how", "book")).thenReturn(results);

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();
    }

    /**
     * Tests borrowing media and then logging out.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testBorrowMediaAndLogout() throws Exception {
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
    }

    /**
     * Tests returning media and then logging out.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testReturnMediaAndLogout() throws Exception {
        String input =
                "3\n" +
                "1\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);

        UserService userService = mock(UserService.class);
        User user = createTestUser();
        when(userService.getLoggedUser()).thenReturn(user);

        List<Borrowing> borrowings = new ArrayList<>();
        when(userService.findBorrowings(user.getUserId())).thenReturn(borrowings);
        when(userService.returnMedia(1)).thenReturn(true);

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();
    }

    /**
     * Tests viewing the fine summary and then logging out.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testViewFineSummaryAndLogout() throws Exception {
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
    }

    /**
     * Tests paying a fine and then logging out.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testPayFineAndLogout() throws Exception {
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

        List<Fine> fines = new ArrayList<Fine>();
        fines.add(fine);
        when(userService.findFines(user.getUserId())).thenReturn(fines);
        when(userService.payFine(18, 60.0)).thenReturn(true);

        UserCLI cli = new UserCLI(scanner, userService);
        cli.run();
    }

    /**
     * Tests logging out directly from the user menu.
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
}
