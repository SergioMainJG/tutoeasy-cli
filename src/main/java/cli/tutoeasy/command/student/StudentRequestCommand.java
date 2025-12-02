package cli.tutoeasy.command.student;

import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.*;
import cli.tutoeasy.service.StudentTutoringService;
import picocli.CommandLine.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Command for students to manage tutoring requests.
 */
@Command(name = "request", description = "Manage tutoring requests (Student)", mixinStandardHelpOptions = true)
public class StudentRequestCommand implements Runnable {

  @Option(names = { "--subject", "-s" }, description = "Subject ID or name for new request")
  private String subject;

  @Option(names = { "--tutor", "-t" }, description = "Tutor username for new request")
  private String tutorUsername;

  @Option(names = { "--date", "-d" }, description = "Meeting date (YYYY-MM-DD)")
  private String dateStr;

  @Option(names = { "--time" }, description = "Meeting time (HH:MM in 24-hour format)")
  private String timeStr;

  @Option(names = { "--topic" }, description = "Topic name (optional)")
  private String topic;

  @Option(names = { "--show", "-l" }, description = "Show all upcoming tutorings")
  private boolean showRequests;

  @Option(names = { "--cancel", "-c" }, description = "Cancel a tutoring by ID")
  private Integer cancelId;

  @Option(names = { "--complete" }, description = "Mark tutoring as completed by ID")
  private Integer completeId;

  @Option(names = { "--update", "-u" }, description = "Update tutoring by ID")
  private Integer updateId;

  @Option(names = { "--new-date" }, description = "New date for update (YYYY-MM-DD)")
  private String newDateStr;

  @Option(names = { "--new-time" }, description = "New time for update (HH:MM)")
  private String newTimeStr;

  @Option(names = { "--new-topic" }, description = "New topic for update")
  private String newTopic;

  private final StudentTutoringService studentTutoringService;

  public StudentRequestCommand(StudentTutoringService studentTutoringService) {
    this.studentTutoringService = studentTutoringService;
  }

  @Override
  public void run() {
    if (!AuthSession.isLoggedIn()) {
      String msg = Help.Ansi.AUTO.string("@|red You must be logged in to use this command.|@");
      System.out.println(msg);
      return;
    }

    if (!AuthSession.hasRole("student")) {
      String msg = Help.Ansi.AUTO.string("@|red Access denied. Only students can manage tutoring requests.|@");
      System.out.println(msg);
      return;
    }

    int studentId = AuthSession.getCurrentUser().getId();

    try {
      if (subject != null && tutorUsername != null && dateStr != null && timeStr != null) {
        createRequest(studentId);
      } else if (cancelId != null) {
        cancelRequest(studentId);
      } else if (completeId != null) {
        completeRequest(studentId);
      } else if (updateId != null) {
        updateRequest(studentId);
      } else if (showRequests) {
        showUpcomingRequests(studentId);
      } else {
        showUpcomingRequests(studentId);
      }

    } catch (IllegalArgumentException e) {
      String msg = Help.Ansi.AUTO.string("@|red " + e.getMessage() + "|@");
      System.out.println(msg);
    } catch (Exception e) {
      String msg = Help.Ansi.AUTO.string("@|red ERROR: " + e.getMessage() + "|@");
      System.out.println(msg);
    }
  }

  /**
   * Creates a new tutoring request
   */
  private void createRequest(int studentId) {
    if (subject == null || tutorUsername == null || dateStr == null || timeStr == null) {
      String msg = Help.Ansi.AUTO.string(
          "@|yellow Missing required fields. Usage:\n" +
              "  request --subject=\"subject\" --tutor=\"username\" --date=\"YYYY-MM-DD\" --time=\"HH:MM\"|@");
      System.out.println(msg);
      return;
    }

    LocalDate date;
    LocalTime time;

    try {
      date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
    } catch (DateTimeParseException e) {
      String msg = Help.Ansi.AUTO.string("@|red Invalid date format. Use YYYY-MM-DD (e.g., 2025-12-25)|@");
      System.out.println(msg);
      return;
    }

    try {
      time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
    } catch (DateTimeParseException e) {
      String msg = Help.Ansi.AUTO.string("@|red Invalid time format. Use HH:MM in 24-hour format (e.g., 14:30)|@");
      System.out.println(msg);
      return;
    }

    CreateTutoringRequestDto dto = new CreateTutoringRequestDto(
        subject,
        tutorUsername,
        topic,
        date,
        time);

    var response = studentTutoringService.createTutoringRequest(studentId, dto);

    if (response.success()) {
      String msg = Help.Ansi.AUTO.string("@|green " + response.message() + "|@");
      System.out.println(msg);
    } else {
      String msg = Help.Ansi.AUTO.string("@|red " + response.message() + "|@");
      System.out.println(msg);
    }
  }

