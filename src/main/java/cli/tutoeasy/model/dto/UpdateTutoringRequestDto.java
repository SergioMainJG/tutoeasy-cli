package cli.tutoeasy.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for updating a tutoring request
 * 
 * @param tutoringId  ID of the tutoring to update
 * @param topicName   New topic name (optional)
 * @param meetingDate New date
 * @param meetingTime New time
 */
public record UpdateTutoringRequestDto(
    int tutoringId,
    String topicName,
    LocalDate meetingDate,
    LocalTime meetingTime) {
}