package cli.tutoeasy.model.dto;

/**
 * Data transfer object for creating a new administrative report.
 *
 * <p>
 * This record encapsulates the essential information required to create a new report
 * in the TutoEasy system. Reports are used by administrators to track and analyze
 * various aspects of the tutoring platform including sessions, tutors, and students.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 * CreateReportDto dto = new CreateReportDto(
 *     "tutoring_sessions",
 *     "Monthly summary of all tutoring sessions with completion rates"
 * );
 * </pre>
 *
 * <h3>Valid Report Types:</h3>
 * <ul>
 *     <li><code>tutoring_sessions</code> - Reports about tutoring session statistics</li>
 *     <li><code>tutors</code> - Reports about tutor activities and performance</li>
 *     <li><code>students</code> - Reports about student participation and engagement</li>
 * </ul>
 *
 * @param reportType The type/category of the report. Must be one of: tutoring_sessions, tutors, students
 * @param content The detailed content or description of the report
 *
 * @version 1.0
 * @since 1.0
 * @see cli.tutoeasy.service.ReportService
 * @see cli.tutoeasy.model.entities.Report
 */
public record CreateReportDto(
        String reportType,
        String content
) {
}