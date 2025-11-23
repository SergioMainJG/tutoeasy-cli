package cli.tutoeasy.command;

import picocli.CommandLine.Command;

@Command(
        name = "tutoeasy",
        description = "CLI para la plataforma TutoEasy",
        mixinStandardHelpOptions = true
)
public class RootCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Use --help para ver los comandos disponibles.");
    }
}
