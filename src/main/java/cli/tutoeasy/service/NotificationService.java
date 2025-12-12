package cli.tutoeasy.service;

import cli.tutoeasy.model.entities.Notification;
import cli.tutoeasy.model.entities.Tutoring;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.repository.NotificationRepository;
import cli.tutoeasy.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing user notifications.
 * Provides methods to create, retrieve, and mark notifications as read.
 */
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Constructor with dependency injection
     *
     * @param notificationRepository Repository for notification operations
     * @param userRepository Repository for user operations
     */
    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Adds a new notification for a user with a specific type
     *
     * @param userId ID of the user to notify
     * @param message Message content of the notification
     * @param type Type/category of the notification (optional)
     * @return true if notification was created successfully, false otherwise
     */
    public boolean addNotification(int userId, String message, String type) {
        try {
            User user = userRepository.findById(userId);
            if (user == null) {
                return false;
            }

            notificationRepository.createNotification(user, message, type);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets notifications for a user. If limit is not specified, retrieves the last
     * 10 unread notifications. Marks all retrieved notifications as read.
     *
     * @param userId ID of the user
     * @param limit Optional number of notifications to retrieve (null = 10 unread)
     * @return List of notifications
     */
    public List<Notification> getNotifications(int userId, Integer limit) {
        List<Notification> notifications;

        if (limit == null) {
            notifications = notificationRepository.findUnreadByUser(userId);
            if (notifications.size() > 10) {
                notifications = notifications.subList(0, 10);
            }
        } else {
            notifications = notificationRepository.findByUser(userId);
            if (notifications.size() > limit) {
                notifications = notifications.subList(0, limit);
            }
        }

        for (Notification notification : notifications) {
            if (!notification.isWasRead()) {
                notificationRepository.markAsRead(notification.getId());
            }
        }

        return notifications;
    }

    /**
     * Gets notifications for a user (default: last 10 unread)
     *
     * @param userId ID of the user
     * @return List of notifications
     */
    public List<Notification> getNotifications(int userId) {
        return getNotifications(userId, null);
    }

    /**
     * Marks a specific notification as read
     *
     * @param notificationId ID of the notification
     */
    public void markAsRead(int notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    /**
     * Marks all notifications of a user as read
     *
     * @param userId ID of the user
     */
    public void markAllAsRead(int userId) {
        notificationRepository.markAllAsRead(userId);
    }

    /**
     * Gets count of unread notifications for a user
     *
     * @param userId ID of the user
     * @return Number of unread notifications
     */
    public int getUnreadCount(int userId) {
        return notificationRepository.findUnreadByUser(userId).size();
    }

    /**
     * Notifies the tutor about a specific tutoring session
     * @param t Tutoring session
     * @param type Notification type ("tutoring_confirmed", "tutoring_cancelled", "reminder_1day", etc.)
     */
    public void notifyTutorForSession(Tutoring t, String type) {
        if (t == null || t.getTutor() == null) return;

        String message = String.format(
                "Tutoring session with %s for %s on %s at %s",
                t.getStudent().getUsername(),
                t.getSubject().getName(),
                t.getMeetingDate(),
                t.getMeetingTime()
        );

        addNotification(t.getTutor().getId(), message, type);
    }
}