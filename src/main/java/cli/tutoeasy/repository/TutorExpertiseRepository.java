package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.TutorExpertise;
import cli.tutoeasy.model.entities.Subject;
import cli.tutoeasy.model.entities.User;

import java.util.List;

/**
 * <p>
 * Repository class for managing {@link TutorExpertise} entities in the database.
 * It extends {@link BaseRepository} to inherit common CRUD operations and provides
 * specialized queries related to a tutor's subjects (expertise).
 * </p>
 *
 * <p>
 * This repository allows finding all subjects of a tutor, deleting all subjects of
 * a tutor, and adding a new subject to a tutor.
 * </p>
 *
 * @see BaseRepository
 * @see TutorExpertise
 * @see Subject
 * @see User
 * @version 1.0
 * @since 1.0
 */
public class TutorExpertiseRepository extends BaseRepository<TutorExpertise> {

    /**
     * Constructs a new instance of {@code TutorExpertiseRepository}.
     * It initializes the repository with the {@link TutorExpertise} class.
     */
    public TutorExpertiseRepository() {
        super(TutorExpertise.class);
    }

    /**
     * Retrieves a list of {@link TutorExpertise} for a specific tutor.
     *
     * @param tutorId The ID of the tutor whose subjects are to be retrieved.
     * @return A list of {@link TutorExpertise} associated with the given tutor.
     */
    public List<TutorExpertise> findByTutor(int tutorId) {
        return executeQuery(em ->
                em.createQuery("SELECT te FROM TutorExpertise te WHERE te.tutor.id = :id", TutorExpertise.class)
                        .setParameter("id", tutorId)
                        .getResultList()
        );
    }

    /**
     * Deletes all {@link TutorExpertise} records associated with a specific tutor.
     * This is typically used when updating a tutor's subjects completely.
     *
     * @param tutorId The ID of the tutor whose subjects are to be deleted.
     */
    public void deleteByTutor(int tutorId) {
        executeQuery(em -> {
            var tx = em.getTransaction();
            try {
                tx.begin();
                em.createQuery("DELETE FROM TutorExpertise te WHERE te.tutor.id = :id")
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
     * Adds a subject to a tutor by creating a new {@link TutorExpertise} entity.
     *
     * @param tutorId   The ID of the tutor.
     * @param subjectId The ID of the subject to add.
     * @return The created {@link TutorExpertise} entity linking the tutor and subject.
     * @throws RuntimeException if the transaction fails or any entity is not found.
     */
    public TutorExpertise addSubjectToTutor(int tutorId, int subjectId) {
        return executeQuery(em -> {
            var tx = em.getTransaction();
            try {
                tx.begin();
                var te = new TutorExpertise();
                te.setTutor(em.find(User.class, tutorId));
                te.setSubject(em.find(Subject.class, subjectId));
                em.persist(te);
                tx.commit();
                return te;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        });
    }
}
