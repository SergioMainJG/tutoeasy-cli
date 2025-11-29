package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.List;

/**
 * Represents a user entity in the database.
 * This class is mapped to the "Users" table and contains information about a user.
 */
@Entity
@Table(name = "Users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_role", columnList = "rol"),
    @Index(name = "idx_user_username", columnList = "username")
})
@Data
public class User {
    /**
     * The unique identifier for the user.
     */
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The username of the user.
     */
    @Column(length = 20, unique = true, nullable = false)
    private String username;

    /**
     * The email of the user.
     */
    @Column(length = 100, unique = true, nullable = false)
    private String email;

    /**
     * The hashed password of the user.
     */
    @Column(length = 255, nullable = false)
    private String passwordHash;

    /**
     * The role of the user.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole rol;

    /**
     * The career of the user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "careerId")
    private Career career;

    /**
     * A description of the user.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * A list of tutorings associated with the user.
     */
    @OneToMany(mappedBy = "tutor", fetch = FetchType.LAZY)
    private List<Tutoring> tutorings;
}