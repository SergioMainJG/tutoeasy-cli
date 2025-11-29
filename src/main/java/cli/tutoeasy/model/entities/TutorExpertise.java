package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Represents a tutor's expertise in a subject entity in the database.
 * This class is mapped to the "TutorExpertise" table and contains information about a tutor's expertise in a subject.
 */
@Entity
@Table(name = "TutorExpertise", 
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_expertise", columnNames = {"tutorId", "subjectId"})
    },
    indexes = {
        @Index(name = "idx_expertise_tutor", columnList = "tutorId"),
        @Index(name = "idx_expertise_subject", columnList = "subjectId")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class TutorExpertise {

    /**
     * The unique identifier for the expertise.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The tutor who has the expertise.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorId", nullable = false)
    private User tutor;

    /**
     * The subject in which the tutor has expertise.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subjectId", nullable = false)
    private Subject subject;
}