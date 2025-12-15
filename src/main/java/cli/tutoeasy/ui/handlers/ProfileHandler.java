package cli.tutoeasy.ui.handlers;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.UpdateProfileDto;
import cli.tutoeasy.service.ProfileService;
import cli.tutoeasy.util.input.SecurePasswordReader;

import java.util.Scanner;

/**
 * <p>Handler for profile management operations.</p>
 * <p>Allows users to edit their profile information such as username, email, password, etc.</p>
 */
public class ProfileHandler {

    /**
     * <p>Handles the profile editing process.</p>
     * <p>Prompts the user for new values for their profile fields.
     * Empty inputs are ignored (no change).</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handleEditProfile(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                     EDIT PROFILE                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        System.out.println("Leave fields blank if you do not wish to change them\n");

        System.out.print("New username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) username = null;

        System.out.print("New email: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) email = null;

        String currentPassword = SecurePasswordReader.readPassword("Current password (required to change password): ");
        if (currentPassword.isEmpty()) currentPassword = null;

        String newPassword = null;
        if (currentPassword != null) {
            newPassword = SecurePasswordReader.readPassword("Password (Minimal 5 characters, letras (uppercase and lowercase), numbers y symbols): ");
            if (newPassword.isEmpty()) newPassword = null;
        }

        System.out.print("Career: ");
        String career = scanner.nextLine().trim();
        if (career.isEmpty()) career = null;

        System.out.print("Replace full description: ");
        String newDescription = scanner.nextLine().trim();
        if (newDescription.isEmpty()) newDescription = null;

        UpdateProfileDto dto = new UpdateProfileDto(
                username, email, currentPassword, newPassword,
                career, "", newDescription
        );

        if (!dto.hasUpdates()) {
            System.out.println("\n No changes specified");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        ProfileService service = factory.getProfileService();
        int userId = AuthSession.getCurrentUser().getId();
        var response = service.updateProfile(userId, dto);

        if (response.success()) {
            System.out.println("\n " + response.message());
            if (username != null || email != null) {
                var updatedUser = service.getUserById(userId);
                if (updatedUser != null) {
                    AuthSession.login(updatedUser);
                }
            }
        } else {
            System.out.println("\n " + response.message());
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Clears the console screen.</p>
     */
    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }
}