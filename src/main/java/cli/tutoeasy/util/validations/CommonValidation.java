package cli.tutoeasy.util.validations;

/**
 * A utility class for common validation operations.
 * This class provides static methods for validating various input fields such as names, emails, and passwords.
 */
public class CommonValidation {
    /**
     * The regular expression for validating a name.
     * A valid name must be between 3 and 20 letters long and contain only alphabetic characters.
     */
    private static final String NAME_REGEX = "^[A-Za-z]{3,20}$";
    /**
     * The regular expression for validating an admin email.
     * A valid admin email must follow the format {name}.{lastname}@jala.university.
     */
    private static final String EMAIL_ADMIN_REGEX =
            "^[A-Za-z]+\\.[A-Za-z]+@jala\\.university$";
    /**
     * The regular expression for validating a student or tutor email.
     * A valid student or tutor email must follow the format {name}.{lastname}{####}@jala.university.
     */
    private static final String EMAIL_STUDENT_TUTOR_REGEX =
            "^[A-Za-z]+\\.[A-Za-z]+[0-9]{4}@jala\\.university$";
    /**
     * The regular expression for validating a password.
     * A valid password must have at least 5 characters, including letters, numbers, and symbols.
     */
    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{5,}$";

    /**
     * Validates a name.
     *
     * @param name The name to validate.
     * @return {@code true} if the name is valid, {@code false} otherwise.
     */
    public static boolean isValidName(String name) {
        if (name == null) return false;
        return name.matches(NAME_REGEX);
    }

    /**
     * Validates an admin email.
     *
     * @param email The email to validate.
     * @return {@code true} if the email is a valid admin email, {@code false} otherwise.
     */
    public static boolean isValidEmailFormatAdmin(String email) {
        if (email == null) return false;
        return email.matches(EMAIL_ADMIN_REGEX);
    }
    /**
     * Validates a student or tutor email.
     *
     * @param email The email to validate.
     * @return {@code true} if the email is a valid student or tutor email, {@code false} otherwise.
     */
    public static boolean isValidEmailFormatStudentTutor(String email) {
        if (email == null) return false;
        return email.matches(EMAIL_STUDENT_TUTOR_REGEX);
    }

    /**
     * Validates a password.
     *
     * @param password The password to validate.
     * @return {@code true} if the password is valid, {@code false} otherwise.
     */
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.matches(PASSWORD_REGEX);
    }
}
