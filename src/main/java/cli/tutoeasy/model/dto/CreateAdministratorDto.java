package cli.tutoeasy.model.dto;

/**
 * Data transfer object for creating a new administrator.
 * This record holds the information required to create a new administrator.
 *
 * @param name     The name of the administrator.
 * @param lastName The last name of the administrator.
 * @param email    The email of the administrator.
 * @param password The password of the administrator.
 */
public record CreateAdministratorDto(
        String name,
        String lastName,
        String email,
        String password
) {}