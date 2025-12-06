package cli.tutoeasy.model.dto;

import java.time.LocalDateTime;

/**
 * Data transfer object for displaying report information.
 *
 * <p>
 * This record represents a complete report entity with all its associated information,
 * used primarily for displaying reports to administrators. It includes metadata about
 * when and by whom the report was created, along with its content.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 * ReportDto report = new ReportDto(
 *     1,
 *     "tutoring_sessions",
 *     "admin_user",
 *     "Monthly tutoring sessions report showing 85% completion rate",
 *     LocalDateTime.now()
 * );
 * </pre>
 *
 * <p>
 * This DTO is typically created by the {@link cli.tutoeasy.service.ReportService}
 * when retrieving reports from the database and is used in the presentation layer
 * to display report information to users.
 * </p>
 *
 * @param id The unique identifier of the report in the database
 * @param reportType The type/category of the report (tutoring_sessions, tutors, students)
 * @param adminUsername The username of the administrator who created the report
 * @param content The detailed content or description of the report
 * @param createdAt The timestamp when the report was created
 *
 * @version 1.0
 * @since 1.0
 * @see cli.tutoeasy.model.entities.Report
 * @see cli.tutoeasy.service.ReportService
 * @see CreateReportDto
 */
public record ReportDto(
        int id,
        String reportType,
        String adminUsername,
        String content,
        LocalDateTime createdAt
) {
}