package cli.tutoeasy.service;

import cli.tutoeasy.config.argon2.Argon2Util;
import cli.tutoeasy.model.dto.CreateUserDto;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import cli.tutoeasy.repository.UserRepository;

/**
 * <p>
 * Service class for handling general user-related operations.
 * This class provides methods for creating generic users.
 * </p>
 *
 * <p>
 * It uses a {@link UserRepository} to interact with the database.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see UserRepository
 * @see CreateUserDto
 * @see User
 */
public class UserService {

    /**
     * The repository for managing user data.
     */
    private final UserRepository repo;

    /**
     * Constructs a new instance of the {@code UserService}.
     *
     * @param repo The repository for managing user data.
     */
    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    /**
     * Creates a new user account with a specified role.
     *
     * <p>
     * This method creates a new user entity, hashes the password using Argon2,
     * assigns the specified role, and saves the user to the database.
     * </p>
     *
     * @param dto The data transfer object containing the new user's information (username, email, password, role).
     */
    public void createUser(CreateUserDto dto) {

        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPasswordHash(Argon2Util.hashingPassword(dto.password()));
        user.setRol(UserRole.valueOf(dto.role().toUpperCase()));

        repo.save(user);
    }
}
