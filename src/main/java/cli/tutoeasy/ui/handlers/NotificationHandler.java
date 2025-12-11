package cli.tutoeasy.ui.handlers;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.entities.Notification;
import cli.tutoeasy.service.NotificationService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * <p>Handler for notification-related operations.</p>
 * <p>Manages viewing and marking notifications as read.</p>
 */
public class NotificationHandler {

    /**
     * <p>Handles viewing notifications with multiple options.</p>
     * <ul>
     *     <li>View the last 10 unread notifications</li>
     *     <li>View all notifications (last 20)</li>
     *     <li>Mark all notifications as read</li>
     * </ul>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handleViewNotifications(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    NOTIFICATIONS                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        NotificationService service = factory.getNotificationService();
        int userId = AuthSession.getCurrentUser().getId();

        System.out.println("1. View last 10 unread");
        System.out.println("2. View all (last 20)");
        System.out.println("3. Mark all as read");
        System.out.println("0. Return");
        System.out.println();

        System.out.print("Select an option: ");
        String input = scanner.nextLine().trim();

        try {
            int option = Integer.parseInt(input);

            switch (option) {
                case 1:
                    List<Notification> unread = service.getNotifications(userId, null);
                    displayNotifications(unread);
                    break;
                case 2:
                    List<Notification> all = service.getNotifications(userId, 20);
                    displayNotifications(all);
                    break;
                case 3:
                    service.markAllAsRead(userId);
                    System.out.println("\n All notifications marked as read");
                    break;
                case 0:
                    return;
            }
        } catch (NumberFormatException e) {
            System.out.println("\n Invalid option");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Displays the list of notifications.</p>
     *
     * @param notifications List of notifications to display.
     */
    private static void displayNotifications(List<Notification> notifications) {
        if (notifications.isEmpty()) {
            System.out.println("\n No notifications");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println();

        for (int i = 0; i < notifications.size(); i++) {
            Notification notif = notifications.get(i);
            String status = notif.isWasRead() ? "[Read]" : "[New]";
            String typeSymbol = getTypeSymbol(notif.getType());

            System.out.println((i + 1) + ". " + status + " " + typeSymbol);
            System.out.println("   " + notif.getMessage());
            System.out.println("   " + notif.getCreatedAt().format(formatter));
            System.out.println();
        }
    }

    /**
     * <p>Gets a symbol representing the notification type.</p>
     *
     * @param type The notification type.
     * @return A string symbol/label.
     */
    private static String getTypeSymbol(String type) {
        if (type == null) return "[INFO]";
        return switch (type.toLowerCase()) {
            case "tutoring_request" -> "[REQ]";
            case "tutoring_confirmed" -> "[OK]";
            case "tutoring_canceled" -> "[X]";
            case "tutoring_completed" -> "[DONE]";
            case "tutoring_rejected" -> "[NO]";
            case "tutoring_updated" -> "[UPD]";
            case "message_received" -> "[MSG]";
            default -> "[INFO]";
        };
    }

    /**
     * <p>Clears the console screen.</p>
     */
    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }
}