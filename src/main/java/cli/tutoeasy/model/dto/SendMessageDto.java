package cli.tutoeasy.model.dto;

import java.time.LocalDateTime;

/**     
 * DTO for sending a new message
 * 
 * @param receiverUsername Username of the message recipient
 * @param content          Content of the message
 */
public record SendMessageDto(
        String receiverUsername,
        String content) {
}