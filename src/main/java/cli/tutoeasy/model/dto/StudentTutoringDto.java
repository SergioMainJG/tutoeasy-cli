package cli.tutoeasy.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for displaying tutoring information from the student's perspective.
 * Contains all information a student needs to see about their tutoring sessions.
 *
 * @param tutoringId ID of the tutoring session
 * @param tutorUsername Username of the tutor
 * @param subjectName Name of the subject
 * @param topicName Name of the topic (can be null)
 * @param meetingDate Date of the tutoring session
 * @param meetingTime Time of the tutoring session
 * @param status Current status of the tutoring (unconfirmed, confirmed, canceled, completed)
 */
public record StudentTutoringDto(
        int tutoringId,
        String tutorUsername,
        String subjectName,
        String topicName,
        LocalDate meetingDate,
        LocalTime meetingTime,
        String status
) {
}