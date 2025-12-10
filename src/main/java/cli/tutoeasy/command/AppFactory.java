package cli.tutoeasy.command;

import cli.tutoeasy.command.admin.AdminCommand;
import cli.tutoeasy.command.admin.ReportCommand;
import cli.tutoeasy.command.global.ContactCommand;
import cli.tutoeasy.command.global.MessageCommand;
import cli.tutoeasy.command.global.NotificationCommand;
import cli.tutoeasy.command.global.ProfileCommand;
import cli.tutoeasy.command.session.LoginCommand;
import cli.tutoeasy.command.student.StudentCommand;
import cli.tutoeasy.command.student.RateSessionCommand;
import cli.tutoeasy.command.student.StudentHistoryCommand;
import cli.tutoeasy.command.student.StudentRequestCommand;
import cli.tutoeasy.command.tutor.EditTutorProfileCommand;
import cli.tutoeasy.command.tutor.RateStudentCommand;
import cli.tutoeasy.command.tutor.TutorCommand;
import cli.tutoeasy.command.tutor.TutorRequestCommand;
import cli.tutoeasy.repository.*;
import cli.tutoeasy.service.*;
import picocli.CommandLine;

/**
 * <p>
 * A factory for creating instances of commands and their dependencies.
 * This class is responsible for instantiating and wiring together the various
 * services and repositories required by the application's commands.
 * </p>
 *
 * <p>
 * It implements the {@link CommandLine.IFactory} interface to integrate with
 * the picocli framework, allowing for dependency injection into command classes.
 * </p>
 *
 * @version 1.0
 * @see CommandLine.IFactory
 * @since 1.0
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
     * The repository for managing topic data.
     */
    private final TopicRepository topicRepository;
    /**
     * The repository for managing career data.
     */
    private final CareerRepository careerRepository;
    /**
     * The repository for managing repors
     */
    private final ReportRepository reportRepository;
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
    /**
     * The service for notification-related operations.
     */
    private final NotificationService notificationService;
    /**
     * The service for student tutoring-related operations.
     */
    private final StudentTutoringService studentTutoringService;
    /**
     * The service for profile-related operations.
     */
    private final ProfileService profileService;
    /**
     * The service for student reportService
     */
    private final ReportService reportService;

    /**
     * The repository for managing tutors' subjects (expertise) data.
     */
    private final TutorExpertiseRepository expertiseRepository;

    /**
     * The repository for managing tutors' schedule data.
     */
    private final TutorScheduleRepository scheduleRepository;

    /**
     * Repository responsible for managing tutoring session feedback persistence.
     */
    private final SessionFeedbackRepository feedbackRepository;

    /**
     * Service responsible for handling business logic related to
     * tutoring session feedback.
     */
    private final SessionFeedbackService feedbackService;

    /**
     * <p>
     * Constructs a new instance of the {@code AppFactory}.
     * </p>
     * <p>
     * This constructor initializes all the repositories and services required by
     * the application.
     * </p>
     */
    public AppFactory() {
        this.userRepository = new UserRepository();
        this.tutorRepository = new TutorRepository();
        this.tutoringRepository = new TutoringRepository();
        this.contactRepository = new ContactRepository();
        this.messageRepository = new MessageRepository();
        this.notificationRepository = new NotificationRepository();
        this.subjectRepository = new SubjectRepository();
        this.topicRepository = new TopicRepository();
        this.careerRepository = new CareerRepository();
        this.reportRepository = new ReportRepository();
        this.authService = new AuthService(userRepository);
        this.studentService = new StudentService(userRepository);
        this.adminService = new AdministratorService(userRepository);
        this.contactService = new ContactService(contactRepository, tutoringRepository);
        this.messageService = new MessageService(messageRepository, contactRepository, notificationRepository);
        this.notificationService = new NotificationService(notificationRepository, userRepository);
        this.expertiseRepository = new TutorExpertiseRepository();
        this.scheduleRepository = new TutorScheduleRepository();
        this.tutorService = new TutorService(userRepository, tutorRepository, tutoringRepository, notificationService,subjectRepository,expertiseRepository, scheduleRepository);
        this.studentTutoringService = new StudentTutoringService(tutoringRepository, userRepository, subjectRepository, contactRepository, notificationService, topicRepository);
        this.profileService = new ProfileService(userRepository, careerRepository);
        this.reportService = new ReportService(reportRepository, userRepository);
        this.feedbackRepository = new SessionFeedbackRepository();
        this.feedbackService = new SessionFeedbackService( feedbackRepository, tutoringRepository);
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

        if (cls == StudentRequestCommand.class) {
            return (K) new StudentRequestCommand(studentTutoringService);
        }

        if (cls == StudentHistoryCommand.class) {
            return (K) new StudentHistoryCommand(studentTutoringService);
        }
        if (cls == EditTutorProfileCommand.class){
            return (K) new EditTutorProfileCommand(tutorService);
        }
        if (cls == ReportCommand.class) {
            return (K) new ReportCommand(reportService);
        }
        if (cls == ProfileCommand.class) {
            return (K) new ProfileCommand(profileService);
        }
        if (cls == RateStudentCommand.class) {
            return (K) new RateStudentCommand(feedbackService);
        }
        if (cls == RateSessionCommand.class) {
            return (K) new RateSessionCommand(feedbackService);
        }
        return cls.getDeclaredConstructor().newInstance();
    }
}