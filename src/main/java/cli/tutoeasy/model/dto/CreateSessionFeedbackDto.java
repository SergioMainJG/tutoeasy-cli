package cli.tutoeasy.model.dto;

import java.time.LocalDateTime;

/**
 * Data transfer object for creating feedback for a completed tutoring session.
 *
 * <p>
 * This record encapsulates the information required to register a student's
 * evaluation of a tutoring session. It is typically used by the service layer
 * to transfer validated data from the command layer to the persistence layer.
 * </p>
 *
 * <p>
 * Each tutoring session can only receive one feedback entry. The system ensures
 * that the tutoring belongs to the student, has been completed, and has not
 * been previously evaluated.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 * CreateSessionFeedbackDto dto = new CreateSessionFeedbackDto(
 *     12,
 *     5,
 *     4,
 *     "Very clear explanations and great support",
 *     LocalDateTime.now()
 * );
 * </pre>
 *
 * <h3>Validation Rules:</h3>
 * <ul>
 *     <li>The tutoring session must exist</li>
 *     <li>The tutoring session must be completed</li>
 *     <li>The feedback must be submitted by the student who attended the session</li>
 *     <li>Only one feedback is allowed per tutoring session</li>
 * </ul>
 *
 * @param tutoringId Identifier of the tutoring session being evaluated
 * @param studentId Identifier of the student submitting the feedback
 * @param rating Numerical rating given by the student (e.g., 1–5)
 * @param comment Textual comment describing the student's experience
 * @param createdAt Timestamp indicating when the feedback was created
 *
 * @version 1.0
 * @since 1.0
 * @see cli.tutoeasy.service.SessionFeedbackService
 * @see cli.tutoeasy.model.entities.SessionFeedback
 */
public record CreateSessionFeedbackDto(
        int tutoringId,
        int studentId,
        int rating,
        String comment,
        LocalDateTime createdAt
) {
}
