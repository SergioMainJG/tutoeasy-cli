package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Topics")
@Data
public class Topic {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "subjectId")
    private Subject subject;

    private String name;
}

