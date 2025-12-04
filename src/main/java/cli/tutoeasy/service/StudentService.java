package cli.tutoeasy.service;

import cli.tutoeasy.config.argon2.Argon2Util;
import cli.tutoeasy.model.dto.ActionResponseDto;
import cli.tutoeasy.model.dto.CreateStudentDto;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import cli.tutoeasy.repository.UserRepository;

/**
 * <p>
 * Service class for handling student-related operations.
 * This class provides methods for creating, retrieving, updating, and deleting students.
 * </p>
 *
 * <p>
 * It uses a {@link UserRepository} to interact with the database, ensuring that all
 * student-related data persistence is handled correctly.
 * </p>
 *
 * @version 1.0
 * @since 1.0
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
     * Creates a new student account.
     *
     * <p>
     * This method checks if a user with the provided email already exists.
     * If not, it creates a new user with the 'student' role, hashes the password,
     * and saves the user to the database.
     * </p>
     *
     * @param dto The data transfer object containing the new student's information (name, email, password).
     * @return An {@link ActionResponseDto} indicating the result of the operation (success or failure message).
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
     * Retrieves a student by their unique identifier.
     *
     * <p>
     * This method delegates to the {@link UserRepository} to find a user with the given ID.
     * </p>
     *
     * @param id The unique identifier of the student to retrieve.
     * @return The {@link User} object corresponding to the given ID, or {@code null} if no user is found.
     */
    public User getStudentById(int id) {
        return userRepository.findById(id);
    }

    /**
     * Updates the information of an existing student.
     *
     * <p>
     * This method delegates to the {@link UserRepository} to persist changes made to the student object.
     * </p>
     *
     * @param student The {@link User} object containing the updated information.
     */
    public void updateStudent(User student) {
        userRepository.update(student);
    }

    /**
     * Deletes a student account by their unique identifier.
     *
     * <p>
     * This method delegates to the {@link UserRepository} to remove the user with the given ID from the database.
     * </p>
     *
     * @param id The unique identifier of the student to delete.
     */
    public void deleteStudent(int id) {
        userRepository.delete(id);
    }
}