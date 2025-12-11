package cli.tutoeasy.model.dto;

import java.time.LocalDateTime;

/**
 * Data transfer object for creating feedback from a tutor about a student.
 *
 * <p>
 * This record encapsulates the information required for a tutor to
 * evaluate a student after a completed tutoring session. It is typically
 * used by the service layer to transfer validated data from the command
 * layer to the persistence layer.
 * </p>
 *
 * <p>
 * Each tutoring session can only receive one tutor feedback entry. The system ensures
 * that the tutoring belongs to the tutor, has been completed, and has not
 * been previously evaluated by the tutor.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 * CreateTutorFeedbackDto dto = new CreateTutorFeedbackDto(
 *     12,
 *     7,
 *     5,
 *     "Student showed excellent understanding of the topic",
 *     LocalDateTime.now()
 * );
 * </pre>
 *
 * <h3>Validation Rules:</h3>
 * <ul>
 *     <li>The tutoring session must exist</li>
 *     <li>The tutoring session must be completed</li>
 *     <li>The feedback must be submitted by the tutor who conducted the session</li>
 *     <li>Only one feedback from the tutor is allowed per tutoring session</li>
 * </ul>
 *
 * @param tutoringId Identifier of the tutoring session being evaluated
 * @param tutorId Identifier of the tutor submitting the feedback
 * @param rating Numerical rating given to the student (e.g., 1–5)
 * @param comment Textual comment describing the student's performance
 * @param createdAt Timestamp indicating when the feedback was created
 *
 * @version 1.0
 * @since 1.0
 * @see cli.tutoeasy.service.SessionFeedbackService
 * @see cli.tutoeasy.model.entities.SessionFeedback
 */
public record CreateTutorFeedbackDto(
        int tutoringId,
        int tutorId,
        int rating,
        String comment,
        LocalDateTime createdAt
) {}
