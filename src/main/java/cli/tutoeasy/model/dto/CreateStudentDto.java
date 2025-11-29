package cli.tutoeasy.model.dto;

/**
 * Data transfer object for creating a new student.
 * This record holds the information required to create a new student.
 *
 * @param name     The name of the student.
 * @param email    The email of the student.
 * @param password The password of the student.
 */
public record CreateStudentDto(
        String name,
        String email,
        String password
){ }