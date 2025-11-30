package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.Tutoring;
import cli.tutoeasy.model.entities.TutoringStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * <p>
 * This class is responsible for managing {@link Tutoring} entities in the database.
 * It extends {@link BaseRepository} to inherit common CRUD operations and provides
 * specialized queries for tutoring-related data.
 * </p>
 *
 * <p>
 * The repository handles operations such as finding pending tutoring sessions for a
 * specific tutor, checking for schedule conflicts, and updating the status of a
 * tutoring session.
 * </p>
 *
 * @see BaseRepository
 * @see Tutoring
 * @see TutoringStatus
 * @version 1.0
 * @since 1.0
 */
public class TutoringRepository extends BaseRepository<Tutoring> {

    /**
     * Constructs a new instance of {@code TutoringRepository}.
     * It initializes the repository with the {@link Tutoring} class.
     */
    public TutoringRepository() {
        super(Tutoring.class);
    }

    /**
     * Retrieves a list of pending tutoring sessions for a specific tutor. The list
     * is ordered by meeting date and time.
     *
     * @param tutorId The ID of the tutor whose pending sessions are to be retrieved.
     * @return A list of {@link Tutoring} objects representing pending sessions.
     */
    public List<Tutoring> findPendingByTutor(int tutorId) {
        return executeQuery(em ->
                em.createQuery("""
                SELECT t FROM Tutoring t
                LEFT JOIN FETCH t.student
                LEFT JOIN FETCH t.tutor
                LEFT JOIN FETCH t.subject
                LEFT JOIN FETCH t.topic
                WHERE t.tutor.id = :tutorId
                AND t.status = :status
                ORDER BY t.meetingDate ASC, t.meetingTime ASC
                """, Tutoring.class)
                        .setParameter("tutorId", tutorId)
                        .setParameter("status", TutoringStatus.unconfirmed)
                        .getResultList()
        );
    }

    /**
     * Checks if a tutor has a confirmed tutoring session at a specific date and time.
     * This is used to prevent schedule conflicts when accepting new requests.
     *
     * @param tutorId The ID of the tutor to check for conflicts.
     * @param date The date of the potential tutoring session.
     * @param time The time of the potential tutoring session.
     * @return {@code true} if a schedule conflict exists, {@code false} otherwise.
     */
    public boolean hasScheduleConflict(int tutorId, LocalDate date, LocalTime time) {
        return executeQuery(em -> {
            Long count = em.createQuery("""
                SELECT COUNT(t)
                FROM Tutoring t
                WHERE t.tutor.id = :tutorId
                AND t.meetingDate = :date
                AND t.meetingTime = :time
                AND t.status = :status
                """, Long.class)
                    .setParameter("tutorId", tutorId)
                    .setParameter("date", date)
                    .setParameter("time", time)
                    .setParameter("status", TutoringStatus.confirmed)
                    .getSingleResult();

            return count > 0;
        });
    }

    /**
     * Updates the status of a tutoring session. This is typically used to change the
     * status from "unconfirmed" to "confirmed" or "rejected".
     *
     * @param tutoringId The ID of the tutoring session to update.
     * @param status The new status to be set for the tutoring session.
     */
    public void updateStatus(int tutoringId, TutoringStatus status) {
        executeInTransaction(em -> {
            Tutoring t = em.find(Tutoring.class, tutoringId);
            if (t != null) {
                t.setStatus(status);
            }
        });
    }
}