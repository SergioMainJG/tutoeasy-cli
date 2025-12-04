package cli.tutoeasy.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for displaying tutoring information from the tutor's perspective.
 * Contains information about confirmed tutoring sessions.
 *
 * @param tutoringId ID of the tutoring session
 * @param studentUsername Username of the student
 * @param subjectName Name of the subject
 * @param topicName Name of the topic (can be null)
 * @param meetingDate Date of the tutoring session
 * @param meetingTime Time of the tutoring session
 * @param status Current status of the tutoring (confirmed)
 */
public record TutorTutoringDto(
        int tutoringId,
        String studentUsername,
        String subjectName,
        String topicName,
        LocalDate meetingDate,
        LocalTime meetingTime,
        String status
) {
}