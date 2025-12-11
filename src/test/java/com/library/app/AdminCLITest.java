package com.library.app;

import com.library.model.Media;
import com.library.model.User;
import com.library.service.AdminService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

/**
 * Tests for AdminCLI using Mockito.
 */
public class AdminCLITest {

    /**
     * Add media as a book and verify AdminService.addMedia is called.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testAddMediaFlowBookCallsService() throws Exception {
        String input =
                "1\n" +
                "Book Title\n" +
                "Book Author\n" +
                "BISBN\n" +
                "book\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);
        when(admin.addMedia(any(Media.class))).thenReturn(true);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).addMedia(any(Media.class));
    }

    /**
     * Add media as a CD and verify the service is called.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testAddMediaFlowCdCallsService() throws Exception {
        String input =
                "1\n" +
                "CD Title\n" +
                "CD Author\n" +
                "CDISBN\n" +
                "cd\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);
        when(admin.addMedia(any(Media.class))).thenReturn(true);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).addMedia(any(Media.class));
    }

    /**
     * Add media as a journal and verify the service is called.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testAddMediaFlowJournalCallsService() throws Exception {
        String input =
                "1\n" +
                "Journal Title\n" +
                "Journal Author\n" +
                "JISBN\n" +
                "journal\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);
        when(admin.addMedia(any(Media.class))).thenReturn(true);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).addMedia(any(Media.class));
    }

    /**
     * List media flow should call AdminService.listAllMedia.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testListMediaFlowCallsService() throws Exception {
        String input =
                "2\n" +
                "media\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);
        List<Media> list = new ArrayList<>();
        when(admin.listAllMedia("media")).thenReturn(list);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).listAllMedia("media");
    }

    /**
     * Remove media flow when service returns true.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testRemoveMediaFlowSuccess() throws Exception {
        String input =
                "3\n" +
                "5\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);
        when(admin.removeMedia(5)).thenReturn(true);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).removeMedia(5);
    }

    /**
     * Remove media flow when service returns false.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testRemoveMediaFlowFail() throws Exception {
        String input =
                "3\n" +
                "5\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);
        when(admin.removeMedia(5)).thenReturn(false);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).removeMedia(5);
    }

    /**
     * Add user with valid data and service returns true.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testAddUserFlowValidServiceTrue() throws Exception {
        String input =
                "4\n" +
                "user1\n" +
                "user1@example.com\n" +
                "pass123\n" +
                "user\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);
        when(admin.addUser("user1", "user1@example.com", "pass123", "user"))
                .thenReturn(true);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).addUser("user1", "user1@example.com", "pass123", "user");
    }

    /**
     * Add user with valid data but service returns false.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testAddUserFlowValidServiceFalse() throws Exception {
        String input =
                "4\n" +
                "userx\n" +
                "userx@example.com\n" +
                "pass123\n" +
                "user\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);
        when(admin.addUser("userx", "userx@example.com", "pass123", "user"))
                .thenReturn(false);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).addUser("userx", "userx@example.com", "pass123", "user");
    }

    /**
     * Add user with invalid email should not call AdminService.addUser.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testAddUserFlowInvalidEmailDoesNotCallService() throws Exception {
        String input =
                "4\n" +
                "user2\n" +
                "invalid-email\n" +
                "pass123\n" +
                "user\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin, never()).addUser(anyString(), anyString(), anyString(), anyString());
    }

    /**
     * Add user with invalid role should not call AdminService.addUser.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testAddUserFlowInvalidRoleDoesNotCallService() throws Exception {
        String input =
                "4\n" +
                "user3\n" +
                "user3@example.com\n" +
                "pass123\n" +
                "invalidrole\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin, never()).addUser(anyString(), anyString(), anyString(), anyString());
    }

    /**
     * List users when the list is empty.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testListUsersFlowEmptyList() throws Exception {
        String input =
                "5\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);
        List<User> users = new ArrayList<>();
        when(admin.listUsers()).thenReturn(users);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).listUsers();
    }

    /**
     * List users when the list is non-empty.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testListUsersFlowNonEmptyList() throws Exception {
        String input =
                "5\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);

        List<User> users = new ArrayList<>();
        User u = new User();
        u.setUserId(1);
        u.setUsername("u1");
        u.setEmail("u1@example.com");
        u.setRole("user");
        u.setBalance(0.0);
        users.add(u);

        when(admin.listUsers()).thenReturn(users);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).listUsers();
    }

    /**
     * Remove user when service returns true.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testRemoveUserFlowSuccess() throws Exception {
        String input =
                "6\n" +
                "9\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);
        when(admin.removeUser(9)).thenReturn(true);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).removeUser(9);
    }

    /**
     * Remove user when service returns false.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testRemoveUserFlowFail() throws Exception {
        String input =
                "6\n" +
                "9\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);
        when(admin.removeUser(9)).thenReturn(false);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).removeUser(9);
    }

    /**
     * Send reminders flow should call AdminService.sendOverdueRemindersFromEnv.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testSendRemindersFlowCallsService() throws Exception {
        String input =
                "7\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);
        when(admin.sendOverdueRemindersFromEnv()).thenReturn(2);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).sendOverdueRemindersFromEnv();
    }

    /**
     * Run with choice 0 only to cover direct logout branch.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testLogoutDirectly() throws Exception {
        String input = "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        // No specific interaction required, just covering branch 0.
    }

    /**
     * Cover the catch block in run() by making addMedia throw an exception.
     *
     * @throws Exception if the CLI run fails
     */
    @Test
    public void testRunCatchesExceptionFromFlow() throws Exception {
        String input =
                "1\n" +
                "Title\n" +
                "Author\n" +
                "ISBN\n" +
                "book\n" +
                "\n" +
                "0\n";
        Scanner scanner = new Scanner(input);
        AdminService admin = mock(AdminService.class);
        when(admin.addMedia(any(Media.class))).thenThrow(new RuntimeException("boom"));

        AdminCLI cli = new AdminCLI(scanner, admin);
        cli.run();

        verify(admin).addMedia(any(Media.class));
    }
}
