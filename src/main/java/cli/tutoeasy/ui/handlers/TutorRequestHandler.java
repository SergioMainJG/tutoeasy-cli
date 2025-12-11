package cli.tutoeasy.ui.handlers;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.*;
import cli.tutoeasy.service.TutorService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * <p>Handler for managing tutoring requests and sessions from the tutor's perspective.</p>
 */
public class TutorRequestHandler {

    /**
     * <p>Handles the display and management of pending tutoring requests.</p>
     * <p>Allows the tutor to view, accept, or reject requests.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handlePendingRequests(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              PENDING REQUESTS                              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        TutorService service = factory.getTutorService();
        int tutorId = AuthSession.getCurrentUser().getId();

        try {
            List<TutorTutoringRequestDto> requests = service.getPending(tutorId);

            if (requests.isEmpty()) {
                System.out.println(" You have no pending requests");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }

            for (int i = 0; i < requests.size(); i++) {
                var req = requests.get(i);
                String topicInfo = req.topicName().equals("No topic") ? "" : " - " + req.topicName();

                System.out.println((i + 1) + ". " + req.subjectName() + topicInfo);
                System.out.println("   Student: " + req.studentName());
                System.out.println("   " + req.meetingDate() + " at " + req.meetingTime());
                System.out.println("   ID: " + req.tutoringId());
                System.out.println();
            }

            System.out.println("\nWhat would you like to do?");
            System.out.println("1. Accept request");
            System.out.println("2. Reject request");
            System.out.println("0. Return");
            System.out.print("\nOption: ");

            String input = scanner.nextLine().trim();
            int option = Integer.parseInt(input);

            switch (option) {
                case 1:
                    handleAcceptRequest(scanner, factory);
                    break;
                case 2:
                    handleRejectRequest(scanner, factory);
                    break;
                case 0:
                    return;
            }

        } catch (NumberFormatException e) {
            System.out.println("\n Invalid option");
        } catch (Exception e) {
            System.out.println("\n Error: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Handles the display and management of confirmed tutoring sessions.</p>
     * <p>Allows the tutor to view, cancel, complete, or update sessions.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handleConfirmedSessions(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              CONFIRMED SESSIONS                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        TutorService service = factory.getTutorService();
        int tutorId = AuthSession.getCurrentUser().getId();

        List<TutorTutoringDto> sessions = service.getUpcomingSessions(tutorId);

        if (sessions.isEmpty()) {
            System.out.println(" No confirmed sessions coming up");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        for (int i = 0; i < sessions.size(); i++) {
            var s = sessions.get(i);
            String topicInfo = s.topicName() != null ? " - " + s.topicName() : "";

            System.out.println((i + 1) + ". " + s.subjectName() + topicInfo);
            System.out.println("   Student: " + s.studentUsername());
            System.out.println("   " + s.meetingDate() + " at " + s.meetingTime());
            System.out.println("   Status: " + s.status() + " (ID: " + s.tutoringId() + ")");
            System.out.println();
        }

        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Cancel session");
        System.out.println("2. Mark as completed");
        System.out.println("3. Update date/time");
        System.out.println("0. Return");
        System.out.print("\nOption: ");

        try {
            String input = scanner.nextLine().trim();
            int option = Integer.parseInt(input);

            switch (option) {
                case 1:
                    handleCancelSession(scanner, factory);
                    break;
                case 2:
                    handleCompleteSession(scanner, factory);
                    break;
                case 3:
                    handleUpdateSession(scanner, factory);
                    break;
                case 0:
                    return;
            }

        } catch (NumberFormatException e) {
            System.out.println("\n Invalid option");
        } catch (Exception e) {
            System.out.println("\n Error: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Handles the acceptance of a tutoring request.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    private static void handleAcceptRequest(Scanner scanner, AppFactory factory) {
        System.out.print("\nID of request to accept: ");
        String idStr = scanner.nextLine().trim();

        try {
            int tutoringId = Integer.parseInt(idStr);
            TutorService service = factory.getTutorService();
            int tutorId = AuthSession.getCurrentUser().getId();

            var response = service.accept(tutorId, tutoringId);

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
     * <p>Handles the rejection of a tutoring request.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    private static void handleRejectRequest(Scanner scanner, AppFactory factory) {
        System.out.print("\nID of request to reject: ");
        String idStr = scanner.nextLine().trim();

        try {
            int tutoringId = Integer.parseInt(idStr);
            TutorService service = factory.getTutorService();
            int tutorId = AuthSession.getCurrentUser().getId();

            var response = service.reject(tutorId, tutoringId);

            if (response.success()) {
                System.out.println("\n  " + response.message());
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
     * <p>Handles the cancellation of a confirmed session.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    private static void handleCancelSession(Scanner scanner, AppFactory factory) {
        System.out.print("\nID of session to cancel: ");
        String idStr = scanner.nextLine().trim();

        try {
            int tutoringId = Integer.parseInt(idStr);
            TutorService service = factory.getTutorService();
            int tutorId = AuthSession.getCurrentUser().getId();

            var response = service.cancelTutoring(tutorId, tutoringId);

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
     * <p>Handles marking a session as completed.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    private static void handleCompleteSession(Scanner scanner, AppFactory factory) {
        System.out.print("\nID of completed session: ");
        String idStr = scanner.nextLine().trim();

        try {
            int tutoringId = Integer.parseInt(idStr);
            TutorService service = factory.getTutorService();
            int tutorId = AuthSession.getCurrentUser().getId();

            var response = service.completeTutoring(tutorId, tutoringId);

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
     * <p>Handles updating the date and time of a session.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    private static void handleUpdateSession(Scanner scanner, AppFactory factory) {
        System.out.print("\nID of session to update: ");
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

            if (newDate == null && newTime == null) {
                System.out.println("\n  No changes specified");
                return;
            }

            UpdateTutoringDto dto = new UpdateTutoringDto(tutoringId, newDate, newTime);

            TutorService service = factory.getTutorService();
            int tutorId = AuthSession.getCurrentUser().getId();

            var response = service.updateTutoring(tutorId, dto);

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