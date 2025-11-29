package cli.tutoeasy.model.dto;

/**
 * Data transfer object for a login request.
 * This record holds the credentials required for a user to log in.
 *
 * @param email    The email of the user.
 * @param password The password of the user.
 */
public record LoginRequestDto(
        String email,
        String password
) {}
