package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.Subject;
import jakarta.persistence.NoResultException;

/**
 * Repository for managing Subject entities.
 */
public class SubjectRepository extends BaseRepository<Subject> {

  public SubjectRepository() {
    super(Subject.class);
  }

  /**
   * Finds a subject by name
   * 
   * @param name Name of the subject
   * @return Subject if found, null otherwise
   */
  public Subject findByName(String name) {
    return executeQuery(em -> {
      try {
        return em.createQuery(
            "SELECT s FROM Subject s WHERE LOWER(s.name) = LOWER(:name)",
            Subject.class).setParameter("name", name)
            .getSingleResult();
      } catch (NoResultException e) {
        return null;
      }
    });
  }
}