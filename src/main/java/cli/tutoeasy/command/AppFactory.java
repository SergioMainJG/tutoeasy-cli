package cli.tutoeasy.command;

import cli.tutoeasy.command.admin.AdminCommand;
import cli.tutoeasy.command.session.LoginCommand;
import cli.tutoeasy.command.student.StudentCommand;
import cli.tutoeasy.command.tutor.TutorCommand;
import cli.tutoeasy.command.tutor.TutorRequestCommand;
import cli.tutoeasy.repository.TutorRepository;
import cli.tutoeasy.repository.TutoringRepository;
import cli.tutoeasy.repository.UserRepository;
import cli.tutoeasy.service.*;
import picocli.CommandLine;

/**
 * A factory for creating instances of commands and their dependencies.
 * This class is responsible for instantiating and wiring together the various
 * services and repositories required by the application's commands.
 * It implements the {@link CommandLine.IFactory} interface to integrate with the picocli framework.
 *
 * @see CommandLine.IFactory
 */
public class AppFactory implements CommandLine.IFactory {

    /**
     * The repository for managing user data.
     */
    private final UserRepository userRepository;
    /**
     * The repository for managing tutor data.
     */
    private final TutorRepository tutorRepository;
    /**
     * The repository for managing tutoring data.
     */
    private final TutoringRepository tutoringRepository;
    /**
     * The service for user-related operations.
     */
    private final UserService userService;
    /**
     * The service for student-related operations.
     */
    private final StudentService studentService;
    /**
     * The service for administrator-related operations.
     */
    private final AdministratorService adminService;
    /**
     * The service for tutor-related operations.
     */
    private final TutorService tutorService;
    /**
     * The service for authentication-related operations.
     */
    private final AuthService authService;

    /**
     * Constructs a new instance of the {@code AppFactory}.
     * This constructor initializes all the repositories and services required by the application.
     */
    public AppFactory() {

        this.userRepository = new UserRepository();
        this.tutorRepository = new TutorRepository();
        this.tutoringRepository = new TutoringRepository();
        this.userService = new UserService(userRepository);
        this.authService = new AuthService(userRepository);
        this.studentService = new StudentService(userRepository);
        this.tutorService = new TutorService(userRepository, tutorRepository, tutoringRepository);
        this.adminService = new AdministratorService(userRepository);

    }

    /**
     * Creates an instance of the specified class.
     * This method is called by picocli to instantiate commands.
     * It uses dependency injection to provide the necessary services to the commands.
     *
     * @param cls The class to instantiate.
     * @param <K> The type of the class to instantiate.
     * @return A new instance of the specified class.
     * @throws Exception If an error occurs while creating the instance.
     */
    @Override
    public <K> K create(Class<K> cls) throws Exception {

        if (cls == LoginCommand.class) {
            return (K) new LoginCommand(authService);
        }

        if (cls == AdminCommand.class) {
            return (K) new AdminCommand(adminService);
        }

        if (cls == TutorCommand.class) {
            return (K) new TutorCommand(tutorService);
        }

        if (cls == TutorRequestCommand.class) {
            return (K) new TutorRequestCommand(tutorService);
        }

        if(cls == StudentCommand.class ){
            return (K) new StudentCommand(studentService);
        }

        return cls.getDeclaredConstructor().newInstance();
    }
}