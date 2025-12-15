package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents the feedback provided for a completed tutoring session.
 *
 * <p>
 * This entity is mapped to the {@code SessionFeedback} table and stores
 * evaluations made by students or tutors. Feedback includes a numeric rating
 * and a textual comment.
 * </p>
 *
 * <p>
 * Instead of using direct JPA relationships, this entity stores only the
 * identifiers (IDs) of the related tutoring session, student, and tutor.
 * This design choice keeps the entity lightweight and avoids unnecessary
 * joins for simple feedback operations.
 * </p>
 *
 * <p>
 * A composite unique constraint on {@code tutoringId} and
 * {@code tutorObservation} ensures that:
 * <ul>
 *   <li>A student can evaluate a tutoring session only once</li>
 *   <li>A tutor can evaluate a student only once per tutoring</li>
 * </ul>
 * </p>
 *
 * <p>
 * The {@code tutorObservation} flag differentiates between student and tutor feedback:
 * <ul>
 *   <li>{@code false} - feedback from student</li>
 *   <li>{@code true} - feedback from tutor</li>
 * </ul>
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Entity(name = "SessionFeedback")
@Table(
        name = "SessionFeedback",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"tutoringId", "isTutorObservation"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class SessionFeedback {

    /**
     * Unique identifier of the feedback record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Identifier of the tutoring session being evaluated.
     */
    @Column(nullable = false)
    private int tutoringId;

    /**
     * Identifier of the student who submitted the feedback.
     */
    @Column(nullable = false)
    private int studentId;

    /**
     * Identifier of the tutor who conducted the tutoring session.
     */
    @Column(nullable = false)
    private int tutorId;

    /**
     * Numeric rating given by the student or tutor.
     * Typically ranges from 1 (lowest) to 5 (highest).
     */
    @Column(nullable = false)
    private int rating;

    /**
     * Textual comment or review provided by the student or tutor.
     */
    @Column(nullable = false)
    private String comment;

    /**
     * Date and time when the feedback was created.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Indicates whether the feedback was written by the tutor.
     * <ul>
     *   <li>{@code false} = feedback from student</li>
     *   <li>{@code true} = feedback from tutor</li>
     * </ul>
     */
    @Column(nullable = false)
    private boolean isTutorObservation = false;
}
