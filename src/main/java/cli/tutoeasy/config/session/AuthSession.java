package cli.tutoeasy.config.session;

import cli.tutoeasy.model.dto.ActionResponseDto;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.repository.UserRepository;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Manages the authentication session of a user.
 * This class provides methods for initializing, logging in, logging out, and checking the status of a user's session.
 *
 * @see SessionData
 * @see SessionStorage
 * @see User
 */
public class AuthSession {

    /**
     * The currently logged-in user.
     */
    @Getter
    private static User currentUser;

    /**
     * Initializes the authentication session.
     * This method loads the session data from storage, validates the session, and sets the current user if the session is valid.
     *
     * @return An {@link ActionResponseDto} indicating the result of the initialization.
     */
    public static ActionResponseDto initialize() {

        SessionData data = SessionStorage.load();

        if (data == null)
            return new ActionResponseDto(false, "You must be log in.");

        LocalDateTime loginAt = LocalDateTime.parse(data.loginAt());

        Duration diff = Duration.between(loginAt, LocalDateTime.now());

        if (diff.toHours() >= 24) {
            SessionStorage.clear();
            return new ActionResponseDto(false, "Session expired. Please log in again.");
        }

        UserRepository repo = new UserRepository();
        User user = repo.findById(data.userId());

        if (user == null) {
            SessionStorage.clear();
            return new ActionResponseDto(false, "You must log in.");
        }

        currentUser = user;
        return new ActionResponseDto(true, "Welcome back, " + user.getUsername() + " (session restored)");
    }

    /**
     * Logs in a user.
     * This method sets the current user and saves the session data to storage.
     *
     * @param user The user to log in.
     */
    public static void login(User user) {
        currentUser = user;

        SessionData data = new SessionData(
                user.getId(),
                user.getUsername(),
                user.getRol().name(),
                LocalDateTime.now().toString()
        );

        SessionStorage.save(data);
    }

    /**
     * Logs out the current user.
     * This method clears the current user and the session data from storage.
     */
    public static void logout() {
        currentUser = null;
        SessionStorage.clear();
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return {@code true} if a user is logged in, {@code false} otherwise.
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Checks if the current user has the specified role.
     *
     * @param role The role to check for.
     * @return {@code true} if the current user has the specified role, {@code false} otherwise.
     */
    public static boolean hasRole(String role) {
        return currentUser != null &&
                currentUser.getRol().name().equalsIgnoreCase(role);
    }
}
