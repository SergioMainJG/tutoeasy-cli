package cli.tutoeasy.model.dto;

public record CreateStudentDto(
        String name,
        String email,
        String password
){ }