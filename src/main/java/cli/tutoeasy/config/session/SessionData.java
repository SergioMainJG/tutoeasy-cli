package cli.tutoeasy.config.session;

/**
 * Represents the data stored in a user session.
 * This record holds information about the logged-in user, including their ID, username, role, and login time.
 *
 * @param userId   The ID of the user.
 * @param username The username of the user.
 * @param role     The role of the user.
 * @param loginAt  The timestamp of when the user logged in.
 */
public record SessionData(
        int userId,
        String username,
        String role,
        String loginAt
) {}
