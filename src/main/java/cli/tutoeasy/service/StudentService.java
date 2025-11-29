package cli.tutoeasy.service;

import cli.tutoeasy.config.argon2.Argon2Util;
import cli.tutoeasy.model.dto.ActionResponseDto;
import cli.tutoeasy.model.dto.CreateStudentDto;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import cli.tutoeasy.repository.UserRepository;

/**
 * Service class for handling student-related operations.
 * This class provides methods for creating, retrieving, updating, and deleting students.
 * It uses a {@link UserRepository} to interact with the database.
 *
 * @see UserRepository
 * @see CreateStudentDto
 * @see User
 */
public class StudentService {

    /**
     * The repository for managing user data.
     */
    private final UserRepository userRepository;

    /**
     * Constructs a new instance of the {@code StudentService}.
     *
     * @param userRepository The repository for managing user data.
     */
    public StudentService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new student.
     *
     * @param dto The data transfer object containing the student's information.
     * @return An {@link ActionResponseDto} indicating the result of the operation.
     */
    public ActionResponseDto createStudent(CreateStudentDto dto) {

        User existing = userRepository.findByEmail(dto.email());
        if (existing != null) {
            return new ActionResponseDto(false, "There is already a registered student with this email.");
        }

        User student = new User();
        student.setUsername(dto.name());
        student.setEmail(dto.email());
        student.setPasswordHash(Argon2Util.hashingPassword(dto.password()));
        student.setRol(UserRole.student);

        userRepository.save(student);

        return new ActionResponseDto(true, "Student successfully registered.");
    }

    /**
     * Retrieves a student by their ID.
     *
     * @param id The ID of the student to retrieve.
     * @return The {@link User} object corresponding to the given ID, or {@code null} if not found.
     */
    public User getStudentById(int id) {
        return userRepository.findById(id);
    }

    /**
     * Updates the information of a student.
     *
     * @param student The student object with the updated information.
     */
    public void updateStudent(User student) {
        userRepository.update(student);
    }

    /**
     * Deletes a student by their ID.
     *
     * @param id The ID of the student to delete.
     */
    public void deleteStudent(int id) {
        userRepository.delete(id);
    }
}