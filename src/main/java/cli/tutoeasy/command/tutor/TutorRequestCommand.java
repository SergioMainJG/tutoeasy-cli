package cli.tutoeasy.command.tutor;

import cli.tutoeasy.model.dto.TutorTutoringRequestDto;
import cli.tutoeasy.service.TutorService;
import cli.tutoeasy.config.session.AuthSession;
import picocli.CommandLine;

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
 * @see TutorService
 * @see AuthSession
 * @see TutorTutoringRequestDto
 * @version 1.0
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

        if (acceptId != null) {
            var res = tutorService.accept(tutorId, acceptId);
            if (res.success()) {
                String msg = CommandLine.Help.Ansi.AUTO.string("@|green " + res.message() + "|@");
                System.out.println(msg);
            } else {
                String msg = CommandLine.Help.Ansi.AUTO.string("@|red " + res.message() + "|@");
                System.out.println(msg);
            }
            return;
        }

        if (rejectId != null) {
            var res = tutorService.reject(tutorId, rejectId);
            if (res.success()) {
                String msg = CommandLine.Help.Ansi.AUTO.string("@|yellow " + res.message() + "|@");
                System.out.println(msg);
            } else {
                String msg = CommandLine.Help.Ansi.AUTO.string("@|red " + res.message() + "|@");
                System.out.println(msg);
            }
            return;
        }

        if (showRequests || (acceptId == null && rejectId == null)) {
            showPendingRequests(tutorId);
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
        try {
            List<TutorTutoringRequestDto> list = tutorService.getPending(tutorId);

            if (list.isEmpty()) {
                String msg = CommandLine.Help.Ansi.AUTO.string("@|yellow No pending requests.|@");
                System.out.println(msg);
                return;
            }

            System.out.println(CommandLine.Help.Ansi.AUTO.string("\n@|bold,cyan Pending Tutoring Requests |@\n"));

            String marketMessage = CommandLine.Help.Ansi.AUTO.string("Subjct: @|bold %d.|@ @|blue %s|@ — Student: @|green %s|@ — @|yellow Scheduled %s at %s|@ (ID: @|bold %d|@)\n");

            for (int i = 0; i < list.size(); i++) {
                var r = list.get(i);
                var msg = String.format(
                        marketMessage,
                        i + 1,
                        r.subjectName(),
                        r.studentName(),
                        r.meetingDate(),
                        r.meetingTime(),
                        r.tutoringId()
                );
                System.out.println( msg );
            }

            System.out.println(CommandLine.Help.Ansi.AUTO.string(
                    "\n@|cyan Use --accept=ID or --reject=ID to manage requests|@"
            ));

        } catch (Exception e) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|red ERROR: " + e.getMessage() + "|@");
            System.out.println(msg);
        }
    }
}