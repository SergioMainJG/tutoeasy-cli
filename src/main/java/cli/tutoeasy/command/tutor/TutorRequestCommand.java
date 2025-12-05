package cli.tutoeasy.command.tutor;

import cli.tutoeasy.model.dto.*;
import cli.tutoeasy.service.*;
import cli.tutoeasy.config.session.AuthSession;
import picocli.CommandLine;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * <p>
 * This class represents the command-line interface for tutors to manage tutoring requests.
 * It allows tutors to view pending requests, accept them, or reject them.
 * </p>
 *
 * <p>
 * The class is annotated with {@code @CommandLine.Command} to define its name, description,
 * and other properties for the picocli framework.
 * </p>
 *
 * <p>
 * To use this command, the tutor must be logged in. The command provides options to:
 * </p>
 * <ul>
 *     <li>Show all pending requests using the {@code --show} or {@code -s} option.</li>
 *     <li>Accept a request by its ID using the {@code --accept} or {@code -a} option.</li>
 *     <li>Reject a request by its ID using the {@code --reject} or {@code -r} option.</li>
 * </ul>
 *
 * @version 1.0
 * @see TutorService
 * @see AuthSession
 * @see TutorTutoringRequestDto
 * @since 1.0
 */
@CommandLine.Command(
        name = "requests",
        description = "View, accept or reject tutoring requests (Tutor only)",
        mixinStandardHelpOptions = true
)
public class TutorRequestCommand implements Runnable {

    /**
     * The service responsible for handling tutor-related business logic, such as
     * accepting or rejecting tutoring requests and fetching pending requests.
     */
    private final TutorService tutorService;

    /**
     * Constructs a new instance of {@code TutorRequestCommand}.
     *
     * @param tutorService The service that provides tutor-related functionalities.
     */
    public TutorRequestCommand(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    /**
     * A command-line option to show all pending tutoring requests. When this option
     * is used, the application will display a list of all requests awaiting the tutor's
     * decision.
     */
    @CommandLine.Option(names = {"--show", "-s"}, description = "Show all pending requests")
    private boolean showRequests;

    /**
     * A command-line option to accept a tutoring request by its unique identifier.
     * The tutor must provide the ID of the request they wish to accept.
     */
    @CommandLine.Option(names = {"--accept", "-a"}, description = "Accept a tutoring request by ID")
    private Integer acceptId;

    /**
     * A command-line option to reject a tutoring request by its unique identifier.
     * The tutor must provide the ID of the request they wish to reject.
     */
    @CommandLine.Option(names = {"--reject", "-r"}, description = "Reject a tutoring request by ID")
    private Integer rejectId;

    @CommandLine.Option(names = {"--list", "-l"}, description = "Show all confirmed upcoming sessions")
    private boolean listSessions;

    @CommandLine.Option(names = {"--cancel", "-c"}, description = "Cancel a confirmed tutoring session by ID")
    private Integer cancelId;

    @CommandLine.Option(names = {"--complete"}, description = "Mark a tutoring session as completed by ID")
    private Integer completeId;

    @CommandLine.Option(names = {"--update", "-u"}, description = "Update a tutoring session by ID")
    private Integer updateId;

    @CommandLine.Option(names = {"--date"}, description = "New date for update (YYYY-MM-DD)")
    private String newDateStr;

    @CommandLine.Option(names = {"--time"}, description = "New time for update (HH:MM)")
    private String newTimeStr;

    /**
     * The main entry point for the command's execution. This method is called by the
     * picocli framework when the command is invoked. It handles the logic for
     * verifying user authentication and authorization, and then delegates the action
     * (accept, reject, or show requests) to the appropriate method.
     */
    @Override
    public void run() {
        if (!AuthSession.isLoggedIn()) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|red You must be logged in to use this command.|@");
            System.out.println(msg);
            return;
        }

        if (!AuthSession.hasRole("tutor")) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|red Access denied. Only tutors can manage requests.|@");
            System.out.println(msg);
            return;
        }

        var user = AuthSession.getCurrentUser();
        int tutorId = user.getId();

