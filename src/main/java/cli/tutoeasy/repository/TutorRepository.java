package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;

/**
 * <p>
 * This class is responsible for managing {@link User} entities that have the
 * role of "tutor" in the database. It extends {@link BaseRepository} to
 * inherit common CRUD operations and provides specialized methods for handling
 * tutor-specific data.
 * </p>
 *
 * <p>
 * The main purpose of this repository is to abstract the data access logic for
 * tutors, ensuring that operations are performed on users with the correct role.
 * </p>
 *
 * @see BaseRepository
 * @see User
 * @see UserRole
 * @version 1.0
 * @since 1.0
 */
public class TutorRepository extends BaseRepository<User> {

    /**
     * Constructs a new instance of {@code TutorRepository}.
     * It initializes the repository with the {@link User} class, as tutors are
     * a specialized type of user.
     */
    public TutorRepository() {
        super(User.class);
    }

    /**
     * Finds a user by their ID and verifies that they have the role of "tutor".
     * If the user is found and has the correct role, the {@link User} object is
     * returned. Otherwise, it returns {@code null}.
     *
     * @param id The ID of the user to find.
     * @return The {@link User} object if they are a tutor, or {@code null} if not found
     *         or if the user is not a tutor.
     */
    public User findTutorById(int id) {
        User user = findById(id);
        if (user != null && user.getRol() == UserRole.tutor) {
            return user;
        }
        return null;
    }
}