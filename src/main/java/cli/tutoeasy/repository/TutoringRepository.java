package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.Topic;
import cli.tutoeasy.model.entities.Tutoring;
import cli.tutoeasy.model.entities.TutoringStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * <p>
 * This class is responsible for managing {@link Tutoring} entities in the
 * database.
 * It extends {@link BaseRepository} to inherit common CRUD operations and
 * provides
 * specialized queries for tutoring-related data.
 * </p>
 *
 * <p>
 * The repository handles operations such as finding pending tutoring sessions
 * for a
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
     * @param tutorId The ID of the tutor whose pending sessions are to be
     *                retrieved.
     * @return A list of {@link Tutoring} objects representing pending sessions.
     */
    public List<Tutoring> findPendingByTutor(int tutorId) {
        return executeQuery(em -> em.createQuery("""
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
                .getResultList());
    }

    /**
     * Checks if a tutor has a confirmed tutoring session at a specific date and
     * time.
     * This is used to prevent schedule conflicts when accepting new requests.
     *
     * @param tutorId The ID of the tutor to check for conflicts.
     * @param date    The date of the potential tutoring session.
     * @param time    The time of the potential tutoring session.
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
     * Updates the status of a tutoring session. This is typically used to change
     * the
     * status from "unconfirmed" to "confirmed" or "rejected".
     *
     * @param tutoringId The ID of the tutoring session to update.
     * @param status     The new status to be set for the tutoring session.
     */
    public void updateStatus(int tutoringId, TutoringStatus status) {
        executeInTransaction(em -> {
            Tutoring t = em.find(Tutoring.class, tutoringId);
            if (t != null) {
                t.setStatus(status);
            }
        });
    }

    /**
     * Finds upcoming tutoring sessions for a student (after current date).
     *
     * @param studentId The ID of the student.
     * @return A list of upcoming tutoring sessions.
     */
    public List<Tutoring> findUpcomingByStudent(int studentId) {
        LocalDate today = LocalDate.now();
        return executeQuery(em -> em.createQuery("""
                SELECT t FROM Tutoring t
                LEFT JOIN FETCH t.student
                LEFT JOIN FETCH t.tutor
                LEFT JOIN FETCH t.subject
                LEFT JOIN FETCH t.topic
                WHERE t.student.id = :studentId
                AND t.meetingDate >= :today
                AND t.status != :canceledStatus
                ORDER BY t.meetingDate ASC, t.meetingTime ASC
                """, Tutoring.class)
                .setParameter("studentId", studentId)
                .setParameter("today", today)
                .setParameter("canceledStatus", TutoringStatus.canceled)
                .getResultList());
    }

    /**
     * Finds a tutoring by ID with all relationships loaded.
     *
     * @param tutoringId The ID of the tutoring session.
     * @return The tutoring session with all details loaded.
     */
    public Tutoring findByIdWithDetails(int tutoringId) {
        return executeQuery(em -> {
            var results = em.createQuery("""
                SELECT t FROM Tutoring t
                LEFT JOIN FETCH t.student
                LEFT JOIN FETCH t.tutor
                LEFT JOIN FETCH t.subject
                LEFT JOIN FETCH t.topic
                WHERE t.id = :tutoringId
                """, Tutoring.class)
                    .setParameter("tutoringId", tutoringId)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        });
    }

    /**
     * Checks if a tutor has a schedule conflict excluding a specific tutoring.
     *
     * @param tutorId           The ID of the tutor.
     * @param date              The date of the session.
     * @param time              The time of the session.
     * @param excludeTutoringId The ID of the tutoring to exclude from the check.
     * @return {@code true} if a conflict exists, {@code false} otherwise.
     */
    public boolean hasScheduleConflictExcluding(int tutorId, LocalDate date, LocalTime time, int excludeTutoringId) {
        return executeQuery(em -> {
            Long count = em.createQuery("""
                    SELECT COUNT(t)
                    FROM Tutoring t
                    WHERE t.tutor.id = :tutorId
                    AND t.meetingDate = :date
                    AND t.meetingTime = :time
                    AND t.status = :status
                    AND t.id != :excludeId
                    """, Long.class)
                    .setParameter("tutorId", tutorId)
                    .setParameter("date", date)
                    .setParameter("time", time)
                    .setParameter("status", TutoringStatus.confirmed)
                    .setParameter("excludeId", excludeTutoringId)
                    .getSingleResult();
            return count > 0;
        });
    }

    /**
     * Updates tutoring details (for student modifications).
     *
     * @param tutoringId The ID of the tutoring session to update.
     * @param newDate    The new date for the session (can be null).
     * @param newTime    The new time for the session (can be null).
     * @param topic      The new topic for the session (can be null).
     */
    public void updateTutoringDetails(int tutoringId, LocalDate newDate, LocalTime newTime, Topic topic) {
        executeInTransaction(em -> {
            Tutoring t = em.find(Tutoring.class, tutoringId);
            if (t != null) {
                if (newDate != null) {
                    t.setMeetingDate(newDate);
                }
                if (newTime != null) {
                    t.setMeetingTime(newTime);
                }
                if (topic != null) {
                    t.setTopic(topic);
                }
            }
        });
    }

    /**
     * Finds tutoring history for a student (past sessions that are completed or canceled).
     * Results are ordered by meeting date descending (most recent first).
     *
     * @param studentId ID of the student
     * @param limit Optional limit on number of results (null = no limit)
     * @param statusFilter Optional status filter ("completed" or "canceled", null = both)
     * @param subjectFilter Optional subject name filter (null = all subjects)
     * @return List of past tutoring sessions
     */
    public List<Tutoring> findHistoryByStudent(int studentId, Integer limit, String statusFilter, String subjectFilter) {
        LocalDate today = LocalDate.now();

        return executeQuery(em -> {
            StringBuilder jpql = new StringBuilder("""
                SELECT t FROM Tutoring t
                LEFT JOIN FETCH t.student
                LEFT JOIN FETCH t.tutor
                LEFT JOIN FETCH t.subject
                LEFT JOIN FETCH t.topic
                WHERE t.student.id = :studentId
                AND t.meetingDate < :today
                AND (t.status = :completedStatus OR t.status = :canceledStatus)
                """);

            if (statusFilter != null) {
                if (statusFilter.equalsIgnoreCase("completed")) {
                    jpql.append("AND t.status = :completedStatus ");
                } else if (statusFilter.equalsIgnoreCase("canceled")) {
                    jpql.append("AND t.status = :canceledStatus ");
                }
            }

            if (subjectFilter != null && !subjectFilter.trim().isEmpty()) {
                jpql.append("AND LOWER(t.subject.name) = LOWER(:subjectName) ");
            }

            jpql.append("ORDER BY t.meetingDate DESC, t.meetingTime DESC");

            var query = em.createQuery(jpql.toString(), Tutoring.class)
                    .setParameter("studentId", studentId)
                    .setParameter("today", today)
                    .setParameter("completedStatus", TutoringStatus.completed)
                    .setParameter("canceledStatus", TutoringStatus.canceled);

            if (subjectFilter != null && !subjectFilter.trim().isEmpty()) {
                query.setParameter("subjectName", subjectFilter.trim());
            }

            if (limit != null && limit > 0) {
                query.setMaxResults(limit);
            }

            return query.getResultList();
        });
    }

    /**
     * Finds upcoming confirmed tutoring sessions for a tutor (future sessions).
     * Only returns confirmed sessions.
     *
     * @param tutorId ID of the tutor
     * @return List of upcoming confirmed tutoring sessions
     */
    public List<Tutoring> findUpcomingByTutor(int tutorId) {
        LocalDate today = LocalDate.now();
        return executeQuery(em -> em.createQuery("""
                SELECT t FROM Tutoring t
                LEFT JOIN FETCH t.student
                LEFT JOIN FETCH t.tutor
                LEFT JOIN FETCH t.subject
                LEFT JOIN FETCH t.topic
                WHERE t.tutor.id = :tutorId
                AND t.meetingDate >= :today
                AND t.status = :confirmedStatus
                ORDER BY t.meetingDate ASC, t.meetingTime ASC
                """, Tutoring.class)
                .setParameter("tutorId", tutorId)
                .setParameter("today", today)
                .setParameter("confirmedStatus", TutoringStatus.confirmed)
                .getResultList());
    }
    /**
     * Retrieves a list of unique tutor IDs who have tutoring sessions scheduled
     * from today onwards.
     *
     * <p>This can be used, for example, to send notifications or reminders
     * only to tutors with upcoming sessions.</p>
     *
     * @return a {@link List} of {@link Integer} representing tutor IDs.
     */
    public List<Integer> getAllTutorIds() {
        LocalDate today = LocalDate.now();
        return executeQuery(em -> em.createQuery("""
        SELECT DISTINCT t.tutor.id FROM Tutoring t
        WHERE t.meetingDate >= :today
        """, Integer.class)
                .setParameter("today", today)
                .getResultList());
    }

    /**
     * Retrieves a list of tutorings filtered by student name, tutor name, subject, and status.
     * <p>Any filter can be null or empty to ignore that criteria.</p>
     *
     * @param studentName optional student username filter (null or empty to ignore)
     * @param tutorName optional tutor username filter (null or empty to ignore)
     * @param subjectName optional subject name filter (null or empty to ignore)
     * @param status optional tutoring status filter (null to ignore)
     * @return a list of {@link Tutoring} objects matching the given filters
     */
    public List<Tutoring> findAllFiltered(
            String studentName,
            String tutorName,
            String subjectName,
            TutoringStatus status
    ) {
        return executeQuery(em -> {
            StringBuilder jpql = new StringBuilder("""
            SELECT t FROM Tutoring t
            LEFT JOIN FETCH t.student
            LEFT JOIN FETCH t.tutor
            LEFT JOIN FETCH t.subject
            LEFT JOIN FETCH t.topic
            WHERE 1=1
        """);

            if (studentName != null && !studentName.isEmpty()) {
                jpql.append(" AND LOWER(t.student.username) LIKE LOWER(CONCAT('%', :studentName, '%'))");
            }
            if (tutorName != null && !tutorName.isEmpty()) {
                jpql.append(" AND LOWER(t.tutor.username) LIKE LOWER(CONCAT('%', :tutorName, '%'))");
            }
            if (subjectName != null && !subjectName.isEmpty()) {
                jpql.append(" AND LOWER(t.subject.name) LIKE LOWER(CONCAT('%', :subjectName, '%'))");
            }
            if (status != null) {
                jpql.append(" AND t.status = :status");
            }

            var query = em.createQuery(jpql.toString(), Tutoring.class);

            if (studentName != null && !studentName.isEmpty()) query.setParameter("studentName", studentName);
            if (tutorName != null && !tutorName.isEmpty()) query.setParameter("tutorName", tutorName);
            if (subjectName != null && !subjectName.isEmpty()) query.setParameter("subjectName", subjectName);
            if (status != null) query.setParameter("status", status);

            return query.getResultList();
        });
    }

}