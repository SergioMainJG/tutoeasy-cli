package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.Career;
import jakarta.persistence.NoResultException;

/**
 * <p>
 * Repository for managing Career entities.
 * </p>
 * <p>
 * Provides methods to retrieve and manage career data.
 * </p>
 */
public class CareerRepository extends BaseRepository<Career> {

    /**
     * <p>
     * Constructs a new instance of {@code CareerRepository}.
     * </p>
     */
    public CareerRepository() {
        super(Career.class);
    }

    /**
     * <p>
     * Finds a career by name (case-insensitive).
     * </p>
     *
     * @param name Name of the career
     * @return {@link Career} if found, {@code null} otherwise
     */
    public Career findByName(String name) {
        return executeQuery(em -> {
            try {
                return em.createQuery(
                                "SELECT c FROM Career c WHERE LOWER(c.name) = LOWER(:name)",
                                Career.class)
                        .setParameter("name", name)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        });
    }

    /**
     * <p>
     * Checks if a career with the given name exists.
     * </p>
     *
     * @param name Name of the career to check
     * @return {@code true} if career exists, {@code false} otherwise
     */
    public boolean existsByName(String name) {
        return executeQuery(em -> {
            Long count = em.createQuery(
                            "SELECT COUNT(c) FROM Career c WHERE LOWER(c.name) = LOWER(:name)",
                            Long.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return count > 0;
        });
    }
}