package com.library.app;

import com.library.service.LibrarianService;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.mockito.Mockito.*;

/**
 * Tests for LibrarianCLI
 */
public class LibrarianCLITest {

    /**
     * Executes the detect-overdue option followed by logout.
     *
     * @throws Exception if the method invocation fails
     */
    @Test
    public void testDetectOverdueAndLogout() throws Exception {
        String input = "1\n\n0\n";
        Scanner scanner = new Scanner(input);
        LibrarianService librarian = mock(LibrarianService.class);

        LibrarianCLI cli = new LibrarianCLI(scanner, librarian);
        cli.run();

        verify(librarian, times(1)).detectOverdueMedia();
        verifyNoMoreInteractions(librarian);
    }

    /**
     * Executes immediate logout.
     *
     * @throws Exception if the method invocation fails
     */
    @Test
    public void testLogoutDirectly() throws Exception {
        String input = "0\n";
        Scanner scanner = new Scanner(input);
        LibrarianService librarian = mock(LibrarianService.class);

        LibrarianCLI cli = new LibrarianCLI(scanner, librarian);
        cli.run();

        verifyNoInteractions(librarian);
    }

    /**
     * Executes detect-overdue option where the service throws an exception,
     * ensuring the exception-handling branch is executed.
     *
     * @throws Exception if the method invocation fails
     */
    @Test
    public void testDetectOverdueHandlesException() throws Exception {
        String input = "1\n\n0\n";
        Scanner scanner = new Scanner(input);
        LibrarianService librarian = mock(LibrarianService.class);

        doThrow(new Exception("failure")).when(librarian).detectOverdueMedia();

        LibrarianCLI cli = new LibrarianCLI(scanner, librarian);
        cli.run();

        verify(librarian, times(1)).detectOverdueMedia();
        verifyNoMoreInteractions(librarian);
    }
}
