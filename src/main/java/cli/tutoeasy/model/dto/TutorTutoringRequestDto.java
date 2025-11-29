package cli.tutoeasy.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data transfer object for a tutor tutoring request.
 * This record holds the information required to display a tutoring request to a tutor.
 *
 * @param tutoringId  The ID of the tutoring session.
 * @param studentId   The ID of the student.
 * @param studentName The name of the student.
 * @param subjectName The name of the subject.
 * @param topicName   The name of the topic.
 * @param meetingDate The date of the meeting.
 * @param meetingTime The time of the meeting.
 */
public record TutorTutoringRequestDto(
        int tutoringId,
        int studentId,
        String studentName,
        String subjectName,
        String topicName,
        LocalDate meetingDate,
        LocalTime meetingTime
) {
}
