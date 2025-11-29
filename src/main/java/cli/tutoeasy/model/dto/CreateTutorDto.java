package cli.tutoeasy.model.dto;

/**
 * Data transfer object for creating a new tutor.
 * This record holds the information required to create a new tutor.
 *
 * @param name     The name of the tutor.
 * @param email    The email of the tutor.
 * @param password The password of the tutor.
 */
public record CreateTutorDto(
        String name,
        String email,
        String password
){ }