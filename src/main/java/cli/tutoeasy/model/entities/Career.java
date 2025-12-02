package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents a career entity in the database.
 * This class is mapped to the "Careers" table and contains information about a
 * career.
 */
@Entity
@Table(name = "Careers")
@Data
public class Career {
    /**
     * The unique identifier for the career.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The name of the career.
     */
    @Column(length = 50, unique = true, nullable = false)
    private String name;
}