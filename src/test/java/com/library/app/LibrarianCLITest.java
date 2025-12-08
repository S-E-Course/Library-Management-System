package com.library.app;

import com.library.service.LibrarianService;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.mockito.Mockito.*;

/**
 * Unit tests for LibrarianCLI menu
 */
public class LibrarianCLITest {

    /**
     * Tests detecting overdue media and then logging out.
     *
     * @throws Exception if CLI execution fails
     */
    @Test
    public void testDetectOverdueAndLogout() throws Exception {
        String input =
                "1\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        LibrarianService librarian = mock(LibrarianService.class);
        LibrarianCLI cli = new LibrarianCLI(scanner, librarian);
        cli.run();
    }

    /**
     * Tests logging out directly from the librarian menu.
     *
     * @throws Exception if CLI execution fails
     */
    @Test
    public void testLogoutDirectly() throws Exception {
        String input =
                "0\n";
        Scanner scanner = new Scanner(input);
        LibrarianService librarian = mock(LibrarianService.class);
        LibrarianCLI cli = new LibrarianCLI(scanner, librarian);
        cli.run();
    }
}
