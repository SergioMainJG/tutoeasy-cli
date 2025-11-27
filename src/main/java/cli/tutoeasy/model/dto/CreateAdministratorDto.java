package cli.tutoeasy.model.dto;

public record CreateAdministratorDto(
        String name,
        String lastName,
        String email,
        String password
) {}