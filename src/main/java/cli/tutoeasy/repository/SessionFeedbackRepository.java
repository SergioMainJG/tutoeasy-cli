package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.SessionFeedback;

/**
 * Repository responsible for managing {@link SessionFeedback} entities.
 *
 * <p>
 * This repository extends {@link BaseRepository} to inherit common CRUD
 * operations and provides additional query methods related to feedback
 * left for tutoring sessions.
 * </p>
 *
 * <p>
 * It encapsulates database access logic for {@code SessionFeedback},
 * keeping persistence concerns separate from business logic.
 * </p>
 *
 * <p>
 * Provides specific methods to check for existing feedback to prevent
 * duplicate evaluations by students or tutors.
 * </p>
 *
 * @see BaseRepository
 * @see SessionFeedback
 * @version 1.0
 * @since 1.0
 */
public class SessionFeedbackRepository extends BaseRepository<SessionFeedback> {

    /**
     * Constructs a new {@code SessionFeedbackRepository}.
     *
     * <p>
     * Initializes the repository for {@link SessionFeedback} entity operations.
     * </p>
     */
    public SessionFeedbackRepository() {
        super(SessionFeedback.class);
    }

    /**
     * Determines whether feedback from a student already exists for a given tutoring session.
     *
     * <p>
     * This method is primarily used to prevent a student from submitting
     * multiple evaluations for the same session.
     * </p>
     *
     * @param tutoringId the identifier of the tutoring session
     * @return {@code true} if feedback exists from a student, {@code false} otherwise
     */
    public boolean existsByTutoringId(int tutoringId) {
        return executeQuery(em -> {
            Long count = em.createQuery("""
                SELECT COUNT(f)
                FROM SessionFeedback f
                WHERE f.tutoringId = :tutoringId
                AND f.tutorObservation = false
                """, Long.class)
                    .setParameter("tutoringId", tutoringId)
                    .getSingleResult();
            return count > 0;
        });
    }

    /**
     * Determines whether feedback from a tutor already exists for a given tutoring session.
     *
     * <p>
     * This method prevents a tutor from submitting multiple evaluations
     * for the same student in a single session.
     * </p>
     *
     * @param tutoringId the identifier of the tutoring session
     * @return {@code true} if feedback exists from the tutor, {@code false} otherwise
     */
    public boolean existsTutorFeedback(int tutoringId) {
        return executeQuery(em -> {
            Long count = em.createQuery("""
                SELECT COUNT(f)
                FROM SessionFeedback f
                WHERE f.tutoringId = :tutoringId
                AND f.tutorObservation = true
                """, Long.class)
                    .setParameter("tutoringId", tutoringId)
                    .getSingleResult();
            return count > 0;
        });
    }
}
