package cli.tutoeasy.ui.menus;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.ui.handlers.*;

import java.util.Scanner;

/**
 * <p>Menu for student users.</p>
 * <p>Provides access to student-specific functions including:</p>
 * <ul>
 *     <li>Managing tutoring sessions (requests, upcoming, history)</li>
 *     <li>Communication (notifications, messages)</li>
 *     <li>Profile management</li>
 * </ul>
 */
public class StudentMenu extends BaseMenu {

    /**
     * <p>Constructs a new StudentMenu instance.</p>
     *
     * @param scanner The scanner used for user input.
     * @param factory The application factory to access services and repositories.
     */
    public StudentMenu(Scanner scanner, AppFactory factory) {
        super(scanner, factory);
    }

    /**
     * <p>Displays the student menu and handles user interactions.</p>
     *
     * @return {@code false} when the user wants to logout, triggering session closure.
     */
    @Override
    public boolean show() {
        while (true) {
            displayHeader("STUDENT MENU - " + AuthSession.getCurrentUser().getUsername());

            System.out.println("TUTORING");
            System.out.println("  1. Request tutoring");
            System.out.println("  2. View upcoming sessions");
            System.out.println("  3. View tutoring history");
            System.out.println("  4. Rate completed tutoring");
            System.out.println();
            System.out.println("COMMUNICATION");
            System.out.println("  5. View notifications");
            System.out.println("  6. View messages");
            System.out.println("  7. Send message");
            System.out.println();
            System.out.println("PROFILE");
            System.out.println("  8. View contact information");
            System.out.println("  9. Edit profile");
            System.out.println();
            System.out.println("0. Logout");
            System.out.println();

            int option = readIntInput("Select an option: ", 0, 9);

            switch (option) {
                case 1:
                    TutoringHandler.handleCreateRequest(scanner, factory);
                    break;
                case 2:
                    TutoringHandler.handleViewUpcoming(scanner, factory);
                    break;
                case 3:
                    TutoringHandler.handleViewHistory(scanner, factory);
                    break;
                case 4:
                    StudentRatingHandler.handleRateTutor(scanner, factory);
                    break;
                case 5:
                    NotificationHandler.handleViewNotifications(scanner, factory);
                    break;
                case 6:
                    MessageHandler.handleViewMessages(scanner, factory);
                    break;
                case 7:
                    MessageHandler.handleSendMessage(scanner, factory);
                    break;
                case 8:
                    ContactHandler.handleViewContact(scanner, factory);
                    break;
                case 9:
                    ProfileHandler.handleEditProfile(scanner, factory);
                    break;
                case 0:
                    return false;
            }
        }
    }
}