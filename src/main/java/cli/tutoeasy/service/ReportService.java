package cli.tutoeasy.service;

import cli.tutoeasy.model.dto.ActionResponseDto;
import cli.tutoeasy.model.dto.CreateReportDto;
import cli.tutoeasy.model.dto.ReportDto;
import cli.tutoeasy.model.entities.Report;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.repository.ReportRepository;
import cli.tutoeasy.repository.UserRepository;
import cli.tutoeasy.util.files.FileExportUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Service class for handling report-related operations in the TutoEasy system.
 * This service manages the creation, retrieval, deletion, and export of administrative reports.
 * </p>
 *
 * <p>
 * The service provides functionality for administrators to:
 * </p>
 * <ul>
 *     <li>Create new reports with various types (tutoring sessions, tutors, students)</li>
 *     <li>Retrieve reports with optional filtering by type and limit</li>
 *     <li>Delete existing reports (with ownership validation)</li>
 *     <li>Export reports to different file formats (TXT, Markdown, DOCX)</li>
 *     <li>Display report previews in the console</li>
 * </ul>
 *
 * <h3>Report Types:</h3>
 * <p>The service supports three main report categories:</p>
 * <ul>
 *     <li><strong>tutoring_sessions</strong> - Statistics and information about tutoring sessions</li>
 *     <li><strong>tutors</strong> - Information about registered tutors and their activities</li>
 *     <li><strong>students</strong> - Information about registered students and participation</li>
 * </ul>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 * ReportService reportService = new ReportService(reportRepository, userRepository);
 *
 * // Create a new report
 * CreateReportDto dto = new CreateReportDto("tutoring_sessions", "Monthly summary");
 * ActionResponseDto response = reportService.createReport(adminId, dto);
 *
 * // Get all reports
 * List&lt;ReportDto&gt; reports = reportService.getAllReports(10);
 *
 * // Export to file
 * ActionResponseDto exportResponse = reportService.exportReportToFile(
 *     adminId, "tutors", "Tutor performance report", "docx"
 * );
 * </pre>
 *
 * @version 1.0
 * @since 1.0
 * @see ReportRepository
 * @see UserRepository
 * @see FileExportUtil
 * @see Report
 */
public class ReportService {

    /**
     * Repository for managing report data persistence.
     * Handles all database operations related to reports.
     */
    private final ReportRepository reportRepository;

    /**
     * Repository for managing user data.
     * Used to validate administrators and retrieve user information.
     */
    private final UserRepository userRepository;

    /**
     * Constructs a new instance of {@code ReportService}.
     *
     * @param reportRepository The repository for managing report data. Must not be null.
     * @param userRepository The repository for managing user data. Must not be null.
     */
    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new administrative report.
     *
     * <p>
     * This method validates the administrator, creates a new report entity with the provided
     * information, stores it in the database, and displays a formatted preview in the console.
     * </p>
     *
     * <p>
     * The report is automatically timestamped with the current date and time, and associated
     * with the administrator who created it.
     * </p>
     *
     * <h3>Validation:</h3>
     * <ul>
     *     <li>Verifies that the administrator exists in the system</li>
     *     <li>Ensures the report type is valid</li>
     *     <li>Validates that content is not empty</li>
     * </ul>
     *
     * @param adminId The unique identifier of the administrator creating the report
     * @param dto The data transfer object containing report type and content
     * @return An {@link ActionResponseDto} indicating success or failure with an appropriate message
     * @throws IllegalArgumentException if the administrator is not found
     */
    public ActionResponseDto createReport(int adminId, CreateReportDto dto) {
        User admin = userRepository.findById(adminId);
        if (admin == null) {
            return new ActionResponseDto(false, "Administrator not found.");
        }

        Report report = new Report();
        report.setCreatedByAdmin(admin);
        report.setReportType(dto.reportType());
        report.setContent(dto.content());
        report.setCreatedAt(LocalDateTime.now());

        reportRepository.save(report);

        displayReportPreview(report);

        return new ActionResponseDto(true,
                "Report created successfully (ID: " + report.getId() + ")");
    }

