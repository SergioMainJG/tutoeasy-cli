package cli.tutoeasy.model.dto;

public record CreateUserDto(
        String username,
        String email,
        String password,
        String role,
        Integer careerId,
        String description
) {}
