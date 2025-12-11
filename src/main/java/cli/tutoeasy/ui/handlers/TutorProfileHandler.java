package cli.tutoeasy.ui.handlers;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.EditTutorProfileDto;
import cli.tutoeasy.service.TutorService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * <p>Handler for tutor profile editing.</p>
 * <p>Allows tutors to manage their subjects and teaching schedules.</p>
 */
public class TutorProfileHandler {

    /**
     * <p>Handles the editing of tutor subjects and schedules.</p>
     * <p>Prompts the tutor to enter a list of subjects and available time slots.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handleEditTutorProfile(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║            EDIT SUBJECTS AND SCHEDULES                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        int tutorId = AuthSession.getCurrentUser().getId();

        System.out.println("SUBJECTS");
        System.out.println("Enter the subjects you can teach (separated by comma)");
        System.out.print("Subjects: ");
        String subjectsInput = scanner.nextLine().trim();

        if (subjectsInput.isEmpty()) {
            System.out.println("\n You must specify at least one subject");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        List<String> subjects = Arrays.stream(subjectsInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (subjects.isEmpty()) {
            System.out.println("\n You must specify at least one valid subject");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("\nAVAILABLE SCHEDULES");
        System.out.println("Format: Day-HH:MM-HH:MM");
        System.out.println("Example: Monday-10:00-12:00, Tuesday-15:00-17:00");
        System.out.println("Valid days: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday");
        System.out.print("\nSchedules: ");
        String schedulesInput = scanner.nextLine().trim();

        if (schedulesInput.isEmpty()) {
            System.out.println("\n You must specify at least one schedule");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        List<String> validSchedules = new ArrayList<>();
        List<String> validDays = Arrays.asList(
                "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"
        );

        String[] scheduleParts = schedulesInput.split(",");
        for (String schedule : scheduleParts) {
            String trimmed = schedule.trim();
            if (trimmed.isEmpty()) continue;

            String[] parts = trimmed.split("-");
            if (parts.length != 3) {
                System.out.println("  Invalid format ignored: " + trimmed);
                continue;
            }

            String day = parts[0].trim().toLowerCase();
            String start = parts[1].trim();
            String end = parts[2].trim();

            if (!validDays.contains(day)) {
                System.out.println("  Invalid day ignored: " + day);
                continue;
            }

            if (!start.matches("\\d{2}:\\d{2}") || !end.matches("\\d{2}:\\d{2}")) {
                System.out.println("  Invalid times ignored in: " + trimmed);
                continue;
            }

            String capitalizedDay = day.substring(0, 1).toUpperCase() + day.substring(1);
            validSchedules.add(capitalizedDay + "-" + start + "-" + end);
        }

        if (validSchedules.isEmpty()) {
            System.out.println("\n Could not process valid schedules");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        EditTutorProfileDto dto = new EditTutorProfileDto(tutorId, subjects, validSchedules);

        TutorService service = factory.getTutorService();
        var response = service.editTutorProfile(dto);

        if (response.success()) {
            System.out.println("\n " + response.message());
            System.out.println("\nUpdated subjects: " + String.join(", ", subjects));
            System.out.println("Configured schedules: " + validSchedules.size());
        } else {
            System.out.println("\n " + response.message());
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