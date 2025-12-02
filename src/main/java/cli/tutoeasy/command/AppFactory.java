package cli.tutoeasy.command;

import cli.tutoeasy.command.admin.AdminCommand;
import cli.tutoeasy.command.global.ContactCommand;
import cli.tutoeasy.command.global.MessageCommand;
import cli.tutoeasy.command.global.NotificationCommand;
import cli.tutoeasy.command.session.LoginCommand;
import cli.tutoeasy.command.student.StudentCommand;
import cli.tutoeasy.command.student.StudentRequestCommand;
import cli.tutoeasy.command.tutor.TutorCommand;
import cli.tutoeasy.command.tutor.TutorRequestCommand;
import cli.tutoeasy.repository.*;
import cli.tutoeasy.service.*;
import picocli.CommandLine;

/**
 * A factory for creating instances of commands and their dependencies.
 * This class is responsible for instantiating and wiring together the various
 * services and repositories required by the application's commands.
 * It implements the {@link CommandLine.IFactory} interface to integrate with
 * the picocli framework.
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
     * The repository for managing contact data.
     */
    private final ContactRepository contactRepository;
    /**
     * The repository for managing message data.
     */
    private final MessageRepository messageRepository;
    /**
     * The repository for managing notification data.
     */
    private final NotificationRepository notificationRepository;
    /**
     * The repository for managing subject data.
     */
    private final SubjectRepository subjectRepository;
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
     * The service for contact-related operations.
     */
    private final ContactService contactService;
    /**
     * The service for message-related operations.
     */
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final StudentTutoringService studentTutoringService;

    /**
     * Constructs a new instance of the {@code AppFactory}.
     * This constructor initializes all the repositories and services required by
     * the application.
     */
    public AppFactory() {
        this.userRepository = new UserRepository();
        this.tutorRepository = new TutorRepository();
        this.tutoringRepository = new TutoringRepository();
        this.contactRepository = new ContactRepository();
        this.messageRepository = new MessageRepository();
        this.notificationRepository = new NotificationRepository();
        this.subjectRepository = new SubjectRepository();
        this.authService = new AuthService(userRepository);
        this.studentService = new StudentService(userRepository);
        this.tutorService = new TutorService(userRepository, tutorRepository, tutoringRepository, notificationRepository);
        this.adminService = new AdministratorService(userRepository);
        this.contactService = new ContactService(contactRepository, tutoringRepository);
        this.messageService = new MessageService(messageRepository, contactRepository, notificationRepository);
        this.notificationService = new NotificationService(notificationRepository, userRepository);
        // this.subjectService = new SubjectService(subjectRepository);
        this.studentTutoringService = new StudentTutoringService(tutoringRepository, userRepository, subjectRepository, contactRepository, notificationService);
    }

    /**
     * Creates an instance of the specified class.
     * This method is called by picocli to instantiate commands.
     * It uses dependency injection to provide the necessary services to the
     * commands.
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

        if (cls == StudentCommand.class) {
            return (K) new StudentCommand(studentService);
        }

        if (cls == ContactCommand.class) {
            return (K) new ContactCommand(contactService);
        }

        if (cls == MessageCommand.class) {
            return (K) new MessageCommand(messageService);
        }

        if (cls == NotificationCommand.class) {
            return (K) new NotificationCommand(notificationService);
        }

        if (cls == StudentRequestCommand.class ){
            return (K) new StudentRequestCommand(studentTutoringService);
        }

        return cls.getDeclaredConstructor().newInstance();
    }
}