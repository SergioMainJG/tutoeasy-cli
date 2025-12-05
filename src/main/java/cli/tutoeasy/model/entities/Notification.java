package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Represents a notification entity in the database.
 * This class is mapped to the "Notifications" table and contains information about a notification.
 */
@Entity
@Table(name = "Notifications", indexes = {
    @Index(name = "idx_notification_user_read", columnList = "userId, wasRead"),
    @Index(name = "idx_notification_created", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    /**
     * The unique identifier for the notification.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The user who received the notification.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    /**
     * The message of the notification.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * The timestamp of when the notification was created.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Whether the notification has been read.
     */
    @Column(nullable = false)
    private boolean wasRead = false;

    /**
     * The type of the notification.
     */
    @Column(length = 20)
    private String type;
}