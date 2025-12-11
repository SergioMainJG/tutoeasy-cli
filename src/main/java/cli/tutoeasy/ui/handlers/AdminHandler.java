
package cli.tutoeasy.ui.handlers;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.model.dto.CreateAdministratorDto;
import cli.tutoeasy.service.AdministratorService;
import cli.tutoeasy.util.input.SecurePasswordReader;
import cli.tutoeasy.util.validations.CommonValidation;

import java.util.Scanner;

/**
 * <p>Handler for administrator-related operations.</p>
 * <p>Manages the creation of new administrator accounts.</p>
 */
public class AdminHandler {

    /**
     * <p>Handles the creation of a new administrator account.</p>
     * <p>This method:</p>
     * <ul>
     *     <li>Prompts for admin details (first name, last name, email, password)</li>
     *     <li>Validates all inputs according to system rules</li>
     *     <li>Creates the administrator account if validations pass</li>
     *     <li>Displays confirmation or error messages</li>
     * </ul>
     *
     * <p>Validation rules:</p>
     * <ul>
     *     <li>Name: 3-20 letters only</li>
     *     <li>Lastname: 3-20 letters only</li>
     *     <li>Email: format first.last@jala.university</li>
     *     <li>Password: minimum 5 characters including letters, numbers, and symbols</li>
     * </ul>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handleCreateAdmin(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                CREATE NEW ADMINISTRATOR                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        System.out.println("Please enter the new administrator's details:\n");

        System.out.print("First Name (3-20 letters): ");
        String name = scanner.nextLine().trim();

        System.out.print("Last Name (3-20 letters): ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Email (format: first.last@jala.university): ");
        String email = scanner.nextLine().trim();

        String password = SecurePasswordReader.readPassword("Password (Minimal 5 characters, letras (uppercase and lowercase), numbers y symbols): ");

        if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("\n Error: All fields are mandatory");
            System.out.println("   Cannot create an administrator with empty fields.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (!CommonValidation.isValidName(name)) {
            System.out.println("\n Error: Invalid First Name");
            System.out.println("   The name must have between 3-20 letters.");
            System.out.println("   Numbers and special characters are not allowed.");
            System.out.println("   Valid example: John");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (!CommonValidation.isValidName(lastName)) {
            System.out.println("\n Error: Invalid Last Name");
            System.out.println("   The last name must have between 3-20 letters.");
            System.out.println("   Numbers and special characters are not allowed.");
            System.out.println("   Valid example: Doe");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (!CommonValidation.isValidEmailFormatAdmin(email)) {
            System.out.println("\n Error: Invalid Email for Administrator");
            System.out.println("   The email must follow the format: first.last@jala.university");
            System.out.println("   Valid example: john.doe@jala.university");
            System.out.println("   Note: Must not include numbers before the @");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (!CommonValidation.isValidPassword(password)) {
            System.out.println("\n Error: Weak Password");
            System.out.println("   The password must comply with:");
            System.out.println("   - Minimum 5 characters");
            System.out.println("   - At least one letter");
            System.out.println("   - At least one number");
            System.out.println("   - At least one symbol (!, @, #, $, etc.)");
            System.out.println("   Valid example: Pass123!");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        CreateAdministratorDto dto = new CreateAdministratorDto(name, lastName, email, password);

        AdministratorService service = factory.getAdminService();
        var response = service.createAdministrator(dto);

        if (response.success()) {
            System.out.println("\n " + response.message());
            System.out.println("\n Created Administrator Summary:");
            System.out.println("   ┌─────────────────────────────────────────────");
            System.out.println("   │ Full Name: " + name + " " + lastName);
            System.out.println("   │ Email: " + email);
            System.out.println("   │ Role: Administrator");
            System.out.println("   │ Status: Active");
            System.out.println("   └─────────────────────────────────────────────");
            System.out.println("\n The new administrator can now log in with their credentials.");
        } else {
            System.out.println("\n " + response.message());
            System.out.println("\n Possible causes:");
            System.out.println("   - The email is already registered in the system");
            System.out.println("   - Database connection error");
            System.out.println("   - Insufficient permissions");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Clears the console screen.</p>
     * <p>Works on Windows (cmd/cls) and Unix-like systems (ANSI escape codes).</p>
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
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}
