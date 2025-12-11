package cli.tutoeasy.ui.menus;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.CreateStudentDto;
import cli.tutoeasy.model.dto.CreateTutorDto;
import cli.tutoeasy.model.dto.LoginRequestDto;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.service.AuthService;
import cli.tutoeasy.service.StudentService;
import cli.tutoeasy.service.TutorService;
import cli.tutoeasy.util.input.SecurePasswordReader;

import java.util.Scanner;

/**
 * <p>Main menu for unauthenticated users.</p>
 * <p>Allows login or registration for students and tutors.</p>
 */
public class MainMenu extends BaseMenu {

    /**
     * <p>Constructs a new MainMenu instance.</p>
     *
     * @param scanner The scanner used for user input.
     * @param factory The application factory to access services and repositories.
     */
    public MainMenu(Scanner scanner, AppFactory factory) {
        super(scanner, factory);
    }

    /**
     * <p>Displays the main menu and handles user interactions.</p>
     *
     * @return {@code true} if login was successful, {@code false} if the user chose to exit.
     */
    @Override
    public boolean show() {
        while (true) {
            displayHeader("MAIN MENU");

            System.out.println("1. Login");
            System.out.println("2. Register as Student");
            System.out.println("3. Register as Tutor");
            System.out.println("0. Exit");
            System.out.println();

            int option = readIntInput("Select an option: ", 0, 3);

            switch (option) {
                case 1:
                    if (handleLogin()) {
                        return true;
                    }
                    break;
                case 2:
                    handleStudentSignup();
                    break;
                case 3:
                    handleTutorSignup();
                    break;
                case 0:
                    return false;
            }
        }
    }

    /**
     * <p>Handles the user login process.</p>
     *
     * @return {@code true} if login was successful, {@code false} otherwise.
     */
    private boolean handleLogin() {
        displayHeader("LOGIN");

        String email = readStringInput("Email: ");
        if (email.isEmpty()) {
            showError("Email cannot be empty");
            waitForEnter();
            return false;
        }

        // Usar entrada segura de contraseña
        String password = SecurePasswordReader.readPassword("Password: ");
        if (password.isEmpty()) {
            showError("Password cannot be empty");
            waitForEnter();
            return false;
        }

        AuthService authService = new AuthService(factory.getUserRepository());
        LoginRequestDto loginDto = new LoginRequestDto(email, password);
        var response = authService.login(loginDto);

        if (response.userId() == -1) {
            showError("Incorrect email or password");
            waitForEnter();
            return false;
        }

        User user = authService.getUserById(response.userId());
        if (user != null) {
            AuthSession.login(user);
            showSuccess("Welcome back, " + response.username() + "!");
            showInfo("Role: " + user.getRol());
            waitForEnter();
            return true;
        }

        return false;
    }

    /**
     * <p>Handles the student registration process.</p>
     */
    private void handleStudentSignup() {
        displayHeader("STUDENT REGISTRATION");

        String name = readStringInput("Name (3-20 letters): ");
        String email = readStringInput("Email (format: first.last####@jala.university): ");

        String password = SecurePasswordReader.readPassword("Password (min 5 chars, letters, numbers, and symbols): ");

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("All fields are mandatory");
            waitForEnter();
            return;
        }

        StudentService studentService = new StudentService(factory.getUserRepository());
        CreateStudentDto dto = new CreateStudentDto(name, email, password);

        var response = studentService.createStudent(dto);

        if (response.success()) {
            showSuccess(response.message());
            showInfo("You can now login with your credentials");
        } else {
            showError(response.message());
        }

        waitForEnter();
    }

    /**
     * <p>Handles the tutor registration process.</p>
     */
    private void handleTutorSignup() {
        displayHeader("TUTOR REGISTRATION");

        String name = readStringInput("Name (3-20 letters): ");
        String email = readStringInput("Email (format: first.last####@jala.university): ");

        String password = SecurePasswordReader.readPassword("Password (min 5 chars, letters, numbers, and symbols): ");

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("All fields are mandatory");
            waitForEnter();
            return;
        }

        TutorService tutorService = factory.getTutorService();
        CreateTutorDto dto = new CreateTutorDto(name, email, password);

        var response = tutorService.createTutor(dto);

        if (response.success()) {
            showSuccess(response.message());
            showInfo("You can now login with your credentials");
        } else {
            showError(response.message());
        }

        waitForEnter();
    }
}
