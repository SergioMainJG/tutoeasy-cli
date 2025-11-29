package cli.tutoeasy.service;

import cli.tutoeasy.config.argon2.Argon2Util;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.dto.*;
import cli.tutoeasy.repository.UserRepository;
import cli.tutoeasy.config.session.AuthSession;

/**
 * Service class for handling authentication-related operations.
 * This class provides methods for user login and retrieving user information.
 * It uses a {@link UserRepository} to interact with the database and {@link AuthSession} to manage user sessions.
 *
 * @see UserRepository
 * @see AuthSession
 */
public class AuthService {

    /**
     * The repository for managing user data.
     */
    private final UserRepository userRepository;

    /**
     * Constructs a new instance of the {@code AuthService}.
     *
     * @param userRepository The repository for managing user data.
     */
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user with the given credentials.
     *
     * @param req The login request data transfer object containing the user's email and password.
     * @return A {@link LoginResponseDto} containing the login result.
     */
    public LoginResponseDto login(LoginRequestDto req) {

        User user = userRepository.findByEmail(req.email());
        if (user == null)
            return new LoginResponseDto(-1, "", "", "Invalid credentials");

        if (!Argon2Util.verifyPassword(req.password(), user.getPasswordHash()))
            return new LoginResponseDto(-1, "", "", "Invalid credentials");

        AuthSession.login( user );
        return new LoginResponseDto(
                user.getId(),
                user.getUsername(),
                user.getRol().name(),
                "Login successful"
        );
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The {@link User} object corresponding to the given ID, or {@code null} if not found.
     */
    public User getUserById(int userId) {
        return userRepository.findById(userId);
    }
}
