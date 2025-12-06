package cli.tutoeasy.command.admin;

import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.CreateReportDto;
import cli.tutoeasy.model.dto.ReportDto;
import cli.tutoeasy.service.ReportService;
import picocli.CommandLine.*;

import java.util.List;

/**
 * <p>
 * Command for managing administrative reports in the TutoEasy system.
 * This command provides functionality for generating, viewing, and deleting various types of reports
 * including tutoring sessions, tutors, and students statistics.
 * </p>
 *
 * <p>
 * The command is restricted to users with administrative privileges only. It supports three main operations:
 * </p>
 * <ul>
 *     <li><strong>--show</strong>: Display existing reports with optional filtering by type</li>
 *     <li><strong>--generate</strong>: Create new reports with specified content and format</li>
 *     <li><strong>--delete</strong>: Remove reports by their unique identifier</li>
 * </ul>
 *
 * <p>
 * Report generation supports multiple output formats including plain text (.txt), Markdown (.md),
 * and Microsoft Word documents (.docx). Generated reports are automatically stored in the database
 * and can optionally be exported to files.
 * </p>
 *
 * <h3>Usage Examples:</h3>
 * <pre>
 * # Show all reports
 * report --show
 *
 * # Show reports filtered by type
 * report --show --type="tutoring_sessions"
 *
 * # Generate a report with preview
 * report --generate --type="tutors" --content="Monthly tutor statistics"
 *
 * # Generate and export to Word document
 * report --generate --type="students" --content="Student enrollment report" --file="docx"
 *
 * # Delete a report
 * report --delete=5
 * </pre>
 *
 * <h3>Report Types:</h3>
 * <ul>
 *     <li><code>tutoring_sessions</code> - Statistics and information about tutoring sessions</li>
 *     <li><code>tutors</code> - Information about registered tutors and their activities</li>
 *     <li><code>students</code> - Information about registered students and their participation</li>
 * </ul>
 *
 * @version 1.0
 * @since 1.0
 * @see ReportService
 * @see CreateReportDto
 * @see ReportDto
 * @see AuthSession
 */
@Command(
        name = "report",
        description = "Manage administrative reports (Admin only)",
        mixinStandardHelpOptions = true
)
public class ReportCommand implements Runnable {

    /**
     * The command specification, used to access command-line properties and the
     * command line instance itself for error reporting and validation.
     */
    @Spec
    Model.CommandSpec spec;

    /**
     * Flag to display existing reports.
     * When set, the command will retrieve and display reports from the database.
     * Can be combined with {@link #type} to filter results.
     */
    @Option(names = {"--show", "-s"}, description = "Show existing reports")
    private boolean showReports;

    /**
     * Flag to generate a new report.
     * When set, requires {@link #type} and {@link #content} to be specified.
     * Can optionally use {@link #fileFormat} to export the report.
     */
    @Option(names = {"--generate", "-g"}, description = "Generate a new report")
    private boolean generateReport;

    /**
     * ID of the report to delete.
     * When specified, the command will attempt to delete the report with this ID
     * from the database.
     */
    @Option(names = {"--delete", "-d"}, description = "Delete a report by ID")
    private Integer deleteId;

    /**
     * The type of report to generate or filter by.
     *
     * <p>Valid values are:</p>
     * <ul>
     *     <li><code>tutoring_sessions</code> - Reports about tutoring sessions</li>
     *     <li><code>tutors</code> - Reports about tutors</li>
     *     <li><code>students</code> - Reports about students</li>
     * </ul>
     *
     * <p>This field is required when using {@link #generateReport} and optional
     * when using {@link #showReports} for filtering.</p>
     */
    @Option(names = {"--type", "-t"},
            description = "Report type: tutoring_sessions, tutors, students")
    private String type;

    /**
     * The content or description of the report to generate.
     * This field is required when using {@link #generateReport}.
     *
     * <p>The content should provide a clear description of what the report contains
     * or summarizes. This text will be stored in the database and displayed when
     * viewing the report.</p>
     */
    @Option(names = {"--content", "-c"},
            description = "Report content/description (required for --generate)")
    private String content;

