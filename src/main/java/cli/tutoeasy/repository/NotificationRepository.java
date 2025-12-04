package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.Notification;
import cli.tutoeasy.model.entities.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing Notification entities.
 */
public class NotificationRepository extends BaseRepository<Notification> {

    public NotificationRepository() {
        super(Notification.class);
    }

    /**
     * Creates a notification for a user
     *
     * @param user    User to notify
     * @param message Notification message
     * @param type    Type of notification
     */
    public void createNotification(User user, String message, String type) {
        executeInTransaction(em -> {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setMessage(message);
            notification.setType(type);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setWasRead(false);
            em.persist(notification);
        });
    }

    /**
     * Finds all notifications for a user
     *
     * @param userId ID of the user
     * @return List of notifications
     */
    public List<Notification> findByUser(int userId) {
        return executeQuery(em -> em.createQuery("""
        SELECT n FROM Notification n
        WHERE n.user.id = :userId
        ORDER BY n.createdAt DESC
        """, Notification.class)
                .setParameter("userId", userId)
                .getResultList());
    }

    /**
     * Finds unread notifications for a user
     *
     * @param userId ID of the user
     * @return List of unread notifications
     */
    public List<Notification> findUnreadByUser(int userId) {
        return executeQuery(em -> em.createQuery("""
        SELECT n FROM Notification n
        WHERE n.user.id = :userId
        AND n.wasRead = false
        ORDER BY n.createdAt DESC
        """, Notification.class)
                .setParameter("userId", userId)
                .getResultList());
    }

    /**
     * Marks a notification as read
     *
     * @param notificationId ID of the notification
     */
    public void markAsRead(int notificationId) {
        executeInTransaction(em -> {
            Notification notification = em.find(Notification.class, notificationId);
            if (notification != null) {
                notification.setWasRead(true);
            }
        });
    }

    /**
     * Marks all notifications of a user as read
     *
     * @param userId ID of the user
     */
    public void markAllAsRead(int userId) {
        executeInTransaction(em -> {
            em.createQuery("""
          UPDATE Notification n
          SET n.wasRead = true
          WHERE n.user.id = :userId
          AND n.wasRead = false
          """)
                    .setParameter("userId", userId)
                    .executeUpdate();
        });
    }
}