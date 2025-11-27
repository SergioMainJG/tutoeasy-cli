package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "Users")
@Data
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    @Column(unique = true)
    private String email;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserRole rol;

    @ManyToOne
    @JoinColumn(name = "careerId")
    private Career career;

    private String description;

    @OneToMany(mappedBy = "tutor")
    private List<Tutoring> tutorings;
}
