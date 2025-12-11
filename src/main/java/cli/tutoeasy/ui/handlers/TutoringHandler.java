package cli.tutoeasy.ui.handlers;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.*;
import cli.tutoeasy.service.StudentTutoringService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * <p>Handler for student tutoring operations.</p>
 * <p>Manages creating requests, viewing upcoming sessions, and history.</p>
 */
public class TutoringHandler {

    /**
     * <p>Handles the creation of a new tutoring request by a student.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handleCreateRequest(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                   REQUEST TUTORING                         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        System.out.print("Tutor username: ");
        String tutorUsername = scanner.nextLine().trim();

        System.out.print("Subject (name or ID): ");
        String subject = scanner.nextLine().trim();

        System.out.print("Topic (optional, Enter to skip): ");
        String topic = scanner.nextLine().trim();
        if (topic.isEmpty()) topic = null;

        System.out.print("Date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine().trim();

        System.out.print("Time (HH:MM 24h format): ");
        String timeStr = scanner.nextLine().trim();

        if (tutorUsername.isEmpty() || subject.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty()) {
            System.out.println("\n The fields tutor, subject, date, and time are mandatory");
            System.out.println("Presiona Enter para continuar...");
            scanner.nextLine();
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));

            CreateTutoringRequestDto dto = new CreateTutoringRequestDto(
                    subject, tutorUsername, topic, date, time
            );

            StudentTutoringService service = factory.getStudentTutoringService();
            int studentId = AuthSession.getCurrentUser().getId();
            var response = service.createTutoringRequest(studentId, dto);

            if (response.success()) {
                System.out.println("\n " + response.message());
            } else {
                System.out.println("\n " + response.message());
            }

        } catch (DateTimeParseException e) {
            System.out.println("\n Invalid date or time format");
            System.out.println("   Use format YYYY-MM-DD for date and HH:MM for time");
        } catch (Exception e) {
            System.out.println("\n Error: " + e.getMessage());
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Handles viewing upcoming tutoring sessions for a student.</p>
     * <p>Allows cancelling, completing, or updating sessions.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handleViewUpcoming(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                UPCOMING SESSIONS                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        StudentTutoringService service = factory.getStudentTutoringService();
        int studentId = AuthSession.getCurrentUser().getId();

        List<StudentTutoringDto> tutorings = service.getUpcomingTutorings(studentId);

        if (tutorings.isEmpty()) {
            System.out.println(" You have no upcoming tutoring sessions");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        for (int i = 0; i < tutorings.size(); i++) {
            var t = tutorings.get(i);
            String statusSymbol = getStatusSymbol(t.status());
            String topicInfo = t.topicName() != null ? " - " + t.topicName() : "";

            System.out.println((i + 1) + ". " + statusSymbol + " " + t.subjectName() + topicInfo);
            System.out.println("   Tutor: " + t.tutorUsername());
            System.out.println("   " + t.meetingDate() + " at " + t.meetingTime());
            System.out.println("   Status: " + t.status() + " (ID: " + t.tutoringId() + ")");
            System.out.println();
        }

        System.out.println("\nDo you want to manage a session?");
        System.out.println("1. Cancel tutoring");
        System.out.println("2. Mark as completed");
        System.out.println("3. Update tutoring");
        System.out.println("0. Return");
        System.out.print("\nOption: ");

        String input = scanner.nextLine().trim();
        try {
            int option = Integer.parseInt(input);

            switch (option) {
                case 1:
                    handleCancelTutoring(scanner, factory, tutorings);
                    break;
                case 2:
                    handleCompleteTutoring(scanner, factory, tutorings);
                    break;
                case 3:
                    handleUpdateTutoring(scanner, factory, tutorings);
                    break;
                case 0:
                    return;
            }
        } catch (NumberFormatException e) {
            System.out.println("\n Invalid option");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Handles viewing the history of tutoring sessions.</p>
     * <p>Allows filtering by limit, status, and subject.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handleViewHistory(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║               TUTORING HISTORY                             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        System.out.println("Filters (optional, Enter to skip):");
        System.out.print("Limit of results: ");
        String limitStr = scanner.nextLine().trim();
        Integer limit = limitStr.isEmpty() ? null : Integer.parseInt(limitStr);

        System.out.print("Status (completed/canceled): ");
        String status = scanner.nextLine().trim();
        if (status.isEmpty()) status = null;

        System.out.print("Subject: ");
        String subject = scanner.nextLine().trim();
        if (subject.isEmpty()) subject = null;

        StudentTutoringService service = factory.getStudentTutoringService();
        int studentId = AuthSession.getCurrentUser().getId();

        List<StudentTutoringHistoryDto> history = service.getTutoringHistory(
                studentId, limit, status, subject
        );

        if (history.isEmpty()) {
            System.out.println("\n No tutoring sessions in your history with these filters");
        } else {
            System.out.println();
            for (int i = 0; i < history.size(); i++) {
                var h = history.get(i);
                String statusSymbol = h.status().equals("completed") ? "[OK]" : "[X]";
                String topicInfo = h.topicName() != null ? " - " + h.topicName() : "";

                System.out.println((i + 1) + ". " + statusSymbol + " " + h.subjectName() + topicInfo);
                System.out.println("   Tutor: " + h.tutorUsername());
                System.out.println("   " + h.meetingDate() + " at " + h.meetingTime());
                System.out.println("   Status: " + h.status());
                System.out.println();
            }
            System.out.println("Total: " + history.size() + " record(s)");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Handles the cancellation of a tutoring session.</p>
     *
     * @param scanner   Scanner for reading user input.
     * @param factory   AppFactory for accessing services.
     * @param tutorings List of tutoring sessions (unused but kept for context/extension).
     */
    private static void handleCancelTutoring(Scanner scanner, AppFactory factory,
                                             List<StudentTutoringDto> tutorings) {
        System.out.print("\nID of tutoring to cancel: ");
        String idStr = scanner.nextLine().trim();

        try {
            int tutoringId = Integer.parseInt(idStr);
            StudentTutoringService service = factory.getStudentTutoringService();
            int studentId = AuthSession.getCurrentUser().getId();

            var response = service.cancelTutoring(studentId, tutoringId);

            if (response.success()) {
                System.out.println("\n " + response.message());
            } else {
                System.out.println("\n " + response.message());
            }
        } catch (NumberFormatException e) {
            System.out.println("\n Invalid ID");
        } catch (Exception e) {
            System.out.println("\n Error: " + e.getMessage());
        }
    }