    /**
     * The file format for exporting the generated report.
     *
     * <p>Supported formats:</p>
     * <ul>
     *     <li><code>txt</code> or <code>text</code> - Plain text file</li>
     *     <li><code>md</code> or <code>markdown</code> - Markdown formatted file</li>
     *     <li><code>docx</code> or <code>word</code> - Microsoft Word document</li>
     * </ul>
     *
     * <p>If not specified, the report will only be stored in the database and
     * displayed as a console preview.</p>
     */
    @Option(names = {"--file", "-f"},
            description = "File format for export: txt/text, md/markdown, docx/word")
    private String fileFormat;

    /**
     * Optional limit on the number of reports to display when using {@link #showReports}.
     * If not specified, all reports (or all filtered reports) will be displayed.
     */
    @Option(names = {"--limit", "-l"},
            description = "Limit number of reports to show")
    private Integer limit;

    /**
     * Flag to automatically open the generated file with the system's default application.
     * When set to true, after generating and exporting a report to a file, the system
     * will attempt to open it using {@link java.awt.Desktop}.
     *
     * <p>
     * This feature requires:
     * </p>
     * <ul>
     *     <li>A graphical environment (not available on headless servers)</li>
     *     <li>The {@link #fileFormat} option to be specified</li>
     *     <li>Desktop support on the operating system</li>
     * </ul>
     *
     * <p>
     * If the system doesn't support desktop operations or the file cannot be opened,
     * an informative message will be displayed without causing the command to fail.
     * </p>
     */
    @Option(names = {"--open", "-o"},
            description = "Automatically open the generated file (requires --file)")
    private boolean openFile;

    /**
     * The service responsible for handling report-related business logic.
     * This service manages report creation, retrieval, deletion, and file generation.
     */
    private final ReportService reportService;

