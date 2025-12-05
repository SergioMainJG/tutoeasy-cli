package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

/**
 * Represents a tutor's schedule entity in the database.
 * This class is mapped to the "TutorSchedule" table and contains information about a tutor's availability.
 */
@Entity
@Table(name = "TutorSchedule", indexes = {
    @Index(name = "idx_schedule_tutor", columnList = "tutorId"),
    @Index(name = "idx_schedule_tutor_day", columnList = "tutorId, dayOfWeek")
})
@Getter
@Setter
@NoArgsConstructor
public class TutorSchedule {

    /**
     * The unique identifier for the schedule.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The tutor to whom the schedule belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorId", nullable = false)
    private User tutor;

    /**
     * The day of the week for the schedule.
     */
    @Column(nullable = false)
    private int dayOfWeek;

    /**
     * The start time of the schedule.
     */
    @Column(nullable = false)
    private LocalTime startTime;

    /**
     * The end time of the schedule.
     */
    @Column(nullable = false)
    private LocalTime endTime;
}