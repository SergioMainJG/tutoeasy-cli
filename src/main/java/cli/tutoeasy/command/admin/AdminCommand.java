package cli.tutoeasy.command.admin;

import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.CreateAdministratorDto;
import cli.tutoeasy.service.AdministratorService;
import cli.tutoeasy.util.validations.CommonValidation;
import cli.tutoeasy.util.validations.ValidationsMessages;
import picocli.CommandLine.*;

/**
 * <p>
 * Represents the command for creating a new administrator.
 * This class provides the functionality to register a new administrator by providing their name, last name, email, and password.
 * </p>
 *
 * <p>
 * It is a subcommand of the main application and is accessible through the "create-admin" command.
 * Access to this command is restricted to existing administrators.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see AdministratorService
 * @see CreateAdministratorDto
 */
@Command(
        name = "create-admin",
        description = "Register a new administrator",
        mixinStandardHelpOptions = true
)
public class AdminCommand implements Runnable {

    /**
     * The command specification, used to access command-line properties and the
     * command line instance itself for error reporting.
     */
    @Spec
    Model.CommandSpec spec;

    /**
     * The first name of the new administrator.
     * This option is mandatory.
     */
    @Option(names = {"--name", "-n"}, required = true, description = "The first name of the administrator")
    private String name;

    /**
     * The last name of the new administrator.
     * This option is mandatory.
     */
    @Option(names = {"--lastname", "-l"}, required = true, description = "The last name of the administrator")
    private String lastName;

    /**
     * The email address of the new administrator.
     * This option is mandatory and must follow a valid email format.
     */
    @Option(names = {"--email", "-e"}, required = true, description = "The email address of the administrator")
    private String email;

    /**
     * The password for the new administrator account.
     * This option is mandatory and interactive, meaning the user will be prompted
     * to enter it securely if not provided directly.
     */
    @Option(names = {"--password", "-p"}, required = true, interactive = true, description = "The password for the administrator")
    private String password;

    /**
     * The service responsible for handling administrator-related business logic.
     */
    private final AdministratorService adminService;

    /**
     * Constructs a new instance of the {@code AdminCommand}.
     *
     * @param adminService The service that provides administrator-related functionalities.
     */
    public AdminCommand(AdministratorService adminService) {
        this.adminService = adminService;
    }


    /**
     * The main entry point for the command execution.
     *
     * <p>
     * This method handles the logic for creating a new administrator. It performs the following steps:
     * </p>
     * <ul>
     *     <li>Checks if the user is logged in.</li>
     *     <li>Verifies if the logged-in user has the 'admin' role.</li>
     *     <li>Validates the input parameters (name, last name, email, password).</li>
     *     <li>Calls the {@link AdministratorService} to create the administrator.</li>
     *     <li>Displays the result (success or error message) to the console.</li>
     * </ul>
     */
    @Override
    public void run() {

        if (!AuthSession.isLoggedIn()) {
            String msg = Help.Ansi.AUTO.string("@|red You must be logged in to use this command.|@");
            System.out.println(msg);
            return;
        }

        if (!AuthSession.hasRole("admin")) {
            String msg = Help.Ansi.AUTO.string("@|red Access denied. Only administrators can create new admins.|@");
            System.out.println(msg);
            return;
        }

        try {
            CreateAdministratorDto dto =
                    new CreateAdministratorDto(name, lastName, email, password);

            if (!CommonValidation.isValidName(name)) {
                throw new ParameterException(
                        spec.commandLine(),
                        ValidationsMessages.INVALID_NAME.toString() + ": " + name);
            }
            if (!CommonValidation.isValidName(lastName)) {
                throw new ParameterException(
                        spec.commandLine(),
                        ValidationsMessages.INVALID_LAST_NAME.toString() + ": " + lastName);
            }
            if (!CommonValidation.isValidEmailFormatAdmin(email)) {
                throw new ParameterException(
                        spec.commandLine(),
                        ValidationsMessages.INVALID_EMAIL_ADMIN.toString() + ": " + email);
            }
            if (!CommonValidation.isValidPassword(password)) {
                throw new ParameterException(
                        spec.commandLine(),
                        ValidationsMessages.INVALID_PASSWORD.toString() + ": " + password);
            }

            var response = adminService.createAdministrator(dto);

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