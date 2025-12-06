package cli.tutoeasy.command.global;

import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.UpdateProfileDto;
import cli.tutoeasy.service.ProfileService;
import cli.tutoeasy.util.validations.CommonValidation;
import cli.tutoeasy.util.validations.ValidationsMessages;
import picocli.CommandLine.*;

/**
 * <p>
 * Command for updating user profile information.
 * </p>
 * <p>
 * Allows users to update their username, email, password, career, and description.
 * </p>
 *
 * @since 1.0
 */
@Command(
        name = "custom-profile",
        description = "Update your profile information",
        mixinStandardHelpOptions = true
)
public class ProfileCommand implements Runnable {

    /**
     * The command specification.
     */
    @Spec
    Model.CommandSpec spec;

    /**
     * New username.
     */
    @Option(names = {"--username", "-u"}, description = "New username")
    private String username;

    /**
     * New email.
     */
    @Option(names = {"--email", "-e"}, description = "New email")
    private String email;

    /**
     * Current password for verification.
     */
    @Option(names = {"--password", "-p"}, interactive = true, description = "Current password (required for password change)")
    private String currentPassword;

    /**
     * New password.
     */
    @Option(names = {"--new-password", "-n"}, interactive = true, description = "New password")
    private String newPassword;

    /**
     * Career name.
     */
    @Option(names = {"--career", "-c"}, description = "Career name")
    private String career;

    /**
     * Additional description to append.
     */
    @Option(names = {"--add-description", "-a"}, description = "Append text to existing description")
    private String addDescription;

    /**
     * New description to replace existing.
     */
    @Option(names = {"--new-description", "-d"}, description = "Replace description with new text")
    private String newDescription;

    /**
     * The service responsible for handling profile operations.
     */
    private final ProfileService profileService;

    /**
     * Constructs a new instance of the {@code ProfileCommand}.
     *
     * @param profileService The service that provides profile-related functionalities.
     */
    public ProfileCommand(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * <p>
     * The main entry point for the command execution.
     * </p>
     */
    @Override
    public void run() {
        if (!AuthSession.isLoggedIn()) {
            String msg = Help.Ansi.AUTO.string("@|red You must be logged in to use this command.|@");
            System.out.println(msg);
            return;
        }

        if (addDescription != null && newDescription != null) {
            String msg = Help.Ansi.AUTO.string(
                    "@|yellow Cannot use --add-description and --new-description together.|@");
            System.out.println(msg);
            return;
        }

        if (newPassword != null && currentPassword == null) {
            String msg = Help.Ansi.AUTO.string(
                    "@|yellow Password verification required. Use --password to provide current password.|@");
            System.out.println(msg);
            return;
        }

        try {
            var currentUser = AuthSession.getCurrentUser();
            String userRole = currentUser.getRol().name();

            if (username != null && !CommonValidation.isValidName(username)) {
                throw new ParameterException(
                        spec.commandLine(),
                        ValidationsMessages.INVALID_NAME.toString() + ": " + username);
            }

            if (email != null) {
                if (userRole.equals("admin")) {
                    if (!CommonValidation.isValidEmailFormatAdmin(email)) {
                        throw new ParameterException(
                                spec.commandLine(),
                                ValidationsMessages.INVALID_EMAIL_ADMIN.toString() + ": " + email);
                    }
                } else {
                    if (!CommonValidation.isValidEmailFormatStudentTutor(email)) {
                        throw new ParameterException(
                                spec.commandLine(),
                                ValidationsMessages.INVALID_EMAIL_STUDENT_TUTOR.toString() + ": " + email);
                    }
                }
            }

            if (newPassword != null && !CommonValidation.isValidPassword(newPassword)) {
                throw new ParameterException(
                        spec.commandLine(),
                        ValidationsMessages.INVALID_PASSWORD.toString());
            }

            UpdateProfileDto dto = new UpdateProfileDto(
                    username,
                    email,
                    currentPassword,
                    newPassword,
                    career,
                    addDescription,
                    newDescription
            );

            if (!dto.hasUpdates()) {
                String msg = Help.Ansi.AUTO.string(
                        "@|yellow No updates specified. Use --help to see available options.|@");
                System.out.println(msg);
                return;
            }

            var response = profileService.updateProfile(currentUser.getId(), dto);

            String msg;
            if (response.success()) {
                msg = Help.Ansi.AUTO.string("@|green " + response.message() + "|@");

                if (username != null || email != null) {
                    var updatedUser = profileService.getUserById(currentUser.getId());
                    if (updatedUser != null) {
                        AuthSession.login(updatedUser);
                    }
                }
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