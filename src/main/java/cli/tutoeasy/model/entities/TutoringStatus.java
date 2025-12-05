package cli.tutoeasy.model.entities;

/**
 * Represents the status of a tutoring session.
 */
public enum TutoringStatus {
    /**
     * The tutoring session has been confirmed by the tutor.
     */
    confirmed,
    /**
     * The tutoring session has been requested by a student but not yet confirmed by the tutor.
     */
    unconfirmed,
    /**
     * The tutoring session has been canceled by either the student or the tutor.
     */
    canceled,
    /**
     * The tutoring session has been completed.
     */
    completed
}