        try {
            if (acceptId != null) {
                acceptRequest(tutorId);
            } else if (rejectId != null) {
                rejectRequest(tutorId);
            } else if (cancelId != null) {
                cancelSession(tutorId);
            } else if (completeId != null) {
                completeSession(tutorId);
            } else if (updateId != null) {
                updateSession(tutorId);
            } else if (listSessions) {
                showConfirmedSessions(tutorId);
            } else showPendingRequests(tutorId);
        } catch (Exception e) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|red ERROR: " + e.getMessage() + "|@");
            System.out.println(msg);
        }
    }

    /**
     * Accepts a tutoring request
     */
    private void acceptRequest(int tutorId) {
        var res = tutorService.accept(tutorId, acceptId);
        if (res.success()) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|green " + res.message() + "|@");
            System.out.println(msg);
        } else {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|red " + res.message() + "|@");
            System.out.println(msg);
        }
    }
    /**
     * Rejects a tutoring request
     */
    private void rejectRequest(int tutorId) {
        var res = tutorService.reject(tutorId, rejectId);
        if (res.success()) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|yellow " + res.message() + "|@");
            System.out.println(msg);
        } else {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|red " + res.message() + "|@");
            System.out.println(msg);
        }
    }

    /**
     * Cancels a confirmed tutoring session
     */
    private void cancelSession(int tutorId) {
        var res = tutorService.cancelTutoring(tutorId, cancelId);
        if (res.success()) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|yellow " + res.message() + "|@");
            System.out.println(msg);
        } else {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|red " + res.message() + "|@");
            System.out.println(msg);
        }
    }

    /**
     * Marks a tutoring session as completed
     */
    private void completeSession(int tutorId) {
        var res = tutorService.completeTutoring(tutorId, completeId);
        if (res.success()) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|green " + res.message() + "|@");
            System.out.println(msg);
        } else {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|red " + res.message() + "|@");
            System.out.println(msg);
        }
    }

    /**
     * Updates a tutoring session (date and time only)
     */
    private void updateSession(int tutorId) {
        LocalDate newDate = null;
        LocalTime newTime = null;

        if (newDateStr != null) {
            try {
                newDate = LocalDate.parse(newDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                String msg = CommandLine.Help.Ansi.AUTO.string("@|red Invalid date format. Use YYYY-MM-DD|@");
                System.out.println(msg);
                return;
            }
        }

        if (newTimeStr != null) {
            try {
                newTime = LocalTime.parse(newTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                String msg = CommandLine.Help.Ansi.AUTO.string("@|red Invalid time format. Use HH:MM|@");
                System.out.println(msg);
                return;
            }
        }

        if (newDate == null && newTime == null) {
            String msg = CommandLine.Help.Ansi.AUTO.string(
                    "@|yellow No changes specified. Use --new-date and/or --new-time to update.|@");
            System.out.println(msg);
            return;
        }

        UpdateTutoringDto dto = new UpdateTutoringDto(updateId, newDate, newTime);
        var res = tutorService.updateTutoring(tutorId, dto);

        if (res.success()) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|green " + res.message() + "|@");
            System.out.println(msg);
        } else {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|red " + res.message() + "|@");
            System.out.println(msg);
        }
    }

    /**
     * Displays the list of pending tutoring requests for the currently logged-in tutor.
     * If there are no pending requests, it prints a message indicating so. Otherwise,
     * it formats and displays the details of each request, including the subject,
     * student name, and scheduled time.
     *
     * @param tutorId The ID of the tutor whose pending requests are to be displayed.
     */
    private void showPendingRequests(int tutorId) {
        List<TutorTutoringRequestDto> list = tutorService.getPending(tutorId);

        if (list.isEmpty()) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|yellow No pending requests.|@");
            System.out.println(msg);
            return;
        }

        System.out.println(CommandLine.Help.Ansi.AUTO.string("\n@|bold,cyan === Pending Tutoring Requests ===|@\n"));

        for (int i = 0; i < list.size(); i++) {
            var r = list.get(i);

            String topicInfo = r.topicName() != null && !r.topicName().equals("No topic")
                    ? " - " + r.topicName()
                    : "";

            System.out.println(CommandLine.Help.Ansi.AUTO.string(String.format(
                    "@|bold %d.|@ @|blue %s|@%s",
                    i + 1,
                    r.subjectName(),
                    topicInfo
            )));

            System.out.println(CommandLine.Help.Ansi.AUTO.string(String.format(
                    "    Student: @|green %s|@",
                    r.studentName()
            )));

            System.out.println(CommandLine.Help.Ansi.AUTO.string(String.format(
                    "    Scheduled: @|yellow %s at %s|@",
                    r.meetingDate(),
                    r.meetingTime()
            )));

            System.out.println(CommandLine.Help.Ansi.AUTO.string(String.format(
                    "    ID: @|bold %d|@",
                    r.tutoringId()
            )));

            System.out.println();
        }

        System.out.println(CommandLine.Help.Ansi.AUTO.string(
                "@|cyan Commands:|@\n" +
                        "  --accept=ID    Accept a pending request\n" +
                        "  --reject=ID    Reject a pending request\n" +
                        "  --list         Show confirmed upcoming sessions"
        ));
    }

    /**
     * Displays the list of confirmed upcoming sessions
     */
    private void showConfirmedSessions(int tutorId) {
        List<TutorTutoringDto> sessions = tutorService.getUpcomingSessions(tutorId);

        if (sessions.isEmpty()) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|yellow No confirmed upcoming sessions.|@");
            System.out.println(msg);
            return;
        }

        System.out.println(CommandLine.Help.Ansi.AUTO.string("\n@|bold,cyan === Confirmed Upcoming Sessions ===|@\n"));

        for (int i = 0; i < sessions.size(); i++) {
            var s = sessions.get(i);

            String topicInfo = s.topicName() != null ? " - " + s.topicName() : "";

            System.out.println(CommandLine.Help.Ansi.AUTO.string(String.format(
                    "@|bold %d.|@ @|blue %s|@%s",
                    i + 1,
                    s.subjectName(),
                    topicInfo
            )));

            System.out.println(CommandLine.Help.Ansi.AUTO.string(String.format(
                    "    Student: @|green %s|@",
                    s.studentUsername()
            )));

            System.out.println(CommandLine.Help.Ansi.AUTO.string(String.format(
                    "    Date: @|yellow %s at %s|@",
                    s.meetingDate(),
                    s.meetingTime()
            )));

            System.out.println(CommandLine.Help.Ansi.AUTO.string(String.format(
                    "    Status: @|green %s|@ (ID: @|bold %d|@)",
                    s.status(),
                    s.tutoringId()
            )));

            System.out.println();
        }

        System.out.println(CommandLine.Help.Ansi.AUTO.string(
                "@|cyan Commands:|@\n" +
                        "  --cancel=ID                     Cancel a session\n" +
                        "  --complete=ID                   Mark as completed (after session)\n" +
                        "  --update=ID --new-date=... --new-time=...  Update date/time"
        ));
    }
}