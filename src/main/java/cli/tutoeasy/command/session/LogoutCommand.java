package cli.tutoeasy.command.session;

import cli.tutoeasy.config.session.AuthSession;
import picocli.CommandLine.*;

/**
 * Represents the command for logging out of the application.
 * This class provides the functionality to close the current user session.
 * It is a subcommand of the main application and is accessible through the "logout" command.
 *
 * @see AuthSession
 */
@Command(
        name = "logout",
        description = "Close current session",
        mixinStandardHelpOptions = true
)
public class LogoutCommand implements Runnable {

    /**
     * The main entry point for the command execution. This method handles the logic
     * for logging out the current user and closing the session.
     */
    @Override
    public void run() {
        if (!AuthSession.isLoggedIn()) {
            String msg = Help.Ansi.AUTO.string("@|yellow No active session to close.|@");
            System.out.println(msg);
            return;
        }

        String username = AuthSession.getCurrentUser().getUsername();
        AuthSession.logout();

        String msg = Help.Ansi.AUTO.string(
                "@|green Goodbye, " + username + "! Session closed successfully.|@"
        );
        System.out.println(msg);
    }
}