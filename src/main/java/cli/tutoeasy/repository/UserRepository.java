package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * Repository class for managing {@link User} entities.
 * This class provides methods for querying user data from the database.
 *
 * @see BaseRepository
 * @see User
 */
public class UserRepository extends BaseRepository<User> {

    /**
     * Constructs a new instance of the {@code UserRepository}.
     */
    public UserRepository() {
        super(User.class);
    }

    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user to find.
     * @return The {@link User} object corresponding to the given email, or {@code null} if not found.
     */
    public User findByEmail(String email) {
        return executeQuery(em -> {
            try {
                TypedQuery<User> query = em.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email",
                        User.class
                );

                query.setParameter("email", email);
                query.setMaxResults(1);
                query.setHint("org.hibernate.readOnly", true);
                query.setHint("org.hibernate.fetchSize", 1);

                return query.getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        });
    }

    /**
     * Finds all users with the role of tutor.
     *
     * @return A list of all users who are tutors.
     */
    public List<User> findAllTutors() {
        return executeQuery(em -> {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.rol = :rol",
                    User.class
            );
            query.setParameter("rol", UserRole.tutor);
            query.setHint("org.hibernate.readOnly", true);
            return query.getResultList();
        });
    }

    /**
     * Finds all users with the role of admin.
     *
     * @return A list of all users who are admins.
     */
    public List<User> findAllAdmins() {
        return executeQuery(em -> {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.rol = :rol",
                    User.class
            );
            query.setParameter("rol", UserRole.admin);
            query.setHint("org.hibernate.readOnly", true);
            return query.getResultList();
        });
    }

    /**
     * Checks if a user with the given email address exists.
     *
     * @param email The email address to check.
     * @return {@code true} if a user with the given email exists, {@code false} otherwise.
     */
    public boolean existsByEmail(String email) {
        return executeQuery(em -> {
            Long count = em.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.email = :email",
                            Long.class
                    ).setParameter("email", email)
                    .setHint("org.hibernate.readOnly", true)
                    .getSingleResult();
            return count > 0;
        });
    }

    /**
     * Checks if a user with the given username exists.
     *
     * @param username The username to check.
     * @return {@code true} if a user with the given username exists, {@code false} otherwise.
     */
    public boolean existsByUsername(String username) {
        return executeQuery(em -> {
            Long count = em.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                            Long.class
                    ).setParameter("username", username)
                    .setHint("org.hibernate.readOnly", true)
                    .getSingleResult();
            return count > 0;
        });
    }
}