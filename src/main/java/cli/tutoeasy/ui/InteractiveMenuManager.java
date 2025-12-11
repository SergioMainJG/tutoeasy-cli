package cli.tutoeasy.ui;

import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.ui.menus.*;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.command.AppFactory;

import java.util.Scanner;

/**
 * <p>Manages the interactive menu system for the TutoEasy application.</p>
 * <p>Handles navigation between different menus based on user authentication state.</p>
 */
public class InteractiveMenuManager {

    private final Scanner scanner;
    private final AppFactory factory;
    private boolean running;

    /**
     * <p>Constructs a new InteractiveMenuManager instance.</p>
     * <p>Initializes the scanner and application factory, and sets the running state to true.</p>
     */
    public InteractiveMenuManager() {
        this.scanner = new Scanner(System.in);
        this.factory = new AppFactory();
        this.running = true;
    }

    /**
     * <p>Starts the interactive menu system.</p>
     * <p>It initializes the session and enters the main loop, handling navigation
     * based on whether the user is logged in or not.</p>
     */
    public void start() {
        printWelcome();

        var sessionResult = AuthSession.initialize();

        while (running) {
            try {
                if (AuthSession.isLoggedIn()) {
                    User currentUser = AuthSession.getCurrentUser();
                    navigateToRoleMenu(currentUser);
                } else {
                    MainMenu mainMenu = new MainMenu(scanner, factory);
                    boolean loginSuccess = mainMenu.show();

                    if (!loginSuccess) {
                        running = false;
                    }
                }
            } catch (Exception e) {
                System.out.println("\n Error: " + e.getMessage());
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }

        cleanup();
    }

    /**
     * <p>Navigates to the appropriate menu based on the user's role.</p>
     *
     * @param user The currently logged-in user.
     */
    private void navigateToRoleMenu(User user) {
        switch (user.getRol()) {
            case student:
                StudentMenu studentMenu = new StudentMenu(scanner, factory);
                boolean continueStudent = studentMenu.show();
                if (!continueStudent) {
                    handleLogout();
                }
                break;

            case tutor:
                TutorMenu tutorMenu = new TutorMenu(scanner, factory);
                boolean continueTutor = tutorMenu.show();
                if (!continueTutor) {
                    handleLogout();
                }
                break;

            case admin:
                AdminMenu adminMenu = new AdminMenu(scanner, factory);
                boolean continueAdmin = adminMenu.show();
                if (!continueAdmin) {
                    handleLogout();
                }
                break;
        }
    }

    /**
     * <p>Handles the user logout process.</p>
     * <p>Logs out the current user and displays a farewell message.</p>
     */
    private void handleLogout() {
        String username = AuthSession.getCurrentUser().getUsername();
        AuthSession.logout();
        System.out.println("\n Session closed successfully. See you soon, " + username + "!");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Prints the welcome message to the console.</p>
     */
    private void printWelcome() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║                   WELCOME TO TUTOEASY                      ║");
        System.out.println("║                                                            ║");
        System.out.println("║                Tutoring Management System                  ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * <p>Performs cleanup operations before the application exits.</p>
     * <p>Displays a thank you message and closes the scanner.</p>
     */
    private void cleanup() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║                 Thank you for using TutoEasy               ║");
        System.out.println("║                       See you soon!                        ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        scanner.close();
    }

    /**
     * <p>Clears the console screen.</p>
     * <p>This method attempts to clear the screen using OS-specific commands.
     * If it fails, it prints multiple new lines to simulate a clear screen.</p>
     */
    public static void clearScreen() {
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
}
