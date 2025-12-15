package cli.tutoeasy.ui.handlers;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.CreateSessionFeedbackDto;
import cli.tutoeasy.model.dto.StudentTutoringHistoryDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * <p>Handler for student rating operations.</p>
 * <p>Allows students to rate tutors for completed tutoring sessions, providing a numeric rating
 * and an optional comment.</p>
 */
public class StudentRatingHandler {

    /**
     * <p>Handles the menu for submitting feedback for tutors.</p>
     * <p>Lists completed tutoring sessions for the logged-in student, prompts
     * for a session to rate, collects rating (1-5) and comment, and submits it
     * via the feedback service.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handleRateTutor(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                RATE TUTOR                                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        if (!AuthSession.isLoggedIn() || !AuthSession.hasRole("student")) {
            System.out.println("Only logged-in students can submit feedback.");
            return;
        }

        int studentId = AuthSession.getCurrentUser().getId();

        List<StudentTutoringHistoryDto> completedSessions =
                factory.getStudentTutoringService().getCompletedTutorings(studentId);

        if (completedSessions.isEmpty()) {
            System.out.println("\nYou have no completed tutoring sessions to rate.");
            System.out.println("\nPress ENTER to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("\nCompleted Tutoring Sessions:");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (int i = 0; i < completedSessions.size(); i++) {
            StudentTutoringHistoryDto t = completedSessions.get(i);
            String topicInfo = t.topicName() != null ? " - " + t.topicName() : "";
            System.out.printf("%d. Tutor: %s | Subject: %s%s | Date: %s%n",
                    i + 1,
                    t.tutorUsername(),
                    t.subjectName(),
                    topicInfo,
                    LocalDateTime.of(t.meetingDate(), t.meetingTime()).format(formatter));
        }

        int selection = readInt(scanner,
                "\nSelect a tutoring session to rate (0 to cancel): ", 0, completedSessions.size());

        if (selection == 0) return;

        StudentTutoringHistoryDto selectedTutoring = completedSessions.get(selection - 1);

        int rating = readInt(scanner, "Enter rating (1-5): ", 1, 5);
        System.out.print("Enter your comment: ");
        String comment = scanner.nextLine().trim();

        try {
            CreateSessionFeedbackDto dto = new CreateSessionFeedbackDto(
                    selectedTutoring.tutoringId(),
                    studentId,
                    rating,
                    comment,
                    LocalDateTime.now()
            );

            factory.getSessionFeedbackService().addFeedback(dto);
            System.out.println("\nFeedback submitted successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\nError: " + e.getMessage());
        }

        System.out.println("\nPress ENTER to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Reads an integer from the console with unlimited range.</p>
     *
     * @param scanner Scanner for input
     * @param prompt  Message to display
     * @return the integer entered by the user
     */
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

    /**
     * <p>Reads an integer from the console with range validation.</p>
     *
     * @param scanner Scanner for input
     * @param prompt  Message to display
     * @param min     Minimum valid value
     * @param max     Maximum valid value
     * @return the integer entered by the user within [min, max]
     */
    private static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value >= min && value <= max) return value;
            System.out.println("Number must be between " + min + " and " + max + ".");
        }
    }

    /**
     * <p>Clears the console screen.</p>
     * <p>Works on Windows (cmd/cls) and Unix-like systems (ANSI escape codes).</p>
     */
    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}