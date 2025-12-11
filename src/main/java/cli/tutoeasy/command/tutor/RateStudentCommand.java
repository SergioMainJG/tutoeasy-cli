package cli.tutoeasy.command.tutor;

import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.CreateTutorFeedbackDto;
import cli.tutoeasy.service.SessionFeedbackService;
import picocli.CommandLine.*;

import java.time.LocalDateTime;

/**
 * CLI command that allows a tutor to evaluate a student
 * after a completed tutoring session.
 *
 * <p>
 * This command can only be executed by authenticated users
 * with the {@code tutor} role. It validates that:
 * </p>
 * <ul>
 *   <li>The tutor is logged in</li>
 *   <li>The tutor conducted the tutoring session</li>
 *   <li>The tutoring session has been completed</li>
 *   <li>No duplicate feedback from the tutor exists for the same session</li>
 * </ul>
 *
 * <p>
 * Feedback is persisted through {@link SessionFeedbackService}.
 * </p>
 *
 * @see SessionFeedbackService
 * @version 1.0
 * @since 1.0
 */
@Command(
        name = "rate-student",
        description = "Rate a student after a completed tutoring session (tutor only)",
        mixinStandardHelpOptions = true
)
public class RateStudentCommand implements Runnable {

    /**
     * Service responsible for managing session feedback.
     */
    private final SessionFeedbackService feedbackService;

    /**
     * ID of the tutoring session being evaluated.
     */
    @Option(names = "--tutoring-id", required = true,
            description = "ID of the completed tutoring session")
    private int tutoringId;

    /**
     * Rating given to the student (1–5).
     */
    @Option(names = "--rating", required = true,
            description = "Numeric rating for the student")
    private int rating;

    /**
     * Comment or review provided by the tutor.
     */
    @Option(names = "--comment", required = true,
            description = "Textual evaluation comment")
    private String comment;

    /**
     * Constructor with dependency injection.
     *
     * @param feedbackService Service that manages session feedback
     */
    public RateStudentCommand(SessionFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * Executes the tutor evaluation command.
     *
     * <p>
     * Validates authentication and authorization, then delegates
     * feedback creation to {@link SessionFeedbackService}.
     * </p>
     */
    @Override
    public void run() {

        if (!AuthSession.isLoggedIn() || !AuthSession.hasRole("tutor")) {
            System.out.println("Only tutors can submit student evaluations.");
            return;
        }

        int tutorId = AuthSession.getCurrentUser().getId();

        try {
            CreateTutorFeedbackDto dto = new CreateTutorFeedbackDto(
                    tutoringId,
                    tutorId,
                    rating,
                    comment,
                    LocalDateTime.now()
            );

            feedbackService.addTutorFeedback(dto);
            System.out.println("Student evaluation registered successfully.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
}
