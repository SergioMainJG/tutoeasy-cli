package cli.tutoeasy.service;

import cli.tutoeasy.config.argon2.Argon2Util;
import cli.tutoeasy.model.dto.CreateUserDto;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import cli.tutoeasy.repository.UserRepository;

/**
 * Service class for handling user-related operations.
 * This class provides methods for creating users.
 * It uses a {@link UserRepository} to interact with the database.
 *
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
     * Creates a new user.
     *
     * @param dto The data transfer object containing the user's information.
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
