package cli.tutoeasy.model.dto;

/**
 * <p>
 * Data transfer object for updating user profile.
 * </p>
 * <p>
 * This record holds the information that can be updated by a user.
 * </p>
 *
 * @param username The new username (optional).
 * @param email The new email (optional).
 * @param currentPassword The current password for verification (required for password change).
 * @param newPassword The new password (optional).
 * @param careerName The career name (optional).
 * @param addDescription Additional description to append (optional).
 * @param newDescription New description to replace existing (optional).
 */
public record UpdateProfileDto(
        String username,
        String email,
        String currentPassword,
        String newPassword,
        String careerName,
        String addDescription,
        String newDescription
) {
    /**
     * <p>
     * Checks if there are any fields to update.
     * </p>
     *
     * @return {@code true} if at least one field is provided, {@code false} otherwise.
     */
    public boolean hasUpdates() {
        return username != null ||
                email != null ||
                newPassword != null ||
                careerName != null ||
                addDescription != null ||
                newDescription != null;
    }
}