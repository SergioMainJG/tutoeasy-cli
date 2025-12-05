package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.Topic;
import jakarta.persistence.NoResultException;

/**
 * Repository for managing Topic entities.
 */
public class TopicRepository extends BaseRepository<Topic> {

    /**
     * Constructs a new instance of {@code TopicRepository}.
     */
    public TopicRepository() {
        super(Topic.class);
    }

    /**
     * Finds a topic by name and subject ID.
     *
     * @param name The name of the topic.
     * @param subjectId The ID of the subject.
     * @return The topic if found, null otherwise.
     */
    public Topic findByNameAndSubject(String name, int subjectId) {
        return executeQuery(em -> {
            try {
                return em.createQuery(
                        "SELECT t FROM Topic t WHERE LOWER(t.name) = LOWER(:name) AND t.subject.id = :subjectId",
                        Topic.class)
                        .setParameter("name", name)
                        .setParameter("subjectId", subjectId)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        });
    }
}
