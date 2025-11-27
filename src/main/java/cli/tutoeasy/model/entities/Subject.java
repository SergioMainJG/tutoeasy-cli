package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Subjects")
@Data
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
}

