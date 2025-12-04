package cli.tutoeasy.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for displaying tutoring history information.
 * Contains information about past tutoring sessions (completed or canceled).
 *
 * @param tutoringId ID of the tutoring session
 * @param tutorUsername Username of the tutor
 * @param subjectName Name of the subject
 * @param topicName Name of the topic (can be null)
 * @param meetingDate Date of the tutoring session
 * @param meetingTime Time of the tutoring session
 * @param status Status of the tutoring (completed or canceled)
 */
public record StudentTutoringHistoryDto(
        int tutoringId,
        String tutorUsername,
        String subjectName,
        String topicName,
        LocalDate meetingDate,
        LocalTime meetingTime,
        String status
) {
}