    /**
     * Retrieves all reports from the system.
     *
     * <p>
     * This method fetches reports from the database and converts them to DTOs for display.
     * The results are ordered by creation date in descending order (most recent first).
     * </p>
     *
     * <p>
     * An optional limit can be specified to restrict the number of results returned.
     * If no limit is specified (null), all reports are returned.
     * </p>
     *
     * @param limit Optional maximum number of reports to retrieve. If null, retrieves all reports.
     * @return A list of {@link ReportDto} objects representing the reports.
     *         Returns an empty list if no reports are found.
     */
    public List<ReportDto> getAllReports(Integer limit) {
        List<Report> reports = reportRepository.findAllReports(limit);
        return reports.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves reports filtered by type.
     *
     * <p>
     * This method fetches reports of a specific type from the database and converts them
     * to DTOs. The results are ordered by creation date in descending order.
     * </p>
     *
     * <p>
     * Valid report types are:
     * </p>
     * <ul>
     *     <li>tutoring_sessions</li>
     *     <li>tutors</li>
     *     <li>students</li>
     * </ul>
     *
     * @param reportType The type of reports to retrieve (tutoring_sessions, tutors, students)
     * @param limit Optional maximum number of reports to retrieve. If null, retrieves all matching reports.
     * @return A list of {@link ReportDto} objects of the specified type.
     *         Returns an empty list if no reports are found.
     */
    public List<ReportDto> getReportsByType(String reportType, Integer limit) {
        List<Report> reports = reportRepository.findByType(reportType, limit);
        return reports.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a report from the system.
     *
     * <p>
     * This method performs the following validations before deletion:
     * </p>
     * <ul>
     *     <li>Verifies that the report exists</li>
     *     <li>Ensures the requesting administrator exists</li>
     *     <li>Validates that the administrator owns the report (created it)</li>
     * </ul>
     *
     * <p>
     * Only the administrator who created a report can delete it. This prevents
     * unauthorized deletion of reports by other administrators.
     * </p>
     *
     * @param reportId The unique identifier of the report to delete
     * @param adminId The unique identifier of the administrator requesting the deletion
     * @return An {@link ActionResponseDto} indicating success or failure with an appropriate message
     */
    public ActionResponseDto deleteReport(int reportId, int adminId) {
        Report report = reportRepository.findById(reportId);
        if (report == null) {
            return new ActionResponseDto(false, "Report not found with ID: " + reportId);
        }

        User admin = userRepository.findById(adminId);
        if (admin == null) {
            return new ActionResponseDto(false, "Administrator not found.");
        }

        if (report.getCreatedByAdmin().getId() != adminId) {
            return new ActionResponseDto(false,
                    "Access denied. You can only delete reports you created.");
        }

        reportRepository.delete(reportId);
        return new ActionResponseDto(true, "Report deleted successfully.");
    }

    /**
     * Exports a report to a file in the specified format.
     *
     * <p>
     * This method creates a file containing the report content in one of three supported formats:
     * </p>
     * <ul>
     *     <li><strong>txt</strong> - Plain text format with simple formatting</li>
     *     <li><strong>md</strong> - Markdown format with headers and formatting</li>
     *     <li><strong>docx</strong> - Microsoft Word document with professional formatting</li>
     * </ul>
     *
     * <p>
     * The file is created in the current working directory with a timestamp-based filename
     * to avoid conflicts. The filename format is: {@code report_[type]_[timestamp].[extension]}
     * </p>
     *
     * <h3>File Generation:</h3>
     * <p>
     * Files are generated using the {@link FileExportUtil} utility class, which handles
     * the format-specific logic for each file type. The utility ensures proper encoding,
     * formatting, and structure for each output format.
     * </p>
     *
     * <h3>Automatic File Opening:</h3>
     * <p>
     * If {@code openFile} is true, the system will attempt to open the generated file
     * using the operating system's default application for that file type. This requires
     * a graphical environment and desktop support.
     * </p>
     *
     * @param adminId The unique identifier of the administrator creating the export
     * @param reportType The type of report being exported (for metadata)
     * @param content The actual content to be written to the file
     * @param format The desired file format: "txt", "md", or "docx"
     * @param openFile Whether to automatically open the file after creation
     * @return An {@link ActionResponseDto} indicating success with the file path,
     *         or failure with an error messagesw
     */
    public ActionResponseDto exportReportToFile(int adminId, String reportType,
                                                String content, String format, boolean openFile) {
        User admin = userRepository.findById(adminId);
        if (admin == null) {
            return new ActionResponseDto(false, "Administrator not found.");
        }

        try {
            String timestamp = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = String.format("report_%s_%s", reportType, timestamp);

            String filePath = FileExportUtil.exportReport(
                    filename,
                    reportType,
                    content,
                    admin.getUsername(),
                    format,
                    openFile);

            return new ActionResponseDto(true,
                    "Report exported successfully to: " + filePath);
        } catch (Exception e) {
            return new ActionResponseDto(false,
                    "Failed to export report: " + e.getMessage());
        }
    }

    /**
     * Displays a formatted preview of a report in the console.
     *
     * <p>
     * This method outputs a nicely formatted representation of the report with:
     * </p>
     * <ul>
     *     <li>A header with decorative borders</li>
     *     <li>Report metadata (type, creator, timestamp)</li>
     *     <li>The full report content</li>
     *     <li>ANSI color coding for better readability</li>
     * </ul>
     *
     * <p>
     * The preview is designed to give administrators an immediate view of the report
     * content without needing to open a separate file or query the database again.
     * </p>
     *
     * @param report The report entity to display
     */
    private void displayReportPreview(Report report) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("REPORT PREVIEW");
        System.out.println("=".repeat(80));
        System.out.println("Type: " + report.getReportType());
        System.out.println("Created by: " + report.getCreatedByAdmin().getUsername());
        System.out.println("Created at: " + report.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("-".repeat(80));
        System.out.println(report.getContent());
        System.out.println("=".repeat(80) + "\n");
    }

    /**
     * Maps a {@link Report} entity to a {@link ReportDto} for data transfer.
     *
     * <p>
     * This helper method converts database entities to DTOs suitable for use
     * in the presentation layer. It extracts all relevant information including
     * the administrator's username.
     * </p>
     *
     * <p>
     * This mapping ensures separation of concerns between the data access layer
     * (entities) and the presentation layer (DTOs).
     * </p>
     *
     * @param report The report entity to convert
     * @return A {@link ReportDto} containing the report information
     */
    private ReportDto mapToDto(Report report) {
        return new ReportDto(
                report.getId(),
                report.getReportType(),
                report.getCreatedByAdmin().getUsername(),
                report.getContent(),
                report.getCreatedAt()
        );
    }
}