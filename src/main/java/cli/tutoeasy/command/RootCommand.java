package cli.tutoeasy.command;

import cli.tutoeasy.command.admin.AdminCommand;
import cli.tutoeasy.command.admin.ReportCommand;
import cli.tutoeasy.command.global.ContactCommand;
import cli.tutoeasy.command.global.MessageCommand;
import cli.tutoeasy.command.global.NotificationCommand;
import cli.tutoeasy.command.global.ProfileCommand;
import cli.tutoeasy.command.session.LoginCommand;
import cli.tutoeasy.command.session.LogoutCommand;
import cli.tutoeasy.command.student.StudentCommand;
import cli.tutoeasy.command.student.StudentHistoryCommand;
import cli.tutoeasy.command.student.StudentRequestCommand;
import cli.tutoeasy.command.tutor.EditTutorProfileCommand;
import cli.tutoeasy.command.tutor.TutorCommand;
import cli.tutoeasy.command.tutor.TutorRequestCommand;
import cli.tutoeasy.config.session.AuthSession;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * <p>
 * The root command of the TutoEasy CLI application.
 * This class serves as the entry point for all other commands and provides a
 * brief description of the application.
 * </p>
 *
 * <p>
 * It also handles the case where no subcommand is specified, displaying the
 * current session status and guidance on how to proceed.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Command(name = "tutoeasy", description = "CLI Application as TutoEasy Service", subcommands = {
        LoginCommand.class,
        LogoutCommand.class,
        StudentCommand.class,
        TutorCommand.class,
        AdminCommand.class,
        TutorRequestCommand.class,
        ContactCommand.class,
        MessageCommand.class,
        StudentRequestCommand.class,
        NotificationCommand.class,
        ReportCommand.class,
        ProfileCommand.class,
        StudentHistoryCommand.class,
        EditTutorProfileCommand.class
}, mixinStandardHelpOptions = true)
public class RootCommand implements Runnable {
    /**
     * <p>
     * The main entry point for the root command execution.
     * </p>
     * <p>
     * This method displays the current session status and provides guidance on how
     * to proceed.
     * </p>
     *
     * <p>
     * It checks if a user is logged in and displays a welcome message or a prompt
     * to login accordingly.
     * </p>
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
