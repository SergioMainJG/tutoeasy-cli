package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Careers")
@Data
public class Career {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
}

