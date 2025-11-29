
package cli.tutoeasy;

import cli.tutoeasy.command.*;
import cli.tutoeasy.config.hibernate.JPAUtil;
import cli.tutoeasy.config.session.AuthSession;
import picocli.CommandLine;

/**
 * The main entry point for the TutoEasy CLI application.
 * This class is responsible for initializing the application, setting up the command-line parser,
 * and executing the commands entered by the user.
 *
 * @see RootCommand
 * @see AppFactory
 * @see AuthSession
 * @see JPAUtil
 */
public class Main {
    /**
     * The main method that starts the CLI application.
     *
     * @param args The command-line arguments passed to the application.
     */
    public static void main(String[] args) {

        System.setProperty("java.util.logging.config.file",
                ClassLoader.getSystemResource("logging.properties").getPath());

        JPAUtil.getEntityManager();
        var sessionResult = AuthSession.initialize();

        if (args.length == 0) {
            if (sessionResult.success()) {
                String msg = CommandLine.Help.Ansi.AUTO.string(
                        "@|green " + sessionResult.message() + "|@"
                );
                System.out.println(msg);
            } else {
                String msg = CommandLine.Help.Ansi.AUTO.string(
                        "@|yellow No active session. Use 'Login' to authenticate.|@"
                );
                System.out.println(msg);
                System.out.println("Use --help to see available commands.");
            }
            System.exit(0);
            return;
        }

        AppFactory factory = new AppFactory();

        CommandLine cmd = new CommandLine(new RootCommand(), factory);

        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }
}
