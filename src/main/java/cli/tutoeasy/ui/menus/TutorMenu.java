package cli.tutoeasy.ui.menus;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.ui.handlers.*;

import java.util.Scanner;

/**
 * <p>Menu for tutor users.</p>
 * <p>Provides access to tutor-specific functions including:</p>
 * <ul>
 *     <li>Managing tutoring requests and sessions</li>
 *     <li>Managing tutor profile (subjects, schedule)</li>
 *     <li>Communication (notifications, messages)</li>
 *     <li>Profile management</li>
 * </ul>
 */
public class TutorMenu extends BaseMenu {

    /**
     * <p>Constructs a new TutorMenu instance.</p>
     *
     * @param scanner The scanner used for user input.
     * @param factory The application factory to access services and repositories.
     */
    public TutorMenu(Scanner scanner, AppFactory factory) {
        super(scanner, factory);
    }

    /**
     * <p>Displays the tutor menu and handles user interactions.</p>
     *
     * @return {@code false} when the user wants to logout, triggering session closure.
     */
    @Override
    public boolean show() {
        while (true) {
            displayHeader("TUTOR MENU - " + AuthSession.getCurrentUser().getUsername());

            System.out.println("REQUESTS");
            System.out.println("  1. View pending requests");
            System.out.println("  2. View confirmed sessions");
            System.out.println();
            System.out.println("TUTOR PROFILE");
            System.out.println("  3. Edit subjects and schedule");
            System.out.println();
            System.out.println("COMMUNICATION");
            System.out.println("  4. View notifications");
            System.out.println("  5. View messages");
            System.out.println("  6. Send message");
            System.out.println();
            System.out.println("PROFILE");
            System.out.println("  7. View contact information");
            System.out.println("  8. Edit profile");
            System.out.println();
            System.out.println("0. Logout");
            System.out.println();

            int option = readIntInput("Select an option: ", 0, 8);

            switch (option) {
                case 1:
                    TutorRequestHandler.handlePendingRequests(scanner, factory);
                    break;
                case 2:
                    TutorRequestHandler.handleConfirmedSessions(scanner, factory);
                    break;
                case 3:
                    TutorProfileHandler.handleEditTutorProfile(scanner, factory);
                    break;
                case 4:
                    NotificationHandler.handleViewNotifications(scanner, factory);
                    break;
                case 5:
                    MessageHandler.handleViewMessages(scanner, factory);
                    break;
                case 6:
                    MessageHandler.handleSendMessage(scanner, factory);
                    break;
                case 7:
                    ContactHandler.handleViewContact(scanner, factory);
                    break;
                case 8:
                    ProfileHandler.handleEditProfile(scanner, factory);
                    break;
                case 0:
                    return false;
            }
        }
    }
}