package cli.tutoeasy.model.dto;

public record CreateAdministradorDto(
        String name,
        String lastName,
        String email,
        String password
) {}