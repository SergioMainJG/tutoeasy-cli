package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.User;
import jakarta.persistence.NoResultException;

/**
 * Repository for managing contact-related queries.
 * Provides methods to retrieve user information for messaging purposes.
 */
public class ContactRepository extends BaseRepository<User> {

    public ContactRepository() {
        super(User.class);
    }

    /**
     * Finds a user by their username
     * 
     * @param username The username to search for
     * @return The User if found, null otherwise
     */
    public User findByUsername(String username) {
        return executeQuery(em -> {
            try {
                return em.createQuery(
                    "SELECT u FROM User u " +
                    "LEFT JOIN FETCH u.career " +
                    "WHERE u.username = :username",
                    User.class
                ).setParameter("username", username)
                 .setMaxResults(1)
                 .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        });
    }

    /**
     * Gets user information with career loaded
     * 
     * @param userId The ID of the user
     * @return The User with career information
     */
    public User findUserWithCareer(int userId) {
        return executeQuery(em -> {
            try {
                return em.createQuery(
                    "SELECT u FROM User u " +
                    "LEFT JOIN FETCH u.career " +
                    "WHERE u.id = :userId",
                    User.class
                ).setParameter("userId", userId)
                 .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        });
    }
}