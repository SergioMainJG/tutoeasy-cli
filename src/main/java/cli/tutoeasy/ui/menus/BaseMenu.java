package cli.tutoeasy.ui.menus;


import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.service.NotificationService;

import java.util.Scanner;

/**
 * <p>Base class for all interactive menus.</p>
 * <p>Provides common functionality like displaying headers, notifications, and input handling.</p>
 */
public abstract class BaseMenu {

    protected final Scanner scanner;
    protected final AppFactory factory;

    /**
     * <p>Constructs a new BaseMenu instance.</p>
     *
     * @param scanner The scanner used for user input.
     * @param factory The application factory to access services and repositories.
     */
    public BaseMenu(Scanner scanner, AppFactory factory) {
        this.scanner = scanner;
        this.factory = factory;
    }

    /**
     * <p>Shows the menu and handles user interaction.</p>
     *
     * @return {@code true} if the user should continue to the next menu, {@code false} if they should logout or exit.
     */
    public abstract boolean show();

    /**
     * <p>Displays the menu header with the given title and potential notifications.</p>
     *
     * @param title The title of the menu to be displayed in the header.
     */
    protected void displayHeader(String title) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  " + centerText(title, 56) + "  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        if (AuthSession.isLoggedIn()) {
            displayNotificationBadge();
        }
        System.out.println();
    }

    /**
     * <p>Displays a notification badge if there are unread notifications for the logged-in user.</p>
     */
    protected void displayNotificationBadge() {
        if (AuthSession.isLoggedIn()) {
            NotificationService notificationService = new NotificationService(
                    factory.getNotificationRepository(),
                    factory.getUserRepository()
            );

            int unreadCount = notificationService.getUnreadCount(
                    AuthSession.getCurrentUser().getId()
            );

            if (unreadCount > 0) {
                System.out.println();
                System.out.println(" You have " + unreadCount + " unread notification(s)");
                System.out.println("   (Option 'View notifications' to check)");
            }
        }
    }

    /**
     * <p>Centers the given text within a specified width.</p>
     *
     * @param text  The text to center.
     * @param width The total width to center the text within.
     * @return The centered text string.
     */
    protected String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }

    /**
     * <p>Reads an integer input from the user within a specified range.</p>
     *
     * @param prompt The message to display to the user.
     * @param min    The minimum valid value.
     * @param max    The maximum valid value.
     * @return The valid integer entered by the user.
     */
    protected int readIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println(" Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println(" Please enter a valid number");
            }
        }
    }

    /**
     * <p>Reads a string input from the user.</p>
     *
     * @param prompt The message to display to the user.
     * @return The trimmed string entered by the user.
     */
    protected String readStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * <p>Waits for the user to press the Enter key.</p>
     */
    protected void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Clears the console screen.</p>
     * <p>Attempts to use OS-specific commands to clear the screen.
     * If it fails, prints multiple empty lines.</p>
     */
    protected void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    /**
     * <p>Displays a success message.</p>
     *
     * @param message The success message to display.
     */
    protected void showSuccess(String message) {
        System.out.println("\n " + message);
    }

    /**
     * <p>Displays an error message.</p>
     *
     * @param message The error message to display.
     */
    protected void showError(String message) {
        System.out.println("\n " + message);
    }

    /**
     * <p>Displays an information message.</p>
     *
     * @param message The information message to display.
     */
    protected void showInfo(String message) {
        System.out.println("\n  " + message);
    }
}
