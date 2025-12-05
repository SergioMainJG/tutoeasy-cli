package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.Message;
import java.util.List;

/**
 * Repository for managing Message entities.
 * Provides optimized queries for messaging operations.
 */
public class MessageRepository extends BaseRepository<Message> {

  public MessageRepository() {
    super(Message.class);
  }

  /**
   * Finds all messages received by a user (inbox)
   * Ordered by creation date descending (newest first)
   * 
   * @param receiverId ID of the receiver
   * @return List of messages with sender information loaded
   */
  public List<Message> findReceivedMessages(int receiverId) {
    return executeQuery(em -> em.createQuery("""
        SELECT m FROM Message m
        LEFT JOIN FETCH m.sender
        WHERE m.receiver.id = :receiverId
        ORDER BY m.createdAt DESC
        """, Message.class)
        .setParameter("receiverId", receiverId)
        .getResultList());
  }

  /**
   * Finds unread messages for a user
   * 
   * @param receiverId ID of the receiver
   * @return List of unread messages
   */
  public List<Message> findUnreadMessages(int receiverId) {
    return executeQuery(em -> em.createQuery("""
        SELECT m FROM Message m
        LEFT JOIN FETCH m.sender
        WHERE m.receiver.id = :receiverId
        AND m.wasRead = false
        ORDER BY m.createdAt DESC
        """, Message.class)
        .setParameter("receiverId", receiverId)
        .getResultList());
  }

  /**
   * Finds messages in a conversation between two users
   * 
   * @param userId1 ID of first user
   * @param userId2 ID of second user
   * @return List of messages ordered by creation date ascending
   */
  public List<Message> findConversation(int userId1, int userId2) {
    return executeQuery(em -> em.createQuery("""
        SELECT m FROM Message m
        LEFT JOIN FETCH m.sender
        LEFT JOIN FETCH m.receiver
        WHERE (m.sender.id = :user1 AND m.receiver.id = :user2)
           OR (m.sender.id = :user2 AND m.receiver.id = :user1)
        ORDER BY m.createdAt ASC
        """, Message.class)
        .setParameter("user1", userId1)
        .setParameter("user2", userId2)
        .getResultList());
  }

  /**
   * Finds messages from a specific sender to current user
   * 
   * @param receiverId ID of the receiver
   * @param senderId   ID of the sender
   * @return List of messages from that sender
   */
  public List<Message> findMessagesFrom(int receiverId, int senderId) {
    return executeQuery(em -> em.createQuery("""
        SELECT m FROM Message m
        LEFT JOIN FETCH m.sender
        WHERE m.receiver.id = :receiverId
        AND m.sender.id = :senderId
        ORDER BY m.createdAt DESC
        """, Message.class)
        .setParameter("receiverId", receiverId)
        .setParameter("senderId", senderId)
        .getResultList());
  }

  /**
   * Marks a message as read
   * 
   * @param messageId ID of the message
   */
  public void markAsRead(int messageId) {
    executeInTransaction(em -> {
      Message message = em.find(Message.class, messageId);
      if (message != null) {
        message.setWasRead(true);
      }
    });
  }

  /**
   * Marks all messages from a sender as read
   * 
   * @param receiverId ID of the receiver
   * @param senderId   ID of the sender
   */
  public void markAllFromSenderAsRead(int receiverId, int senderId) {
    executeInTransaction(em -> {
      em.createQuery("""
          UPDATE Message m
          SET m.wasRead = true
          WHERE m.receiver.id = :receiverId
          AND m.sender.id = :senderId
          AND m.wasRead = false
          """)
          .setParameter("receiverId", receiverId)
          .setParameter("senderId", senderId)
          .executeUpdate();
    });
  }

  /**
   * Counts unread messages for a user
   * 
   * @param receiverId ID of the receiver
   * @return Number of unread messages
   */
  public long countUnreadMessages(int receiverId) {
    return executeQuery(em -> em.createQuery("""
        SELECT COUNT(m)
        FROM Message m
        WHERE m.receiver.id = :receiverId
        AND m.wasRead = false
        """, Long.class)
        .setParameter("receiverId", receiverId)
        .getSingleResult());
  }
}