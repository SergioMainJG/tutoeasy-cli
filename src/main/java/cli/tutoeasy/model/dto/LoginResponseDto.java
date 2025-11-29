package cli.tutoeasy.model.dto;

/**
 * Data transfer object for a login response.
 * This record holds the information returned after a user logs in.
 *
 * @param userId   The ID of the user.
 * @param username The username of the user.
 * @param role     The role of the user.
 * @param message  A message providing details about the login result.
 */
public record LoginResponseDto(
        int userId,
        String username,
        String role,
        String message
) {}
