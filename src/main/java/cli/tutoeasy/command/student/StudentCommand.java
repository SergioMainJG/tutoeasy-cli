package cli.tutoeasy.command.student;

import cli.tutoeasy.model.dto.CreateStudentDto;
import cli.tutoeasy.service.StudentService;
import cli.tutoeasy.util.validations.CommonValidation;
import cli.tutoeasy.util.validations.ValidationsMessages;
import picocli.CommandLine.*;

/**
 * Represents the command for creating a new student.
 * This class provides the functionality to register a new student by providing their name, email, and password.
 * It is a subcommand of the main application and is accessible through the "create-student" command.
 *
 * @see StudentService
 * @see CreateStudentDto
 */
@Command(
        name = "create-student",
        description = "Register a new student",
        mixinStandardHelpOptions = true
)
public class StudentCommand implements Runnable {

    /**
     * The command specification, used to access command-line properties.
     */
    @Spec
    Model.CommandSpec  spec;

    /**
     * The name of the student.
     */
    @Option(names = {"--name", "-n"}, required = true)
    private String name;

    /**
     * The email of the student.
     */
    @Option(names = {"--email", "-e"}, required = true)
    private String email;

    /**
     * The password of the student.
     */
    @Option(names = {"--password", "-p"}, required = true, interactive = true)
    private String password;

    /**
     * The service responsible for handling student-related business logic.
     */
    private final StudentService studentService;

    /**
     * Constructs a new instance of the {@code StudentCommand}.
     *
     * @param studentService The service that provides student-related functionalities.
     */
    public StudentCommand(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * The main entry point for the command execution. This method handles the logic
     * for creating a new student, including input validation and calling the {@link StudentService}.
     */
    @Override
    public void run() {

        try {
            CreateStudentDto dto = new CreateStudentDto(name, email, password);

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

            var response = studentService.createStudent(dto);
            String msg;
            if (response.success()) {
                msg = Help.Ansi.AUTO.string("@|green " + response.message() + "|@");
            } else {
                msg = Help.Ansi.AUTO.string("@|red " + response.message() + "|@");
            }
            System.out.println(msg);
        } catch (Exception e) {
            String msg = Help.Ansi.AUTO.string("@|red ERROR: " + e.getMessage() + "|@");
            System.out.println(msg);
        }
    }
}