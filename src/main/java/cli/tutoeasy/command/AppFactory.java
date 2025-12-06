package cli.tutoeasy.command;

import cli.tutoeasy.command.global.ContactCommand;
import cli.tutoeasy.command.global.MessageCommand;
import cli.tutoeasy.command.global.NotificationCommand;
import cli.tutoeasy.command.global.ProfileCommand; 
import cli.tutoeasy.command.session.LoginCommand;

import cli.tutoeasy.command.admin.AdminCommand;
import cli.tutoeasy.command.admin.ReportCommand; 

import cli.tutoeasy.command.student.StudentCommand;
import cli.tutoeasy.command.student.StudentHistoryCommand;
import cli.tutoeasy.command.student.StudentRequestCommand;

import cli.tutoeasy.command.tutor.EditTutorProfileCommand; 
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

    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final TutoringRepository tutoringRepository;
    private final ContactRepository contactRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final CareerRepository careerRepository;          
    private final ReportRepository reportRepository;          
    private final TutorExpertiseRepository expertiseRepository;
    private final TutorScheduleRepository scheduleRepository;  

    private final StudentService studentService;
    private final AdministratorService adminService;
    private final TutorService tutorService;
    private final AuthService authService;
    private final ContactService contactService;
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final StudentTutoringService studentTutoringService;
    private final ProfileService profileService;               
    private final ReportService reportService;                 

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
        this.topicRepository = new TopicRepository();
        this.careerRepository = new CareerRepository();             
        this.reportRepository = new ReportRepository();             
        this.expertiseRepository = new TutorExpertiseRepository();  
        this.scheduleRepository = new TutorScheduleRepository();    

        this.authService = new AuthService(userRepository);
        this.studentService = new StudentService(userRepository);
        this.adminService = new AdministratorService(userRepository);
        this.notificationService = new NotificationService(notificationRepository, userRepository);
        
        this.contactService = new ContactService(contactRepository, tutoringRepository);
        this.messageService = new MessageService(messageRepository, contactRepository, notificationRepository);
        
        this.reportService = new ReportService(reportRepository, userRepository); 
        this.profileService = new ProfileService(userRepository, careerRepository); 

        this.tutorService = new TutorService(
            userRepository, 
            tutorRepository, 
            tutoringRepository, 
            notificationService,
            subjectRepository,
            expertiseRepository,
            scheduleRepository
        );
        
        this.studentTutoringService = new StudentTutoringService(
            tutoringRepository, 
            userRepository, 
            subjectRepository, 
            contactRepository, 
            notificationService, 
            topicRepository
        );
    }

    /**
     * Creates an instance of the specified class, providing dependencies.
     * @param cls The class to instantiate.
     * @return A new instance of the specified class.
     */
    @Override
    public <K> K create(Class<K> cls) throws Exception {

        if (cls == LoginCommand.class) {
            return (K) new LoginCommand(authService);
        }

        if (cls == AdminCommand.class) {
            return (K) new AdminCommand(adminService);
        }
        
        if (cls == ReportCommand.class) { 
            return (K) new ReportCommand(reportService);
        }

        if (cls == TutorCommand.class) {
            return (K) new TutorCommand(tutorService);
        }
        
        if (cls == EditTutorProfileCommand.class){ 
             return (K) new EditTutorProfileCommand(tutorService);
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

        if (cls == ProfileCommand.class) { 
            return (K) new ProfileCommand(profileService);
        }

        return cls.getDeclaredConstructor().newInstance();
    }
}