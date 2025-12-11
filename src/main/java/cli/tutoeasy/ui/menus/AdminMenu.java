package cli.tutoeasy.ui.menus;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.ui.handlers.*;

import java.util.Scanner;

/**
 * <p>Menu for admin users.</p>
 * <p>Provides access to administrative functions including:</p>
 * <ul>
 *     <li>Creating new administrators</li>
 *     <li>Managing reports</li>
 *     <li>Communication (notifications and messages)</li>
 *     <li>Profile management</li>
 * </ul>
 */
public class AdminMenu extends BaseMenu {

    /**
     * <p>Constructs a new AdminMenu.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory Factory for accessing services.
     */
    public AdminMenu(Scanner scanner, AppFactory factory) {
        super(scanner, factory);
    }

    /**
     * <p>Displays the admin menu and handles user interactions.</p>
     * <p>Continues to show the menu until the user chooses to logout.</p>
     *
     * @return {@code false} when user wants to logout, triggering session closure.
     */
    @Override
    public boolean show() {
        while (true) {
            displayHeader("ADMINISTRATOR MENU - " + AuthSession.getCurrentUser().getUsername());

            System.out.println("MANAGEMENT");
            System.out.println("  1. Create new administrator");
            System.out.println("  2. Manage reports");
            System.out.println();
            System.out.println("COMMUNICATION");
            System.out.println("  3. View notifications");
            System.out.println("  4. View messages");
            System.out.println("  5. Send message");
            System.out.println();
            System.out.println("PROFILE");
            System.out.println("  6. View contact information");
            System.out.println("  7. Edit profile");
            System.out.println();
            System.out.println("0. Logout");
            System.out.println();

            int option = readIntInput("Select an option: ", 0, 7);

            switch (option) {
                case 1:
                    AdminHandler.handleCreateAdmin(scanner, factory);
                    break;
                case 2:
                    ReportHandler.handleReports(scanner, factory);
                    break;
                case 3:
                    NotificationHandler.handleViewNotifications(scanner, factory);
                    break;
                case 4:
                    MessageHandler.handleViewMessages(scanner, factory);
                    break;
                case 5:
                    MessageHandler.handleSendMessage(scanner, factory);
                    break;
                case 6:
                    ContactHandler.handleViewContact(scanner, factory);
                    break;
                case 7:
                    ProfileHandler.handleEditProfile(scanner, factory);
                    break;
                case 0:
                    return false;
                default:
                    showError("Invalid option. Please select an option between 0 and 7.");
                    waitForEnter();
            }
        }
    }
}
