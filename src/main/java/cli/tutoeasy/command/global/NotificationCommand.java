package cli.tutoeasy.command.global;

import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.entities.Notification;
import cli.tutoeasy.service.NotificationService;
import picocli.CommandLine.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Command for managing user notifications.
 * Allows users to view their notifications and manage their read status.
 */
@Command(name = "notifications", description = "View and manage your notifications", mixinStandardHelpOptions = true)
public class NotificationCommand implements Runnable {

    /**
     * Number of notifications to retrieve (default: 10 unread).
     */
    @Option(names = { "--limit", "-l" }, description = "Number of notifications to retrieve (default: 10 unread)")
    private Integer limit;

    /**
     * Mark all notifications as read.
     */
    @Option(names = { "--mark-all-read", "-m" }, description = "Mark all notifications as read")
    private boolean markAllRead;

    /**
     * Show only the count of unread notifications.
     */
    @Option(names = { "--count", "-c" }, description = "Show only the count of unread notifications")
    private boolean showCount;

    /**
     * The service responsible for handling notification-related operations.
     */
    private final NotificationService notificationService;

    /**
     * Constructor with dependency injection
     *
     * @param notificationService Service for notification operations
     */
    public NotificationCommand(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void run() {
        if (!AuthSession.isLoggedIn()) {
            String msg = Help.Ansi.AUTO.string("@|red You must be logged in to view notifications.|@");
            System.out.println(msg);
            return;
        }

        int userId = AuthSession.getCurrentUser().getId();

        try {
            if (markAllRead) {
                markAllAsRead(userId);
            } else if (showCount) {
                showUnreadCount(userId);
            } else {
                showNotifications(userId);
            }
        } catch (Exception e) {
            String msg = Help.Ansi.AUTO.string("@|red ERROR: " + e.getMessage() + "|@");
            System.out.println(msg);
        }
    }

    /**
     * Displays notifications for the user
     */
    private void showNotifications(int userId) {
        List<Notification> notifications = notificationService.getNotifications(userId, limit);

        if (notifications.isEmpty()) {
            String msg = Help.Ansi.AUTO.string("@|yellow You have no notifications.|@");
            System.out.println(msg);
            return;
        }

        System.out.println(Help.Ansi.AUTO.string("\n@|bold,cyan === Your Notifications ===|@\n"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (int i = 0; i < notifications.size(); i++) {
            Notification notif = notifications.get(i);

            String typeColor = getTypeColor(notif.getType());
            String readStatus = notif.isWasRead() ? "@|faint (read)|@" : "@|bold,yellow (new)|@";

            System.out.println(Help.Ansi.AUTO.string(String.format(
                    "@|bold %d.|@ %s",
                    i + 1,
                    readStatus)));

            if (notif.getType() != null) {
                System.out.println(Help.Ansi.AUTO.string(String.format(
                        "    Type: @|%s %s|@",
                        typeColor,
                        notif.getType())));
            }

            System.out.println(Help.Ansi.AUTO.string(String.format(
                    "    Message: @|white %s|@",
                    notif.getMessage())));

            System.out.println(Help.Ansi.AUTO.string(String.format(
                    "    Date: @|faint %s|@",
                    notif.getCreatedAt().format(formatter))));

            System.out.println();
        }

        if (limit == null) {
            System.out.println(Help.Ansi.AUTO.string(
                    "@|cyan Tip: These notifications are now marked as read.|@"));
        }

        System.out.println(Help.Ansi.AUTO.string(
                "@|cyan Commands:|@\n" +
                        "  --limit=N          Show N notifications\n" +
                        "  --mark-all-read    Mark all as read\n" +
                        "  --count            Show unread count"));
    }

    /**
     * Marks all notifications as read
     */
    private void markAllAsRead(int userId) {
        notificationService.markAllAsRead(userId);
        String msg = Help.Ansi.AUTO.string("@|green All notifications marked as read.|@");
        System.out.println(msg);
    }

    /**
     * Shows the count of unread notifications
     */
    private void showUnreadCount(int userId) {
        int count = notificationService.getUnreadCount(userId);

        if (count == 0) {
            String msg = Help.Ansi.AUTO.string("@|green You have no unread notifications.|@");
            System.out.println(msg);
        } else {
            String msg = Help.Ansi.AUTO.string(String.format(
                    "@|yellow You have %d unread notification%s.|@",
                    count,
                    count == 1 ? "" : "s"));
            System.out.println(msg);
        }
    }

    /**
     * Gets color for notification type
     */
    private String getTypeColor(String type) {
        if (type == null) {
            return "white";
        }

        return switch (type.toLowerCase()) {
            case "tutoring_request" -> "cyan";
            case "tutoring_canceled" -> "red";
            case "tutoring_completed" -> "blue";
            case "tutoring_confirmed" -> "green";
            case "tutoring_rejected" -> "red";
            case "tutoring_updated" -> "yellow";
            default -> "white";
        };
    }
}