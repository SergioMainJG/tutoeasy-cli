package cli.tutoeasy.command.tutor;

import cli.tutoeasy.model.dto.CreateTutorDto;
import cli.tutoeasy.service.TutorService;
import cli.tutoeasy.util.validations.CommonValidation;
import cli.tutoeasy.util.validations.ValidationsMessages;
import picocli.CommandLine.*;

/**
 * <p>
 * Represents the command for creating a new tutor.
 * This class provides the functionality to register a new tutor by providing their name, email, and password.
 * </p>
 *
 * <p>
 * It is a subcommand of the main application and is accessible through the "create-tutor" command.
 * Unlike the admin creation command, this command might be accessible publicly or have different restrictions
 * depending on the application configuration.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see TutorService
 * @see CreateTutorDto
 */
@Command(
        name = "create-tutor",
        description = "Register a new tutor",
        mixinStandardHelpOptions = true
)
public class TutorCommand implements Runnable {

    /**
     * The command specification, used to access command-line properties and the
     * command line instance itself for error reporting.
     */
    @Spec
    Model.CommandSpec spec;

    /**
     * The full name of the new tutor.
     * This option is mandatory.
     */
    @Option(names = {"--name", "-n"}, required = true, description = "The full name of the tutor")
    private String name;

    /**
     * The email address of the new tutor.
     * This option is mandatory and must follow a valid email format for students/tutors.
     */
    @Option(names = {"--email", "-e"}, required = true, description = "The email address of the tutor")
    private String email;

    /**
     * The password for the new tutor account.
     * This option is mandatory and interactive, prompting the user for secure input.
     */
    @Option(names = {"--password", "-p"}, required = true, interactive = true, description = "The password for the tutor")
    private String password;

    /**
     * The service responsible for handling tutor-related business logic.
     */
    private final TutorService tutorService;

    /**
     * Constructs a new instance of the {@code TutorCommand}.
     *
     * @param tutorService The service that provides tutor-related functionalities.
     */
    public TutorCommand(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    /**
     * The main entry point for the command execution.
     *
     * <p>
     * This method handles the logic for creating a new tutor. It performs the following steps:
     * </p>
     * <ul>
     *     <li>Validates the input parameters (name, email, password) using {@link CommonValidation}.</li>
     *     <li>Calls the {@link TutorService} to register the new tutor.</li>
     *     <li>Displays the result (success or error message) to the console.</li>
     * </ul>
     *
     * <p>
     * If any validation fails, a {@link ParameterException} is thrown and caught to display
     * a user-friendly error message.
     * </p>
     */
    @Override
    public void run() {
        try {
            CreateTutorDto dto = new CreateTutorDto(name, email, password);
            if (!CommonValidation.isValidName(name)) {
                throw new ParameterException(
                        spec.commandLine(),
                        ValidationsMessages.INVALID_NAME.toString() + ": " + name);
            }

            if (!CommonValidation.isValidEmailFormatStudentTutor(email)) {
                throw new ParameterException(
                        spec.commandLine(),
                        ValidationsMessages.INVALID_EMAIL_STUDENT_TUTOR.toString() + ": " + email);
            }
            if (!CommonValidation.isValidPassword(password)) {
                throw new ParameterException(
                        spec.commandLine(),
                        ValidationsMessages.INVALID_PASSWORD.toString() + ": " + password);
            }

            var response = tutorService.createTutor(dto);

            String msg;
            if (response.success()) {
                msg = Help.Ansi.AUTO.string("@|green " + response.message() + "|@");
            } else {
                msg = Help.Ansi.AUTO.string("@|red " + response.message() + "|@");
            }
            System.out.println(msg);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}