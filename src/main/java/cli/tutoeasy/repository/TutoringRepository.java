package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.Tutoring;
import cli.tutoeasy.model.entities.TutoringStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TutoringRepository extends BaseRepository<Tutoring> {

    public TutoringRepository() {
        super(Tutoring.class);
    }

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

    public void updateStatus(int tutoringId, TutoringStatus status) {
        executeInTransaction(em -> {
            Tutoring t = em.find(Tutoring.class, tutoringId);
            if (t != null) {
                t.setStatus(status);
            }
        });
    }
}