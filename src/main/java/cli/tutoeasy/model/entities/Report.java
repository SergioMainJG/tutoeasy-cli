package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Represents a report entity in the database.
 * This class is mapped to the "Reports" table and contains information about a
 * report.
 */
@Entity
@Table(name = "Reports", indexes = {
        @Index(name = "idx_report_admin", columnList = "createdByAdmin"),
        @Index(name = "idx_report_type", columnList = "reportType"),
        @Index(name = "idx_report_created", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
public class Report {

    /**
     * The unique identifier for the report.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The administrator who created the report.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdByAdmin", nullable = false)
    private User createdByAdmin;

    /**
     * The type of the report.
     */
    @Column(length = 50, nullable = false)
    private String reportType;

    /**
     * The content of the report.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * The timestamp of when the report was created.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
}