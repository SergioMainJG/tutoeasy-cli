package cli.tutoeasy.service;

import cli.tutoeasy.config.argon2.Argon2Util;
import cli.tutoeasy.model.dto.ActionResponseDto;
import cli.tutoeasy.model.dto.CreateAdministratorDto;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import cli.tutoeasy.repository.UserRepository;

import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.entities.Tutoring;
import cli.tutoeasy.model.entities.TutoringStatus;
import cli.tutoeasy.repository.TutoringRepository;

import java.util.List;

/**
 * Service class for handling administrator-related operations.
 * This class provides methods for creating, retrieving, updating, and deleting administrators.
 * It uses a {@link UserRepository} to interact with the database.
 *
 * @see UserRepository
 * @see CreateAdministratorDto
 * @see User
 */
public class AdministratorService {
    /**
     * The repository for managing user data.
     */
    private final UserRepository repo;

    private final TutoringRepository tutoringRepository;

    /**
     * Constructs a new instance of the {@code AdministratorService}.
     *
     * @param repo The repository for managing user data.
     * @param tutoringRepository the repository for managing tutorings
     */
    public AdministratorService(UserRepository repo, TutoringRepository tutoringRepository) {
        this.repo = repo;
        this.tutoringRepository = tutoringRepository;
    }

    /**
     * Creates a new administrator.
     *
     * @param dto The data transfer object containing the administrator's information.
     * @return An {@link ActionResponseDto} indicating the result of the operation.
     */
    public ActionResponseDto createAdministrator(CreateAdministratorDto dto) {

        User existing = repo.findByEmail(dto.email());
        if (existing != null) {
            return new ActionResponseDto(false, "There is already a registered administrator with that email address.");
        }

        User admin = new User();
        admin.setUsername(dto.name() + " " + dto.lastName());
        admin.setEmail(dto.email());
        admin.setPasswordHash(Argon2Util.hashingPassword(dto.password()));
        admin.setRol(UserRole.admin);

        repo.save(admin);

        return new ActionResponseDto(true, "Administrator successfully registered.");
    }

    /**
     * Retrieves an administrator by their ID.
     *
     * @param id The ID of the administrator to retrieve.
     * @return The {@link User} object corresponding to the given ID, or {@code null} if not found.
     */
    public User getAdminById(int id) {
        return repo.findById(id);
    }

    /**
     * Updates the information of an administrator.
     *
     * @param admin The administrator object with the updated information.
     */
    public void updateAdmin(User admin) {
        repo.update(admin);
    }

    /**
     * Deletes an administrator by their ID.
     *
     * @param id The ID of the administrator to delete.
     */
    public void deleteAdmin(int id) {
        repo.delete(id);
    }

    /**
     * Retrieves a list of tutorings filtered by student name, tutor name, subject, and status.
     * <p>Requires administrator role; throws {@link IllegalStateException} if the current user is not an admin.</p>
     *
     * @param studentName optional student username filter (can be null or empty)
     * @param tutorName optional tutor username filter (can be null or empty)
     * @param subjectName optional subject name filter (can be null or empty)
     * @param status optional tutoring status filter (can be null)
     * @return a list of {@link Tutoring} objects matching the given filters
     */
    public List<Tutoring> getFilteredTutoringsByName(
            String studentName,
            String tutorName,
            String subjectName,
            TutoringStatus status
    ) {
        if (!AuthSession.hasRole(UserRole.admin.name())) {
            throw new IllegalStateException("Access denied. Administrator role required.");
        }
        return tutoringRepository.findAllFiltered(studentName, tutorName, subjectName, status);
    }

}