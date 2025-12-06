package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.TutorSchedule;
import java.util.List;

/**
 * <p>
 * Repository class for managing {@link TutorSchedule} entities in the database.
 * Extends {@link BaseRepository} to inherit common CRUD operations and provides
 * specialized queries related to tutors' schedules.
 * </p>
 *
 * <p>
 * This repository handles operations such as retrieving all schedules for a specific tutor
 * and deleting schedules when updating or removing a tutor's availability.
 * </p>
 *
 * @see BaseRepository
 * @see TutorSchedule
 * @version 1.0
 * @since 1.0
 */
public class TutorScheduleRepository extends BaseRepository<TutorSchedule> {

    /**
     * Constructs a new instance of {@code TutorScheduleRepository}.
     * Initializes the repository with the {@link TutorSchedule} class.
     */
    public TutorScheduleRepository() {
        super(TutorSchedule.class);
    }

    /**
     * Deletes all schedules associated with a given tutor.
     *
     * @param tutorId The ID of the tutor whose schedules should be removed.
     */
    public void deleteByTutor(int tutorId) {
        executeQuery(em -> {
            var tx = em.getTransaction();
            try {
                tx.begin();
                em.createQuery("DELETE FROM TutorSchedule ts WHERE ts.tutor.id = :id")
                        .setParameter("id", tutorId)
                        .executeUpdate();
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
            return null;
        });
    }

    /**
     * Retrieves all schedules for a specific tutor.
     *
     * @param tutorId The ID of the tutor whose schedules are being fetched.
     * @return A list of {@link TutorSchedule} objects for the specified tutor.
     */
    public List<TutorSchedule> findByTutor(int tutorId) {
        return executeQuery(em ->
                em.createQuery("SELECT ts FROM TutorSchedule ts WHERE ts.tutor.id = :id", TutorSchedule.class)
                        .setParameter("id", tutorId)
                        .getResultList()
        );
    }

    /**
     * Retrieves all schedules for a specific tutor.
     * <p>
     * This method is similar to {@link #findByTutor(int)}, provided for convenience.
     * </p>
     *
     * @param tutorId The ID of the tutor whose schedules are being fetched.
     * @return A list of {@link TutorSchedule} objects for the specified tutor.
     */
    public List<TutorSchedule> findAllByTutor(int tutorId) {
        return executeQuery(em ->
                em.createQuery("SELECT ts FROM TutorSchedule ts WHERE ts.tutor.id = :id", TutorSchedule.class)
                        .setParameter("id", tutorId)
                        .getResultList()
        );
    }
}
