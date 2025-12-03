package cli.tutoeasy.command.student;

import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.StudentTutoringHistoryDto;
import cli.tutoeasy.service.StudentTutoringService;
import picocli.CommandLine.*;

import java.util.List;

/**
 * Command for students to view their tutoring history.
 * Shows past tutoring sessions that are either completed or canceled.
 */
@Command(name = "history", description = "View your tutoring history (Student)", mixinStandardHelpOptions = true)
public class StudentHistoryCommand implements Runnable {

    /**
     * Number of tutoring sessions to show (default: all).
     */
    @Option(names = { "--limit", "-l" }, description = "Number of tutoring sessions to show (default: all)")
    private Integer limit;

    /**
     * Show only completed tutorings.
     */
    @Option(names = { "--completed", "-c" }, description = "Show only completed tutorings")
    private boolean completedOnly;

    /**
     * Show only canceled tutorings.
     */
    @Option(names = { "--canceled", "-x" }, description = "Show only canceled tutorings")
    private boolean canceledOnly;

    /**
     * Filter by subject name.
     */
    @Option(names = { "--subject", "-s" }, description = "Filter by subject name")
    private String subjectFilter;

    /**
     * Service for student tutoring operations.
     */
    private final StudentTutoringService studentTutoringService;

    /**
     * Constructor with dependency injection
     *
     * @param studentTutoringService Service for student tutoring operations
     */
    public StudentHistoryCommand(StudentTutoringService studentTutoringService) {
        this.studentTutoringService = studentTutoringService;
    }

    @Override
    public void run() {
        if (!AuthSession.isLoggedIn()) {
            String msg = Help.Ansi.AUTO.string("@|red You must be logged in to use this command.|@");
            System.out.println(msg);
            return;
        }

        if (!AuthSession.hasRole("student")) {
            String msg = Help.Ansi.AUTO.string("@|red Access denied. Only students can view tutoring history.|@");
            System.out.println(msg);
            return;
        }

        int studentId = AuthSession.getCurrentUser().getId();

        try {
            showHistory(studentId);
        } catch (Exception e) {
            String msg = Help.Ansi.AUTO.string("@|red ERROR: " + e.getMessage() + "|@");
            System.out.println(msg);
        }
    }

    /**
     * Displays the tutoring history for the student
     */
    private void showHistory(int studentId) {
        if (completedOnly && canceledOnly) {
            String msg = Help.Ansi.AUTO.string(
                    "@|yellow Cannot use --completed and --canceled together. Please choose one.|@");
            System.out.println(msg);
            return;
        }

        String statusFilter = null;
        if (completedOnly) {
            statusFilter = "completed";
        } else if (canceledOnly) {
            statusFilter = "canceled";
        }

        List<StudentTutoringHistoryDto> history = studentTutoringService.getTutoringHistory(
                studentId,
                limit,
                statusFilter,
                subjectFilter);

        if (history.isEmpty()) {
            String msg = Help.Ansi.AUTO.string("@|yellow You have no tutoring history matching the criteria.|@");
            System.out.println(msg);
            return;
        }

        System.out.println(Help.Ansi.AUTO.string("\n@|bold,cyan === Your Tutoring History ===|@\n"));

        if (statusFilter != null) {
            String filterText = statusFilter.equals("completed") ? "Completed" : "Canceled";
            System.out.println(Help.Ansi.AUTO.string(String.format("@|cyan Filter: %s tutorings|@\n", filterText)));
        }

        if (subjectFilter != null) {
            System.out.println(Help.Ansi.AUTO.string(String.format("@|cyan Subject: %s|@\n", subjectFilter)));
        }

        for (int i = 0; i < history.size(); i++) {
            var record = history.get(i);

            String statusColor = getStatusColor(record.status());
            String topicInfo = record.topicName() != null ? " - " + record.topicName() : "";

            System.out.println(Help.Ansi.AUTO.string(String.format(
                    "@|bold %d.|@ @|blue %s|@%s",
                    i + 1,
                    record.subjectName(),
                    topicInfo)));

            System.out.println(Help.Ansi.AUTO.string(String.format(
                    "    Tutor: @|green %s|@",
                    record.tutorUsername())));

            System.out.println(Help.Ansi.AUTO.string(String.format(
                    "    Date: @|yellow %s at %s|@",
                    record.meetingDate(),
                    record.meetingTime())));

            System.out.println(Help.Ansi.AUTO.string(String.format(
                    "    Status: @|%s %s|@ (ID: @|bold %d|@)",
                    statusColor,
                    record.status(),
                    record.tutoringId())));

            System.out.println();
        }

        System.out.println(Help.Ansi.AUTO.string(String.format(
                "@|cyan Total records shown: %d|@", history.size())));

        System.out.println(Help.Ansi.AUTO.string(
                "\n@|cyan Filter options:|@\n" +
                        "  --limit=N          Show last N tutorings\n" +
                        "  --completed        Show only completed tutorings\n" +
                        "  --canceled         Show only canceled tutorings\n" +
                        "  --subject=\"name\"   Filter by subject name"));
    }

    /**
     * Gets color for status display
     */
    private String getStatusColor(String status) {
        return switch (status.toLowerCase()) {
            case "completed" -> "green";
            case "canceled" -> "red";
            default -> "white";
        };
    }
}