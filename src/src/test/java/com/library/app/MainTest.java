package com.library.app;

import com.library.service.AdminService;
import com.library.service.LibrarianService;
import com.library.service.UserService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Scanner;

/**
 * Test cases for role session logic in Main.
 */
public class MainTest {

    /**
     * call the admin session flow via reflection.
     */
    @Test
    public void testRunAdminSession() throws Exception {
        String input = "0\n";
        Scanner scanner = new Scanner(input);

        Method m = Main.class.getDeclaredMethod(
                "runAdmin", Scanner.class, String.class, String.class);
        m.setAccessible(true);

        m.invoke(null, scanner, "admin", "adminpass");
    }

    /**
     * call the librarian session flow via reflection.
     */
    @Test
    public void testRunLibrarianSession() throws Exception {
        String input = "0\n";
        Scanner scanner = new Scanner(input);

        Method m = Main.class.getDeclaredMethod(
                "runLibrarian", Scanner.class, String.class, String.class);
        m.setAccessible(true);

        m.invoke(null, scanner, "librarian", "libpass");
    }

    /**
     * call the user session flow via reflection.
     */
    @Test
    public void testRunUserSession() throws Exception {
        String input = "0\n";
        Scanner scanner = new Scanner(input);

        Method m = Main.class.getDeclaredMethod(
                "runUser", Scanner.class, String.class, String.class);
        m.setAccessible(true);

        m.invoke(null, scanner, "user", "userpass");
    }
}
