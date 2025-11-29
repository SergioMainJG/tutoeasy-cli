package cli.tutoeasy.model.dto;

/**
 * Data transfer object for a tutor's action on a tutoring session.
 * This record holds the information required for a tutor to accept or reject a tutoring session.
 *
 * @param tutoringId The ID of the tutoring session.
 * @param tutorId    The ID of the tutor.
 * @param accepted   A boolean indicating whether the tutor accepted the tutoring session.
 */
public record TutorTutoringActionDto(
        int tutoringId,
        int tutorId,
        boolean accepted
) { }
