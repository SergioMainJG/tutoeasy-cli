package cli.tutoeasy.ui.handlers;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.CreateTutorFeedbackDto;
import cli.tutoeasy.model.dto.TutorTutoringRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * <p>Handles rating of students by tutors.</p>
 * <p>Tutors can view completed tutoring sessions and submit ratings with comments for each student.</p>
 */
public class TutorRatingHandler {

    /**
     * <p>Displays completed tutoring sessions for the logged-in tutor and allows submitting a rating.</p>
     *
     * @param scanner Scanner for reading user input
     * @param factory AppFactory for accessing services
     */
    public static void handleRateStudent(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                RATE STUDENT                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        if (!AuthSession.isLoggedIn() || !AuthSession.hasRole("tutor")) {
            System.out.println("Only tutors can submit feedback.");
            return;
        }

        int tutorId = AuthSession.getCurrentUser().getId();

        List<TutorTutoringRequestDto> completedSessions =
                factory.getTutorService().getCompletedTutorings(tutorId);

        if (completedSessions.isEmpty()) {
            System.out.println("\nYou have no completed tutoring sessions to rate.");
            System.out.println("\nPress ENTER to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("\nCompleted Tutoring Sessions:");
        for (int i = 0; i < completedSessions.size(); i++) {
            TutorTutoringRequestDto t = completedSessions.get(i);
            System.out.printf("%d. Student: %s | Subject: %s | Date: %s %s%n",
                    i + 1, t.studentName(), t.subjectName(), t.meetingDate(), t.meetingTime());
        }

        int selection = readInt(scanner, "\nSelect a tutoring session to rate (0 to cancel): ", 0, completedSessions.size());
        if (selection == 0) return;

        TutorTutoringRequestDto selected = completedSessions.get(selection - 1);
        int rating = readInt(scanner, "Enter rating (1-5): ", 1, 5);

        System.out.print("Enter comment: ");
        String comment = scanner.nextLine().trim();

        CreateTutorFeedbackDto dto = new CreateTutorFeedbackDto(
                selected.tutoringId(),
                tutorId,
                rating,
                comment,
                LocalDateTime.now()
        );

        try {
            factory.getSessionFeedbackService().addTutorFeedback(dto);
            System.out.println("\nStudent evaluation submitted successfully.");
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }

        System.out.println("\nPress ENTER to continue...");
        scanner.nextLine();
    }

    /** Reads an integer from the user input with prompt */
    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    /** Reads an integer with min and max validation */
    private static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value >= min && value <= max) return value;
            System.out.println("Number must be between " + min + " and " + max + ".");
        }
    }

    /** Clears the console screen */
    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
