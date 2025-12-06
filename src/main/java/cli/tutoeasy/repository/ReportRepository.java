package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.Report;

import java.util.List;

/**
 * <p>
 * Repository class for managing {@link Report} entities in the database.
 * This class extends {@link BaseRepository} to inherit common CRUD operations
 * and provides specialized query methods for report-specific data retrieval.
 * </p>
 *
 * <p>
 * The repository handles all database interactions related to administrative reports,
 * including:
 * </p>
 * <ul>
 *     <li>Fetching all reports with optional limits</li>
 *     <li>Filtering reports by type (tutoring_sessions, tutors, students)</li>
 *     <li>Eager loading of related entities (admin user) for efficient querying</li>
 *     <li>Ordering results by creation date</li>
 * </ul>
 *
 * <h3>Query Optimization:</h3>
 * <p>
 * This repository uses JPA's {@code JOIN FETCH} to eagerly load the admin user
 * relationship, preventing N+1 query problems when displaying report information.
 * All queries are ordered by creation date in descending order to show the most
 * recent reports first.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 * ReportRepository repo = new ReportRepository();
 *
 * // Get last 10 reports
 * List&lt;Report&gt; recent = repo.findAllReports(10);
 *
 * // Get tutoring session reports
 * List&lt;Report&gt; sessions = repo.findByType("tutoring_sessions", null);
 *
 * // Get specific report
 * Report report = repo.findById(5);
 * </pre>
 *
 * @version 1.0
 * @since 1.0
 * @see BaseRepository
 * @see Report
 * @see cli.tutoeasy.service.ReportService
 */
public class ReportRepository extends BaseRepository<Report> {

    /**
     * Constructs a new instance of {@code ReportRepository}.
     * Initializes the repository with the {@link Report} entity class.
     */
    public ReportRepository() {
        super(Report.class);
    }

    /**
     * Retrieves all reports from the database with an optional limit.
     *
     * <p>
     * This method fetches reports ordered by creation date in descending order
     * (most recent first). The admin relationship is eagerly loaded using a
     * {@code LEFT JOIN FETCH} to avoid additional database queries.
     * </p>
     *
     * <p>
     * If a limit is specified, only that number of most recent reports will be returned.
     * If the limit is null, all reports are returned.
     * </p>
     *
     * <h3>Performance Considerations:</h3>
     * <ul>
     *     <li>Uses eager loading to prevent N+1 query problems</li>
     *     <li>Limits results to avoid loading excessive data</li>
     *     <li>Orders by indexed column (createdAt) for efficient sorting</li>
     * </ul>
     *
     * @param limit Optional maximum number of reports to retrieve.
     *              If null, all reports are retrieved.
     * @return A list of {@link Report} entities with their admin relationships loaded.
     *         Returns an empty list if no reports are found.
     */
    public List<Report> findAllReports(Integer limit) {
        return executeQuery(em -> {
            var query = em.createQuery("""
                    SELECT r FROM Report r
                    LEFT JOIN FETCH r.createdByAdmin
                    ORDER BY r.createdAt DESC
                    """, Report.class);

            if (limit != null && limit > 0) {
                query.setMaxResults(limit);
            }

            return query.getResultList();
        });
    }

    /**
     * Retrieves reports filtered by type with an optional limit.
     *
     * <p>
     * This method fetches reports of a specific type, ordered by creation date
     * in descending order. The admin relationship is eagerly loaded for efficiency.
     * </p>
     *
     * <p>
     * Valid report types include:
     * </p>
     * <ul>
     *     <li><code>tutoring_sessions</code> - Reports about tutoring sessions</li>
     *     <li><code>tutors</code> - Reports about tutors</li>
     *     <li><code>students</code> - Reports about students</li>
     * </ul>
     *
     * <h3>Query Optimization:</h3>
     * <p>
     * The query uses an index on the {@code reportType} column for efficient filtering.
     * Results are limited to avoid loading excessive data when only a subset is needed.
     * </p>
     *
     * @param reportType The type of reports to retrieve (e.g., "tutoring_sessions", "tutors", "students")
     * @param limit Optional maximum number of reports to retrieve.
     *              If null, all matching reports are retrieved.
     * @return A list of {@link Report} entities matching the specified type,
     *         with their admin relationships loaded. Returns an empty list if no
     *         matching reports are found.
     */
    public List<Report> findByType(String reportType, Integer limit) {
        return executeQuery(em -> {
            var query = em.createQuery("""
                    SELECT r FROM Report r
                    LEFT JOIN FETCH r.createdByAdmin
                    WHERE r.reportType = :type
                    ORDER BY r.createdAt DESC
                    """, Report.class)
                    .setParameter("type", reportType);

            if (limit != null && limit > 0) {
                query.setMaxResults(limit);
            }

            return query.getResultList();
        });
    }

    /**
     * Retrieves reports created by a specific administrator.
     *
     * <p>
     * This method is useful for displaying an administrator's report history
     * or for administrative oversight. The results are ordered by creation date
     * in descending order.
     * </p>
     *
     * <p>
     * Note: While the admin relationship is fetched, this is more for consistency
     * with other queries than necessity, since we're filtering by admin ID.
     * </p>
     *
     * @param adminId The unique identifier of the administrator whose reports to retrieve
     * @return A list of {@link Report} entities created by the specified administrator.
     *         Returns an empty list if the administrator has no reports.
     */
    public List<Report> findByAdmin(int adminId) {
        return executeQuery(em -> em.createQuery("""
                SELECT r FROM Report r
                LEFT JOIN FETCH r.createdByAdmin
                WHERE r.createdByAdmin.id = :adminId
                ORDER BY r.createdAt DESC
                """, Report.class)
                .setParameter("adminId", adminId)
                .getResultList());
    }
}