package cli.tutoeasy.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Represents a topic entity in the database.
 * This class is mapped to the "Topics" table and contains information about a topic.
 */
@Entity
@Table(name = "Topics", indexes = {
    @Index(name = "idx_topic_subject", columnList = "subjectId")
})
@Data
public class Topic {
    /**
     * The unique identifier for the topic.
     */
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * The subject to which the topic belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subjectId", nullable = false)
    private Subject subject;

    /**
     * The name of the topic.
     */
    @Column(length = 50, nullable = false)
    private String name;
}