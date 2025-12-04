package cli.tutoeasy.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for updating a tutoring session (tutor version).
 * Tutors can only update date and time, not subject or topic.
 *
 * @param tutoringId ID of the tutoring to update (required)
 * @param meetingDate New meeting date (optional)
 * @param meetingTime New meeting time (optional)
 */
public record UpdateTutoringDto(
        int tutoringId,
        LocalDate meetingDate,
        LocalTime meetingTime
) {
}