package cli.tutoeasy.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for creating a new tutoring request
 * 
 * @param subjectNameOrId Subject name or ID
 * @param tutorUsername   Username of the tutor
 * @param topicName       Topic name (optional)
 * @param meetingDate     Date of the tutoring session
 * @param meetingTime     Time of the tutoring session
 */
public record CreateTutoringRequestDto(
    String subjectNameOrId,
    String tutorUsername,
    String topicName,
    LocalDate meetingDate,
    LocalTime meetingTime) {
}