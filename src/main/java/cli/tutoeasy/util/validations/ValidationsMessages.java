package cli.tutoeasy.util.validations;

/**
 * Enum representing validation messages for various input fields.
 * This enum provides a centralized way to manage and access validation error messages.
 */
public enum ValidationsMessages {
    /**
     * Validation message for an invalid first name.
     */
    INVALID_NAME{
        @Override
        public String toString() {
            return "Invalid first name. It must be 3-20 letters";
        }
    },
    /**
     * Validation message for an invalid last name.
     */
    INVALID_LAST_NAME{
        @Override
        public String toString() {
            return "Invalid last name. It must be 3-20 letters";
        }
    },
    /**
     * Validation message for an invalid admin email.
     */
    INVALID_EMAIL_ADMIN{
        @Override
        public String toString() {
            return "Invalid email for admin. Must follow {name}.{lastname}@jala.university format";
        }
    },
    /**
     * Validation message for an invalid student or tutor email.
     */
    INVALID_EMAIL_STUDENT_TUTOR{
        @Override
        public String toString() {
            return "Invalid email for student. Must follow {name}.{lastname}{###}@jala.university format";
        }
    },
    /**
     * Validation message for an invalid password.
     */
    INVALID_PASSWORD{
        @Override
        public String toString() {
            return "Weak password. Must have letters, numbers, symbols and min 5 chars";
        }
    }
}
