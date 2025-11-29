package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a tutoring session entity in the database.
 * This class is mapped to the "Tutorings" table and contains information about a tutoring session.
 */
@Entity
@Table(name = "Tutorings", indexes = {
    @Index(name = "idx_tutoring_tutor_status", columnList = "tutorId, status"),
    @Index(name = "idx_tutoring_schedule", columnList = "tutorId, meetingDate, meetingTime, status"),
    @Index(name = "idx_tutoring_student", columnList = "studentId"),
    @Index(name = "idx_tutoring_subject", columnList = "subjectId")
})
@Getter
@Setter
@NoArgsConstructor
public class Tutoring {

    /**
     * The unique identifier for the tutoring session.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The student who requested the tutoring session.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studentId", nullable = false)
    private User student;

    /**
     * The tutor who will conduct the tutoring session.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorId", nullable = false)
    private User tutor;

    /**
     * The subject of the tutoring session.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subjectId", nullable = false)
    private Subject subject;

    /**
     * The topic of the tutoring session.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topicId")
    private Topic topic;

    /**
     * The status of the tutoring session.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TutoringStatus status;

    /**
     * The date of the tutoring session.
     */
    @Column(nullable = false)
    private LocalDate meetingDate;

    /**
     * The time of the tutoring session.
     */
    @Column(nullable = false)
    private LocalTime meetingTime;
}