    /**
     * Constructs a new instance of {@code ReportCommand}.
     *
     * @param reportService The service that provides report management functionalities.
     *                      Must not be null.
     */
    public ReportCommand(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * The main entry point for the command execution.
     *
     * <p>
     * This method is called by the picocli framework when the command is invoked.
     * It performs the following operations:
     * </p>
     * <ol>
     *     <li>Validates that the user is logged in</li>
     *     <li>Verifies that the user has administrator privileges</li>
     *     <li>Determines which operation to perform based on flags</li>
     *     <li>Delegates to the appropriate handler method</li>
     *     <li>Handles any errors and displays appropriate messages</li>
     * </ol>
     *
     * <p>
     * If no operation flags are specified, the command defaults to showing existing reports.
     * </p>
     *
     * @throws IllegalArgumentException if invalid parameters are provided
     * @throws RuntimeException if an unexpected error occurs during execution
     */
    @Override
    public void run() {
        if (!AuthSession.isLoggedIn()) {
            String msg = Help.Ansi.AUTO.string("@|red You must be logged in to use this command.|@");
            System.out.println(msg);
            return;
        }

        if (!AuthSession.hasRole("admin")) {
            String msg = Help.Ansi.AUTO.string(
                    "@|red Access denied. Only administrators can manage reports.|@");
            System.out.println(msg);
            return;
        }

        try {
            if (deleteId != null) {
                deleteReport();
            } else if (generateReport) {
                createReport();
            } else {
                displayReports();
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
     * Handles the report generation operation.
     *
     * <p>
     * This method performs the following steps:
     * </p>
     * <ol>
     *     <li>Validates that required fields (type and content) are provided</li>
     *     <li>Validates that the report type is one of the allowed values</li>
     *     <li>Creates a DTO with the report information</li>
     *     <li>Calls the service to generate and store the report</li>
     *     <li>Displays a preview of the generated report</li>
     *     <li>Optionally exports the report to a file if format is specified</li>
     * </ol>
     *
     * <p>
     * The report is always stored in the database regardless of file export settings.
     * The administrator's username is automatically associated with the report.
     * </p>
     *
     * @throws ParameterException if required parameters are missing or invalid
     */
    private void createReport() {
        if (type == null || type.trim().isEmpty()) {
            throw new ParameterException(
                    spec.commandLine(),
                    "Report type is required. Use --type with: tutoring_sessions, tutors, or students");
        }

        if (content == null || content.trim().isEmpty()) {
            throw new ParameterException(
                    spec.commandLine(),
                    "Report content is required. Use --content to describe the report");
        }

        String normalizedType = normalizeReportType(type);
        if (!isValidReportType(normalizedType)) {
            throw new ParameterException(
                    spec.commandLine(),
                    "Invalid report type. Valid types are: tutoring_sessions, tutors, students");
        }

        int adminId = AuthSession.getCurrentUser().getId();
        CreateReportDto dto = new CreateReportDto(normalizedType, content.trim());

        var response = reportService.createReport(adminId, dto);

        if (response.success()) {
            String msg = Help.Ansi.AUTO.string("@|green " + response.message() + "|@");
            System.out.println(msg);

            if (fileFormat != null && !fileFormat.trim().isEmpty()) {
                exportReport(response.message(), normalizedType);
            }
        } else {
            String msg = Help.Ansi.AUTO.string("@|red " + response.message() + "|@");
            System.out.println(msg);
        }
    }

    /**
     * Handles the report display operation.
     *
     * <p>
     * This method retrieves reports from the database and displays them in a
     * formatted table-like structure. Reports can be filtered by type and limited
     * in quantity.
     * </p>
     *
     * <p>
     * Each report entry displays:
     * </p>
     * <ul>
     *     <li>Report ID</li>
     *     <li>Report type</li>
     *     <li>Administrator who created it</li>
     *     <li>Creation timestamp</li>
     *     <li>Content/description</li>
     * </ul>
     *
     * <p>
     * If no reports are found matching the criteria, an informative message is displayed.
     * </p>
     */
    private void displayReports() {
        List<ReportDto> reports;

        if (type != null && !type.trim().isEmpty()) {
            String normalizedType = normalizeReportType(type);
            if (!isValidReportType(normalizedType)) {
                throw new IllegalArgumentException(
                        "Invalid report type. Valid types are: tutoring_sessions, tutors, students");
            }
            reports = reportService.getReportsByType(normalizedType, limit);
        } else {
            reports = reportService.getAllReports(limit);
        }

        if (reports.isEmpty()) {
            String msg = Help.Ansi.AUTO.string("@|yellow No reports found.|@");
            System.out.println(msg);
            return;
        }

        System.out.println(Help.Ansi.AUTO.string("\n@|bold,cyan === Administrative Reports ===|@\n"));

        if (type != null) {
            System.out.println(Help.Ansi.AUTO.string(
                    "@|cyan Filtered by type: " + normalizeReportType(type) + "|@\n"));
        }

        for (int i = 0; i < reports.size(); i++) {
            ReportDto report = reports.get(i);
            displayReportEntry(i + 1, report);
        }

        System.out.println(Help.Ansi.AUTO.string(
                String.format("@|cyan Total: %d report(s)|@", reports.size())));

        System.out.println(Help.Ansi.AUTO.string(
                "\n@|cyan Commands:|@\n" +
                        "  --generate --type=TYPE --content=\"...\"  Create new report\n" +
                        "  --show --type=TYPE                       Filter by type\n" +
                        "  --delete=ID                              Delete report\n" +
                        "  --generate ... --file=FORMAT             Export to file (txt/md/docx)"));
    }

    /**
     * Displays a single report entry in a formatted manner.
     *
     * <p>
     * This helper method formats and displays the details of a single report,
     * including color-coded output for better readability.
     * </p>
     *
     * @param index The sequential number of this report in the list (for display purposes)
     * @param report The report data transfer object containing the report information
     */
    private void displayReportEntry(int index, ReportDto report) {
        String typeColor = getTypeColor(report.reportType());

        System.out.println(Help.Ansi.AUTO.string(String.format(
                "@|bold %d.|@ @|%s %s|@ @|faint (ID: %d)|@",
                index,
                typeColor,
                report.reportType(),
                report.id())));

        System.out.println(Help.Ansi.AUTO.string(String.format(
                "    Created by: @|green %s|@",
                report.adminUsername())));

        System.out.println(Help.Ansi.AUTO.string(String.format(
                "    Date: @|yellow %s|@",
                report.createdAt().toString().substring(0, 19))));

        String contentPreview = report.content().length() > 100
                ? report.content().substring(0, 97) + "..."
                : report.content();
        System.out.println(Help.Ansi.AUTO.string(String.format(
                "    Content: @|white %s|@",
                contentPreview)));

        System.out.println();
    }

    /**
     * Handles the report deletion operation.
     *
     * <p>
     * This method attempts to delete a report from the database using its unique ID.
     * Only the administrator who created the report or a super administrator can
     * delete reports.
     * </p>
     *
     * <p>
     * After successful deletion, a confirmation message is displayed.
     * If the report is not found or cannot be deleted, an error message is shown.
     * </p>
     */
    private void deleteReport() {
        int adminId = AuthSession.getCurrentUser().getId();
        var response = reportService.deleteReport(deleteId, adminId);

        if (response.success()) {
            String msg = Help.Ansi.AUTO.string("@|green " + response.message() + "|@");
            System.out.println(msg);
        } else {
            String msg = Help.Ansi.AUTO.string("@|red " + response.message() + "|@");
            System.out.println(msg);
        }
    }

    /**
     * Exports the generated report to a file in the specified format.
     *
     * <p>
     * This method handles the file export process after a report has been successfully
     * generated and stored in the database. It supports multiple file formats:
     * </p>
     * <ul>
     *     <li>Plain text (.txt)</li>
     *     <li>Markdown (.md)</li>
     *     <li>Microsoft Word (.docx)</li>
     * </ul>
     *
     * <p>
     * The file is created in the current working directory with a timestamp-based
     * filename to avoid conflicts.
     * </p>
     *
     * @param reportSummary A summary message about the report creation
     * @param reportType The type of report being exported
     */
    private void exportReport(String reportSummary, String reportType) {
        String format = normalizeFileFormat(fileFormat);

        var exportResponse = reportService.exportReportToFile(
                AuthSession.getCurrentUser().getId(),
                reportType,
                content,
                format,
                true);

        if (exportResponse.success()) {
            String msg = Help.Ansi.AUTO.string("@|green " + exportResponse.message() + "|@");
            System.out.println(msg);
        } else {
            String msg = Help.Ansi.AUTO.string("@|yellow " + exportResponse.message() + "|@");
            System.out.println(msg);
        }
    }

    /**
     * Normalizes the report type string to a standard format.
     *
     * <p>
     * This method converts various input formats to the standard database format.
     * For example, "Tutoring Sessions", "tutoring-sessions", "TUTORING_SESSIONS"
     * all become "tutoring_sessions".
     * </p>
     *
     * @param type The report type string to normalize
     * @return The normalized report type string in lowercase with underscores
     */
    private String normalizeReportType(String type) {
        if (type == null) return null;
        return type.toLowerCase()
                .trim()
                .replace(" ", "_")
                .replace("-", "_");
    }

    /**
     * Validates if a report type is one of the allowed values.
     *
     * <p>Valid report types are:</p>
     * <ul>
     *     <li>tutoring_sessions</li>
     *     <li>tutors</li>
     *     <li>students</li>
     * </ul>
     *
     * @param type The report type to validate (should be normalized first)
     * @return {@code true} if the type is valid, {@code false} otherwise
     */
    private boolean isValidReportType(String type) {
        return type != null && (
                type.equals("tutoring_sessions") ||
                        type.equals("tutors") ||
                        type.equals("students"));
    }

    /**
     * Normalizes the file format string to a standard format.
     *
     * <p>
     * This method converts various input formats to standard file extensions.
     * For example, "Word", "DOCX", "word" all become "docx".
     * </p>
     *
     * @param format The file format string to normalize
     * @return The normalized file format string (txt, md, or docx)
     */
    private String normalizeFileFormat(String format) {
        if (format == null) return "txt";

        String normalized = format.toLowerCase().trim();

        return switch (normalized) {
            case "text", "txt" -> "txt";
            case "markdown", "md" -> "md";
            case "word", "docx" -> "docx";
            default -> "txt";
        };
    }

    /**
     * Returns the ANSI color code for a report type for display purposes.
     *
     * <p>
     * This method provides color-coding for different report types to improve
     * visual distinction in the console output:
     * </p>
     * <ul>
     *     <li>tutoring_sessions - cyan</li>
     *     <li>tutors - green</li>
     *     <li>students - blue</li>
     *     <li>default - white</li>
     * </ul>
     *
     * @param type The report type
     * @return The ANSI color code string for picocli formatting
     */
    private String getTypeColor(String type) {
        if (type == null) return "white";

        return switch (type.toLowerCase()) {
            case "tutoring_sessions" -> "cyan";
            case "tutors" -> "green";
            case "students" -> "blue";
            default -> "white";
        };
    }
}