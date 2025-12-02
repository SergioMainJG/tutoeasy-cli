package cli.tutoeasy.model.dto;

import java.time.LocalDateTime;

/**
 * Data transfer object for displaying notification information.
 *
 * @param id ID of the notification
 * @param message Content of the notification
 * @param type Type/category of the notification
 * @param createdAt Timestamp when the notification was created
 * @param wasRead Whether the notification has been read
 */
public record NotificationDto(
        int id,
        String message,
        String type,
        LocalDateTime createdAt,
        boolean wasRead
) {
}