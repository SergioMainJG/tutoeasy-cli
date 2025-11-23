package cli.tutoeasy;

import cli.tutoeasy.command.LoginCommand;
import cli.tutoeasy.command.RootCommand;
import cli.tutoeasy.repository.UserRepository;
import cli.tutoeasy.service.AuthService;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {

        System.setProperty("java.util.logging.config.file",
                ClassLoader.getSystemResource("logging.properties").getPath());

        UserRepository userRepo = new UserRepository();
       AuthService authService = new AuthService(userRepo);

       new CommandLine(new RootCommand())
               .addSubcommand("Login", new LoginCommand(authService))
               .execute(args);
    }
}
