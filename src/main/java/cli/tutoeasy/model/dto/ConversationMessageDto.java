package cli.tutoeasy.model.dto;

import java.time.LocalDateTime;

/**
 * DTO for conversation message (simplified view)
 * 
 * @param messageId     ID of the message
 * @param otherUsername Username of the other party (sender if received,
 *                      receiver if sent)
 * @param content       Content of the message
 * @param createdAt     Timestamp
 * @param wasRead       Read status
 * @param isSent        Whether this message was sent by current user (true) or
 *                      received (false)
 */
public record ConversationMessageDto(
    int messageId,
    String otherUsername,
    String content,
    LocalDateTime createdAt,
    boolean wasRead,
    boolean isSent) {
  /**
   * Formats the message for display
   */
  public String toFormattedString() {
    String direction = isSent ? "→" : "←";
    String readStatus = wasRead ? "✓" : "○";
    return String.format("[%s] %s %s: %s [%s]",
        createdAt.toString().substring(0, 16),
        direction,
        otherUsername,
        content,
        readStatus);
  }
}