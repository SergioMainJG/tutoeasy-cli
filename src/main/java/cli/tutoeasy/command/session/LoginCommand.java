package cli.tutoeasy.command.session;

import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.LoginRequestDto;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.service.AuthService;
import picocli.CommandLine.*;

/**
 * Represents the command for user authentication.
 * This class provides the functionality to log in a user with their email and password.
 * It is a subcommand of the main application and is accessible through the "login" command.
 *
 * @see AuthService
 * @see AuthSession
 */
@Command(
        name = "login",
        description = "Authenticate user",
        mixinStandardHelpOptions = true
)
public class LoginCommand implements Runnable {

    /**
     * The user's email address.
     */
    @Option(names = {"--email", "-e"}, required = true, description = "User email")
    private String email;

    /**
     * The user's password.
     */
    @Option(names = {"--password", "-p"}, required = true, interactive = true, description = "User password")
    private String password;

    /**
     * The service responsible for handling authentication-related business logic.
     */
    private final AuthService authService;

    /**
     * Constructs a new instance of the {@code LoginCommand}.
     *
     * @param authService The service that provides authentication functionalities.
     */
    public LoginCommand(AuthService authService) {
        this.authService = authService;
    }

    /**
     * The main entry point for the command execution. This method handles the logic
     * for authenticating the user, creating a session, and providing feedback to the user.
     */
    @Override
    public void run() {
        if (AuthSession.isLoggedIn()) {
            String currentUser = AuthSession.getCurrentUser().getUsername();
            String msg = Help.Ansi.AUTO.string(
                    "@|yellow You are already logged in as " + currentUser +
                            ". Use 'logout' to close the session first. |@"
            );
            System.out.println(msg);
            return;
        }
        try {
            LoginRequestDto req = new LoginRequestDto(email, password);
            var res = authService.login(req);

            if (res.userId() == -1) {
                String msg = Help.Ansi.AUTO.string("@|red Invalid email or password|@");
                System.out.println(msg);
                return;
            }

            User user = authService.getUserById(res.userId());
            if (user != null) {
                AuthSession.login(user);
                String msg = Help.Ansi.AUTO.string(
                        "@|green Welcome back, |@@|bold,blue " + res.username() + "|@@|green !|@\n" +
                                "@|cyan Role: " + user.getRol() + "|@"
                );
                System.out.println(msg);
            }

        } catch (Exception e) {
            String msg = Help.Ansi.AUTO.string("@|red ERROR: " + e.getMessage() + "|@");
            System.out.println(msg);
        }
    }
}
