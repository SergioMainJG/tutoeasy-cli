package cli.tutoeasy.command.student;

import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.CreateSessionFeedbackDto;
import cli.tutoeasy.service.SessionFeedbackService;
import picocli.CommandLine.*;

import java.time.LocalDateTime;

/**
 * CLI command that allows a student to rate and leave feedback
 * for a completed tutoring session.
 *
 * <p>
 * This command can only be executed by authenticated users
 * with the {@code student} role. It validates that:
 * </p>
 * <ul>
 *   <li>The student is logged in</li>
 *   <li>The student owns the tutoring session</li>
 *   <li>The tutoring session has been completed</li>
 *   <li>No duplicate feedback is submitted</li>
 * </ul>
 *
 * <p>
 * The command delegates the business logic to {@link SessionFeedbackService}.
 * </p>
 *
 * @see SessionFeedbackService
 * @version 1.0
 * @since 1.0
 */
@Command(
        name = "tutor-feedback",
        description = "Rate a completed tutoring session (students only)",
        mixinStandardHelpOptions = true
)
public class RateSessionCommand implements Runnable {

    /**
     * Service responsible for handling feedback business logic.
     */
    private final SessionFeedbackService feedbackService;

    /**
     * Identifier of the tutoring session to be rated.
     */
    @Option(names = "--tutoring-id", required = true,
            description = "ID of the tutoring session to evaluate")
    private int tutoringId;

    /**
     * Rating given by the student (e.g., 1 to 5).
     */
    @Option(names = "--rating", required = true,
            description = "Rating given to the tutoring session")
    private int rating;

    /**
     * Comment or review provided by the student.
     */
    @Option(names = "--comment", required = true,
            description = "Textual feedback about the tutoring session")
    private String comment;

    /**
     * Constructor with dependency injection.
     *
     * @param feedbackService Service that manages session feedback operations
     */
    public RateSessionCommand(SessionFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * Executes the command.
     *
     * <p>
     * Validates authentication and authorization, then delegates
     * feedback creation to the {@link SessionFeedbackService}.
     * </p>
     */
    @Override
    public void run() {

        if (!AuthSession.isLoggedIn()) {
            System.out.println("You must log in to rate a tutoring session.");
            return;
        }

        if (!AuthSession.hasRole("student")) {
            System.out.println("Only students can submit feedback.");
            return;
        }

        int studentId = AuthSession.getCurrentUser().getId();

        try {
            CreateSessionFeedbackDto dto = new CreateSessionFeedbackDto(
                    tutoringId,
                    studentId,
                    rating,
                    comment,
                    LocalDateTime.now()
            );

            feedbackService.addFeedback(dto);
            System.out.println("Feedback submitted successfully.");

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Cannot submit feedback: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
}