  /**
   * Shows all upcoming tutoring requests
   */
  private void showUpcomingRequests(int studentId) {
    List<StudentTutoringDto> requests = studentTutoringService.getUpcomingTutorings(studentId);

    if (requests.isEmpty()) {
      String msg = Help.Ansi.AUTO.string("@|yellow You have no upcoming tutoring sessions.|@");
      System.out.println(msg);
      return;
    }

    System.out.println(Help.Ansi.AUTO.string("\n@|bold,cyan === Your Upcoming Tutoring Sessions ===|@\n"));

    for (int i = 0; i < requests.size(); i++) {
      var req = requests.get(i);

      String statusColor = getStatusColor(req.status());
      String topicInfo = req.topicName() != null ? " - " + req.topicName() : "";

      System.out.println(Help.Ansi.AUTO.string(String.format(
          "@|bold %d.|@ @|blue %s|@%s",
          i + 1,
          req.subjectName(),
          topicInfo)));

      System.out.println(Help.Ansi.AUTO.string(String.format(
          "    Tutor: @|green %s|@",
          req.tutorUsername())));

      System.out.println(Help.Ansi.AUTO.string(String.format(
          "    Date: @|yellow %s at %s|@",
          req.meetingDate(),
          req.meetingTime())));

      System.out.println(Help.Ansi.AUTO.string(String.format(
          "    Status: @|%s %s|@ (ID: @|bold %d|@)",
          statusColor,
          req.status(),
          req.tutoringId())));

      System.out.println();
    }

    System.out.println(Help.Ansi.AUTO.string(
        "@|cyan Commands:|@\n" +
            "  --cancel=ID        Cancel a tutoring\n" +
            "  --complete=ID      Mark as completed (after session)\n" +
            "  --update=ID        Update date/time/topic"));
  }

  /**
   * Cancels a tutoring request
   */
  private void cancelRequest(int studentId) {
    var response = studentTutoringService.cancelTutoring(studentId, cancelId);

    if (response.success()) {
      String msg = Help.Ansi.AUTO.string("@|yellow " + response.message() + "|@");
      System.out.println(msg);
    } else {
      String msg = Help.Ansi.AUTO.string("@|red " + response.message() + "|@");
      System.out.println(msg);
    }
  }

  /**
   * Marks tutoring as completed
   */
  private void completeRequest(int studentId) {
    var response = studentTutoringService.completeTutoring(studentId, completeId);

    if (response.success()) {
      String msg = Help.Ansi.AUTO.string("@|green " + response.message() + "|@");
      System.out.println(msg);
    } else {
      String msg = Help.Ansi.AUTO.string("@|red " + response.message() + "|@");
      System.out.println(msg);
    }
  }

  /**
   * Updates a tutoring request
   */
  private void updateRequest(int studentId) {
    LocalDate newDate = null;
    LocalTime newTime = null;

    if (newDateStr != null) {
      try {
        newDate = LocalDate.parse(newDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
      } catch (DateTimeParseException e) {
        String msg = Help.Ansi.AUTO.string("@|red Invalid date format. Use YYYY-MM-DD|@");
        System.out.println(msg);
        return;
      }
    }

    if (newTimeStr != null) {
      try {
        newTime = LocalTime.parse(newTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
      } catch (DateTimeParseException e) {
        String msg = Help.Ansi.AUTO.string("@|red Invalid time format. Use HH:MM|@");
        System.out.println(msg);
        return;
      }
    }

    UpdateTutoringRequestDto dto = new UpdateTutoringRequestDto(
        updateId,
        newTopic,
        newDate,
        newTime);

    var response = studentTutoringService.updateTutoring(studentId, dto);

    if (response.success()) {
      String msg = Help.Ansi.AUTO.string("@|green " + response.message() + "|@");
      System.out.println(msg);
    } else {
      String msg = Help.Ansi.AUTO.string("@|red " + response.message() + "|@");
      System.out.println(msg);
    }
  }

  /**
   * Gets color for status display
   */
  private String getStatusColor(String status) {
    return switch (status.toLowerCase()) {
      case "confirmed" -> "green";
      case "unconfirmed" -> "yellow";
      case "canceled" -> "red";
      case "completed" -> "cyan";
      default -> "white";
    };
  }
}