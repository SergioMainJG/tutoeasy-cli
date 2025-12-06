package cli.tutoeasy.command.tutor;

import cli.tutoeasy.model.dto.EditTutorProfileDto;
import cli.tutoeasy.model.dto.ActionResponseDto;
import cli.tutoeasy.service.TutorService;
import picocli.CommandLine.*;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Command for editing a tutor's profile, including subjects and availability.
 *
 * <p>
 * This command allows updating a tutor's subjects and their available schedule slots.
 * The tutor is identified using the required {@code --tutor-id} option.
 * </p>
 *
 * <p>
 * Usage example:
 * <pre>
 * java -jar tutoeasy-cli.jar edit-tutor-profile --tutor-id 1
 * </pre>
 * After running, the user is prompted to input:
 * <ul>
 *     <li>Subjects (comma-separated, e.g., Math, Logic)</li>
 *     <li>Schedules in the format: Day-HH:mm-HH:mm (comma-separated,
 *         e.g., Monday-10:00-12:00, Tuesday-15:00-17:00)</li>
 * </ul>
 * Only valid days (Monday-Sunday) and hour formats (HH:mm) are accepted.
 * Invalid entries are ignored with a warning message.
 * </p>
 *
 * <p>
 * This command internally uses {@link TutorService#editTutorProfile(EditTutorProfileDto)}
 * to update the tutor's profile in the database.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see TutorService
 * @see EditTutorProfileDto
 * @see ActionResponseDto
 */
@Command(
        name = "edit-tutor-profile",
        description = "Edit tutor subjects and availability",
        mixinStandardHelpOptions = true
)
public class EditTutorProfileCommand implements Runnable {

    @Option(names = {"--tutor-id"}, required = true, description = "Tutor ID")
    private int tutorId;

    private final TutorService tutorService;

    /**
     * Constructs a new instance of the command with the specified TutorService.
     *
     * @param tutorService The service used to perform tutor-related operations.
     */
    public EditTutorProfileCommand(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    /**
     * Executes the command: prompts the user for subjects and schedules,
     * validates the input, and updates the tutor's profile.
     */
    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter subjects (separated by commas):");
        List<String> subjectNames = Arrays.stream(sc.nextLine().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        System.out.println("Enter schedules (e.g., Monday-10:00-12:00, Tuesday-15:00-17:00):");
        List<String> validatedSlots = Arrays.stream(sc.nextLine().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(slot -> {
                    String[] p = slot.split("-");
                    if (p.length != 3) {
                        System.out.println("Invalid format: " + slot);
                        return null;
                    }
                    String day = p[0].trim();
                    String start = p[1].trim();
                    String end = p[2].trim();

                    List<String> validDays = List.of(
                            "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"
                    );

                    if (!validDays.contains(day.toLowerCase())) {
                        System.out.println("Invalid day: " + day);
                        return null;
                    }

                    if (!start.matches("\\d{2}:\\d{2}") || !end.matches("\\d{2}:\\d{2}")) {
                        System.out.println("Invalid hours in: " + slot);
                        return null;
                    }

                    return day + "-" + start + "-" + end;
                })
                .filter(s -> s != null)
                .toList();

        if (validatedSlots.isEmpty()) {
            System.out.println("No valid schedule could be processed");
            return;
        }

        EditTutorProfileDto dto = new EditTutorProfileDto(tutorId, subjectNames, validatedSlots);
        ActionResponseDto result = tutorService.editTutorProfile(dto);
        System.out.println(result.message());
    }
}
