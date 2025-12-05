package cli.tutoeasy.command.global;

import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.ContactInfoDto;
import cli.tutoeasy.service.ContactService;
import picocli.CommandLine.*;

/**
 * <p>
 * Command for retrieving contact information.
 * Allows users to get contact details by username or from a tutoring session.
 * </p>
 *
 * <p>
 * This command is useful for students and tutors to find contact details of each other
 * to facilitate communication outside the platform if necessary.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see ContactService
 * @see ContactInfoDto
 */
@Command(name = "contact", description = "Get contact information of users", mixinStandardHelpOptions = true)
public class ContactCommand implements Runnable {

  /**
   * Option to get contact info by providing a specific username.
   */
  @Option(names = { "--info-of", "-u" }, description = "Get contact info by username", paramLabel = "<username>")
  private String username;

  /**
   * Option to get contact info related to a specific tutoring session.
   * If the user is a student, it returns the tutor's contact info.
   * If the user is a tutor, it returns the student's contact info.
   */
  @Option(names = { "--info-from",
      "-t" }, description = "Get contact info from a tutoring session (returns tutor if you're student, or student if you're tutor)", paramLabel = "<tutoring-id>")
  private Integer tutoringId;

  /**
   * The service responsible for handling contact-related operations.
   */
  private final ContactService contactService;

  /**
   * Constructs a new instance of the {@code ContactCommand}.
   *
   * @param contactService The service that provides contact-related functionalities.
   */
  public ContactCommand(ContactService contactService) {
    this.contactService = contactService;
  }

  /**
   * The main entry point for the command execution.
   *
   * <p>
   * This method handles the logic for retrieving contact information.
   * It checks for login status, validates that only one option is selected,
   * and then calls the {@link ContactService} to fetch the data.
   * Finally, it displays the contact information or an error message.
   * </p>
   */
  @Override
  public void run() {
    if (!AuthSession.isLoggedIn()) {
      String msg = Help.Ansi.AUTO.string("@|red You must be logged in to use this command.|@");
      System.out.println(msg);
      return;
    }

    if (username == null && tutoringId == null) {
      String msg = Help.Ansi.AUTO.string(
          "@|yellow Please specify either --info-of=<username> or --info-from=<tutoring-id>|@");
      System.out.println(msg);
      System.out.println("Use --help for more information.");
      return;
    }

    if (username != null && tutoringId != null) {
      String msg = Help.Ansi.AUTO.string(
          "@|yellow Please use only one option at a time.|@");
      System.out.println(msg);
      return;
    }

    try {
      ContactInfoDto contactInfo;

      if (username != null) {
        contactInfo = contactService.getContactInfoByUsername(username);

        if (contactInfo == null) {
          String msg = Help.Ansi.AUTO.string(
              "@|red User not found: " + username + "|@");
          System.out.println(msg);
          return;
        }
      } else {
        int requestorId = AuthSession.getCurrentUser().getId();
        contactInfo = contactService.getContactInfoFromTutoring(tutoringId, requestorId);
      }

      displayContactInfo(contactInfo);

    } catch (IllegalArgumentException e) {
      String msg = Help.Ansi.AUTO.string("@|red " + e.getMessage() + "|@");
      System.out.println(msg);
    } catch (Exception e) {
      String msg = Help.Ansi.AUTO.string("@|red ERROR: " + e.getMessage() + "|@");
      System.out.println(msg);
    }
  }

  /**
   * Displays contact information with formatted output.
   *
   * <p>
   * Prints the user's ID, name, email, role, and career (if available)
   * using ANSI colors for better readability.
   * </p>
   *
   * @param contact The {@link ContactInfoDto} object containing the contact details.
   */
  private void displayContactInfo(ContactInfoDto contact) {
    System.out.println(Help.Ansi.AUTO.string("\n@|bold,cyan === Contact Information ===|@\n"));

    System.out.println(Help.Ansi.AUTO.string("@|bold ID:|@ " + contact.userId()));
    System.out.println(Help.Ansi.AUTO.string("@|bold Name:|@ @|green " + contact.username() + "|@"));
    System.out.println(Help.Ansi.AUTO.string("@|bold Email:|@ @|blue " + contact.email() + "|@"));
    System.out.println(Help.Ansi.AUTO.string("@|bold Role:|@ @|yellow " + contact.role() + "|@"));

    String career = contact.careerName() != null ? contact.careerName() : "Not specified";
    System.out.println(Help.Ansi.AUTO.string("@|bold Career:|@ " + career));

    System.out.println();
  }
}