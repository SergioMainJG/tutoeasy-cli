package cli.tutoeasy.command;

import cli.tutoeasy.util.AuthSession;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "tutoeasy",
        description = "CLI Application as TutoEasy Service",
        subcommands = {
                LoginCommand.class,
                TutorCommand.class,
                AdminCommand.class
        },
        mixinStandardHelpOptions = true
)
public class RootCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Use --help to see the commands.");
        var actionResponse = AuthSession.initialize();
        if (!actionResponse.success()) {
            String msg = CommandLine.Help.Ansi.AUTO.string("@|red" + actionResponse.message() + "|@");
            System.out.println(msg);
            System.exit(0);
        }
        String msg = CommandLine.Help.Ansi.AUTO.string("@|green " + actionResponse.message() + "|@");
        System.out.println(msg);
        System.exit(0);
    }
}
