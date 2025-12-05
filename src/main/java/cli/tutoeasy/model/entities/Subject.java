package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Represents a subject entity in the database.
 * This class is mapped to the "Subjects" table.
 */
@Entity
@Table(name = "Subjects")
@Data
public class Subject {
    /**
     * The unique identifier for the subject.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The name of the subject.
     */
    @Column(length = 50, unique = true, nullable = false)
    private String name;
}