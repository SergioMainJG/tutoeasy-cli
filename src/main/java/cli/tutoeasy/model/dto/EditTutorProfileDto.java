package cli.tutoeasy.model.dto;

import java.util.List;

/**
 * Data Transfer Object used to update a tutor's technical profile.
 *
 * @param tutorId       the unique identifier of the tutor whose profile will be edited.
 * @param subjectNames  the list of subject names the tutor wants to teach.
 * @param scheduleSlots the list of available time slots the tutor wants to set.
 */
public record EditTutorProfileDto(
        int tutorId,
        List<String> subjectNames,
        List<String> scheduleSlots
) { }
