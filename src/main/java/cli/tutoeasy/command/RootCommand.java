package cli.tutoeasy.command;

import cli.tutoeasy.command.admin.AdminCommand;
import cli.tutoeasy.command.session.LoginCommand;
import cli.tutoeasy.command.session.LogoutCommand;
import cli.tutoeasy.command.student.StudentCommand;
import cli.tutoeasy.command.tutor.TutorCommand;
import cli.tutoeasy.command.tutor.TutorRequestCommand;
import cli.tutoeasy.config.session.AuthSession;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * The root command of the TutoEasy CLI application.
 * This class serves as the entry point for all other commands and provides a brief description of the application.
 * It also handles the case where no subcommand is specified.
 */
@Command(
        name = "tutoeasy",
        description = "CLI Application as TutoEasy Service",
        subcommands = {
                LoginCommand.class,
                LogoutCommand.class,
                StudentCommand.class,
                TutorCommand.class,
                AdminCommand.class,
                TutorRequestCommand.class
        },
        mixinStandardHelpOptions = true
)
public class RootCommand implements Runnable {
    /**
     * The main entry point for the root command execution.
     * This method displays the current session status and provides gsuidance on how to proceed.
     */
    @Override
    public void run() {
        var sessionResult = AuthSession.initialize();
        if (sessionResult.success()) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|green " + sessionResult.message() + "|@");
            System.out.println(msg);
        } else {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|yellow No active session.|@");
            System.out.println(msg);
            System.out.println("Use 'login' command to authenticate.");
        }
        System.out.println("\nUse --help to see available commands.");
    }
}
