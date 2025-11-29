package cli.tutoeasy.model.dto;

/**
 * Data transfer object for creating a new user.
 * This record holds the information required to create a new user.
 *
 * @param username    The username of the user.
 * @param email       The email of the user.
 * @param password    The password of the user.
 * @param role        The role of the user.
 * @param careerId    The ID of the career of the user.
 * @param description A description of the user.
 */
public record CreateUserDto(
        String username,
        String email,
        String password,
        String role,
        Integer careerId,
        String description
) {}
