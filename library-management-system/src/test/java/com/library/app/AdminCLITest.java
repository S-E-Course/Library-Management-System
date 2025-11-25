package com.library.app;

import com.library.service.AdminService;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

/**
 * Tests basic AdminCLI menu
 */
public class AdminCLITest {

    /**
     * Tests listing media and then logging out.
     */
    @Test
    public void testListMediaAndLogout() throws Exception {
        String input =
                "2\n" +
                "media\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = new AdminService();
        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();
    }

    /**
     * Tests adding a media item and then logging out.
     */
    @Test
    public void testAddMediaAndLogout() throws Exception {
        String input =
                "1\n" +
                "Test Title\n" +
                "Test Author\n" +
                "1234567890\n" +
                "book\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = new AdminService();
        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();
    }

    /**
     * Tests removing a media item and then logging out.
     */
    @Test
    public void testRemoveMediaAndLogout() throws Exception {
        String input =
                "3\n" +
                "1\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = new AdminService();
        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();
    }

    /**
     * Tests adding a valid user and then logging out.
     */
    @Test
    public void testAddUserValidAndLogout() throws Exception {
        String input =
                "4\n" +
                "user1\n" +
                "user1@example.com\n" +
                "pass123\n" +
                "user\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = new AdminService();
        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();
    }

    /**
     * Tests adding a user with an invalid email format.
     */
    @Test
    public void testAddUserInvalidEmail() throws Exception {
        String input =
                "4\n" +
                "user2\n" +
                "invalid-email\n" +
                "pass123\n" +
                "user\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = new AdminService();
        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();
    }

    /**
     * Tests adding a user with an invalid role.
     */
    @Test
    public void testAddUserInvalidRole() throws Exception {
        String input =
                "4\n" +
                "user3\n" +
                "user3@example.com\n" +
                "pass123\n" +
                "invalidrole\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = new AdminService();
        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();
    }

    /**
     * Tests listing users and then logging out.
     */
    @Test
    public void testListUsersAndLogout() throws Exception {
        String input =
                "5\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = new AdminService();
        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();
    }

    /**
     * Tests removing a user and then logging out.
     */
    @Test
    public void testRemoveUserAndLogout() throws Exception {
        String input =
                "6\n" +
                "1\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = new AdminService();
        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();
    }

    /**
     * Tests sending overdue reminders and then logging out.
     */
    @Test
    public void testSendRemindersAndLogout() throws Exception {
        String input =
                "7\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = new AdminService();
        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();
    }

    /**
     * Tests logging out directly.
     */
    @Test
    public void testLogoutDirectly() throws Exception {
        String input = "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = new AdminService();
        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();
    }
}
