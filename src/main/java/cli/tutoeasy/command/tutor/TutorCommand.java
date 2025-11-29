package cli.tutoeasy.command.tutor;

import cli.tutoeasy.model.dto.CreateTutorDto;
import cli.tutoeasy.service.TutorService;
import cli.tutoeasy.util.validations.CommonValidation;
import cli.tutoeasy.util.validations.ValidationsMessages;
import picocli.CommandLine.*;

/**
 * Represents the command for creating a new tutor.
 * This class provides the functionality to register a new tutor by providing their name, email, and password.
 * It is a subcommand of the main application and is accessible through the "create-tutor" command.
 *
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
     * The command specification, used to access command-line properties.
     */
    @Spec
    Model.CommandSpec spec;

    /**
     * The name of the tutor.
     */
    @Option(names = {"--name", "-n"}, required = true)
    private String name;

    /**
     * The email of the tutor.
     */
    @Option(names = {"--email", "-e"}, required = true)
    private String email;

    /**
     * The password of the tutor.
     */
    @Option(names = {"--password", "-p"}, required = true, interactive = true)
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
     * The main entry point for the command execution. This method handles the logic
     * for creating a new tutor, including input validation and calling the {@link TutorService}.
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