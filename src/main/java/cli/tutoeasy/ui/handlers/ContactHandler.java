package cli.tutoeasy.ui.handlers;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.model.dto.ContactInfoDto;
import cli.tutoeasy.service.ContactService;

import java.util.Scanner;

/**
 * <p>Handler for displaying contact information.</p>
 */
public class ContactHandler {

    /**
     * <p>Handles the view of contact information for a specific user.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handleViewContact(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                  CONTACT INFORMATION                       ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        System.out.print("Username to search: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("\n Username cannot be empty");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        ContactService service = factory.getContactService();
        ContactInfoDto contact = service.getContactInfoByUsername(username);

        if (contact == null) {
            System.out.println("\n User not found: " + username);
        } else {
            System.out.println("\n Contact Information");
            System.out.println("─".repeat(60));
            System.out.println("ID:      " + contact.userId());
            System.out.println("Name:    " + contact.username());
            System.out.println("Email:   " + contact.email());
            System.out.println("Role:    " + contact.role());
            System.out.println("Career:  " + (contact.careerName() != null ? contact.careerName() : "Not specified"));
        }

        System.out.println("\nPress Enter to continue...");
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