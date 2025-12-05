package cli.tutoeasy.model.dto;

import java.time.LocalDateTime;

/**
 * DTO for displaying message information
 * 
 * @param messageId        ID of the message
 * @param senderUsername   Username of the sender
 * @param receiverUsername Username of the receiver
 * @param content          Content of the message
 * @param createdAt        Timestamp when message was created
 * @param wasRead          Whether the message has been read
 */
public record MessageInfoDto(
    int messageId,
    String senderUsername,
    String receiverUsername,
    String content,
    LocalDateTime createdAt,
    boolean wasRead) {
}