package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Represents a message entity in the database.
 * This class is mapped to the "Messages" table and contains information about
 * messages between users.
 */
@Entity
@Table(name = "Messages", indexes = {
        @Index(name = "idx_message_sender", columnList = "senderId"),
        @Index(name = "idx_message_receiver", columnList = "receiverId"),
        @Index(name = "idx_message_receiver_read", columnList = "receiverId, wasRead"),
        @Index(name = "idx_message_created", columnList = "createdAt"),
        @Index(name = "idx_message_conversation", columnList = "senderId, receiverId, createdAt")
})
@Getter
@Setter
@NoArgsConstructor
public class Message {

    /**
     * The unique identifier for the message.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The user who sent the message.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senderId", nullable = false)
    private User sender;

    /**
     * The user who receives the message.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiverId", nullable = false)
    private User receiver;

    /**
     * The content of the message.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * The timestamp when the message was created.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Indicates whether the message has been read.
     */
    @Column(nullable = false)
    private boolean wasRead = false;

    /**
     * Sets the creation timestamp before persisting.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}