    /**
     * <p>Handles marking a tutoring session as completed.</p>
     *
     * @param scanner   Scanner for reading user input.
     * @param factory   AppFactory for accessing services.
     * @param tutorings List of tutoring sessions.
     */
    private static void handleCompleteTutoring(Scanner scanner, AppFactory factory,
                                               List<StudentTutoringDto> tutorings) {
        System.out.print("\nID of completed tutoring: ");
        String idStr = scanner.nextLine().trim();

        try {
            int tutoringId = Integer.parseInt(idStr);
            StudentTutoringService service = factory.getStudentTutoringService();
            int studentId = AuthSession.getCurrentUser().getId();

            var response = service.completeTutoring(studentId, tutoringId);

            if (response.success()) {
                System.out.println("\n " + response.message());
            } else {
                System.out.println("\n " + response.message());
            }
        } catch (NumberFormatException e) {
            System.out.println("\n Invalid ID");
        } catch (Exception e) {
            System.out.println("\n Error: " + e.getMessage());
        }
    }

    /**
     * <p>Handles updating a tutoring session.</p>
     *
     * @param scanner   Scanner for reading user input.
     * @param factory   AppFactory for accessing services.
     * @param tutorings List of tutoring sessions.
     */
    private static void handleUpdateTutoring(Scanner scanner, AppFactory factory,
                                             List<StudentTutoringDto> tutorings) {
        System.out.print("\nID of tutoring to update: ");
        String idStr = scanner.nextLine().trim();

        try {
            int tutoringId = Integer.parseInt(idStr);

            System.out.print("New date (YYYY-MM-DD, Enter to keep): ");
            String dateStr = scanner.nextLine().trim();
            LocalDate newDate = dateStr.isEmpty() ? null :
                    LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);

            System.out.print("New time (HH:MM, Enter to keep): ");
            String timeStr = scanner.nextLine().trim();
            LocalTime newTime = timeStr.isEmpty() ? null :
                    LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));

            System.out.print("New topic (Enter to keep): ");
            String newTopic = scanner.nextLine().trim();
            if (newTopic.isEmpty()) newTopic = null;

            UpdateTutoringRequestDto dto = new UpdateTutoringRequestDto(
                    tutoringId, newTopic, newDate, newTime
            );

            StudentTutoringService service = factory.getStudentTutoringService();
            int studentId = AuthSession.getCurrentUser().getId();

            var response = service.updateTutoring(studentId, dto);

            if (response.success()) {
                System.out.println("\n " + response.message());
            } else {
                System.out.println("\n " + response.message());
            }

        } catch (DateTimeParseException e) {
            System.out.println("\n Invalid date or time format");
        } catch (NumberFormatException e) {
            System.out.println("\n Invalid ID");
        } catch (Exception e) {
            System.out.println("\n Error: " + e.getMessage());
        }
    }

    /**
     * <p>Gets a symbol representing the status.</p>
     *
     * @param status The status string.
     * @return A string symbol.
     */
    private static String getStatusSymbol(String status) {
        return switch (status.toLowerCase()) {
            case "confirmed" -> "[OK]";
            case "unconfirmed" -> "[WAIT]";
            case "canceled" -> "[X]";
            case "completed" -> "[DONE]";
            default -> "[?]";
        };
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