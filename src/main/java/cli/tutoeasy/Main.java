package cli.tutoeasy;

import cli.tutoeasy.command.AdminCommand;
import cli.tutoeasy.command.LoginCommand;
import cli.tutoeasy.command.RootCommand;
import cli.tutoeasy.command.StudentCommand;
import cli.tutoeasy.command.TutorCommand;
import cli.tutoeasy.repository.UserRepository;
import cli.tutoeasy.service.AdministratorService;
import cli.tutoeasy.service.AuthService;
import cli.tutoeasy.service.StudentService;
import cli.tutoeasy.service.TutorService;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {

        System.setProperty("java.util.logging.config.file",
                ClassLoader.getSystemResource("logging.properties").getPath());

        UserRepository userRepo = new UserRepository();
        AuthService authService = new AuthService(userRepo);
        StudentService studentService = new StudentService(userRepo);
        TutorService tutorService = new TutorService(userRepo);
        AdministratorService adminService = new AdministratorService(userRepo);

        new CommandLine(new RootCommand())
                .addSubcommand("Login", new LoginCommand(authService))
                .addSubcommand("Create-student", new StudentCommand(studentService))
                .addSubcommand("Create-tutor", new TutorCommand(tutorService))
                .addSubcommand("Create-admin", new AdminCommand(adminService))
                .execute(args);
    }
}
