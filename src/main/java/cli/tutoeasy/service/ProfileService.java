package cli.tutoeasy.service;

import cli.tutoeasy.config.argon2.Argon2Util;
import cli.tutoeasy.model.dto.ActionResponseDto;
import cli.tutoeasy.model.dto.UpdateProfileDto;
import cli.tutoeasy.model.entities.Career;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.repository.CareerRepository;
import cli.tutoeasy.repository.UserRepository;

/**
 * <p>
 * Service class for handling profile-related operations.
 * </p>
 * <p>
 * This class provides methods for updating user profile information.
 * </p>
 */
public class ProfileService {

    private final UserRepository userRepository;
    private final CareerRepository careerRepository;

    /**
     * Constructs a new instance of the {@code ProfileService}.
     *
     * @param userRepository The repository for managing user data.
     * @param careerRepository The repository for managing career data.
     */
    public ProfileService(UserRepository userRepository, CareerRepository careerRepository) {
        this.userRepository = userRepository;
        this.careerRepository = careerRepository;
    }

    /**
     * <p>
     * Updates user profile information.
     * </p>
     *
     * @param userId The ID of the user to update.
     * @param dto The data transfer object containing the updated information.
     * @return An {@link ActionResponseDto} indicating the result of the operation.
     */
    public ActionResponseDto updateProfile(int userId, UpdateProfileDto dto) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return new ActionResponseDto(false, "User not found.");
        }

        StringBuilder updateMessages = new StringBuilder();
        boolean hasChanges = false;

        if (dto.username() != null && !dto.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(dto.username())) {
                return new ActionResponseDto(false, "Username already taken: " + dto.username());
            }
            user.setUsername(dto.username());
            updateMessages.append("Username updated. ");
            hasChanges = true;
        }

        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.email())) {
                return new ActionResponseDto(false, "Email already registered: " + dto.email());
            }
            user.setEmail(dto.email());
            updateMessages.append("Email updated. ");
            hasChanges = true;
        }

        if (dto.newPassword() != null) {
            if (dto.currentPassword() == null) {
                return new ActionResponseDto(false, "Current password required for password change.");
            }

            if (!Argon2Util.verifyPassword(dto.currentPassword(), user.getPasswordHash())) {
                return new ActionResponseDto(false, "Current password is incorrect.");
            }

            user.setPasswordHash(Argon2Util.hashingPassword(dto.newPassword()));
            updateMessages.append("Password updated. ");
            hasChanges = true;
        }

        if (dto.careerName() != null) {
            Career career = careerRepository.findByName(dto.careerName());

            if (career == null) {
                career = new Career();
                career.setName(dto.careerName());
                careerRepository.save(career);
            }

            user.setCareer(career);
            updateMessages.append("Career updated. ");
            hasChanges = true;
        }

        if (dto.addDescription() != null) {
            String currentDesc = user.getDescription() != null ? user.getDescription() : "";
            String newDesc = currentDesc.isEmpty()
                    ? dto.addDescription()
                    : currentDesc + " " + dto.addDescription();
            user.setDescription(newDesc);
            updateMessages.append("Description appended. ");
            hasChanges = true;
        } else if (dto.newDescription() != null) {
            user.setDescription(dto.newDescription());
            updateMessages.append("Description updated. ");
            hasChanges = true;
        }

        if (!hasChanges) {
            return new ActionResponseDto(false, "No changes detected.");
        }

        userRepository.update(user);
        return new ActionResponseDto(true, "Profile updated successfully. " + updateMessages.toString());
    }

    /**
     * <p>
     * Retrieves a user by their ID.
     * </p>
     *
     * @param userId The ID of the user to retrieve.
     * @return The {@link User} object corresponding to the given ID, or {@code null} if not found.
     */
    public User getUserById(int userId) {
        return userRepository.findById(userId);
    }
}