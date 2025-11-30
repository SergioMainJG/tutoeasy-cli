package cli.tutoeasy.command.tutor;

import cli.tutoeasy.model.dto.TutorTutoringRequestDto;
import cli.tutoeasy.service.TutorService;
import cli.tutoeasy.config.session.AuthSession;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(
        name = "requests",
        description = "View, accept or reject tutoring requests (Tutor only)",
        mixinStandardHelpOptions = true
)
public class TutorRequestCommand implements Runnable {

    private final TutorService tutorService;

    public TutorRequestCommand(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @CommandLine.Option(names = {"--show", "-s"}, description = "Show all pending requests")
    private boolean showRequests;

    @CommandLine.Option(names = {"--accept", "-a"}, description = "Accept a tutoring request by ID")
    private Integer acceptId;

    @CommandLine.Option(names = {"--reject", "-r"}, description = "Reject a tutoring request by ID")
    private Integer rejectId;

    @Override
    public void run() {
        if (!AuthSession.isLoggedIn()) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|red ✗ You must be logged in to use this command.|@");
            System.out.println(msg);
            return;
        }

        if (!AuthSession.hasRole("tutor")) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|red ✗ Access denied. Only tutors can manage requests.|@");
            System.out.println(msg);
            return;
        }

        var user = AuthSession.getCurrentUser();
        int tutorId = user.getId();

        if (acceptId != null) {
            var res = tutorService.accept(tutorId, acceptId);
            if (res.success()) {
                String msg = CommandLine.Help.Ansi.AUTO.string("@|green ✓ " + res.message() + "|@");
                System.out.println(msg);
            } else {
                String msg = CommandLine.Help.Ansi.AUTO.string("@|red ✗ " + res.message() + "|@");
                System.out.println(msg);
            }
            return;
        }

        if (rejectId != null) {
            var res = tutorService.reject(tutorId, rejectId);
            if (res.success()) {
                String msg = CommandLine.Help.Ansi.AUTO.string("@|yellow ⚠ " + res.message() + "|@");
                System.out.println(msg);
            } else {
                String msg = CommandLine.Help.Ansi.AUTO.string("@|red ✗ " + res.message() + "|@");
                System.out.println(msg);
            }
            return;
        }

        if (showRequests || (acceptId == null && rejectId == null)) {
            showPendingRequests(tutorId);
        }
    }

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
            String msg = CommandLine.Help.Ansi.AUTO.string("@|red ✗ ERROR: " + e.getMessage() + "|@");
            System.out.println(msg);
        }
    }
}