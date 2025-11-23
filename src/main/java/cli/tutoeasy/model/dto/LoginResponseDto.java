package cli.tutoeasy.model.dto;

public record LoginResponseDto(
        int userId,
        String username,
        String role,
        String message
) {}
