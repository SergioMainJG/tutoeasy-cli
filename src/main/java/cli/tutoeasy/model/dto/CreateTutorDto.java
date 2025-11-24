package cli.tutoeasy.model.dto;

public record CreateTutorDto(
        String name,
        String email,
        String password
){ }