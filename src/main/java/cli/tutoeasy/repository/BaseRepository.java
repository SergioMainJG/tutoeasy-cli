package cli.tutoeasy.repository;

import cli.tutoeasy.config.hibernate.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 * An abstract base repository providing common CRUD operations for entities.
 * This class uses the Java Persistence API (JPA) to interact with the database.
 *
 * @param <T> The type of the entity.
 */
public abstract class BaseRepository<T> {

    /**
     * The class of the entity.
     */
    private final Class<T> entityClass;

    /**
     * Constructs a new instance of the {@code BaseRepository}.
     *
     * @param entityClass The class of the entity.
     */
    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Saves an entity to the database.
     *
     * @param entity The entity to save.
     */
    public void save(T entity) {
        executeInTransaction(em -> em.persist(entity));
    }

    /**
     * Finds an entity by its ID.
     *
     * @param id The ID of the entity to find.
     * @return The found entity, or {@code null} if not found.
     */
    public T findById(int id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.find(entityClass, id);
        }
    }

    /**
     * Updates an entity in the database.
     *
     * @param entity The entity to update.
     */
    public void update(T entity) {
        executeInTransaction(em -> em.merge(entity));
    }

    /**
     * Deletes an entity from the database by its ID.
     *
     * @param id The ID of the entity to delete.
     */
    public void delete(int id) {
        executeInTransaction(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
        });
    }

    /**
     * Executes an action within a database transaction.
     *
     * @param action The action to execute.
     */
    protected void executeInTransaction(EntityManagerConsumer action) {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();
            action.accept(em);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error executing transaction", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Executes a database query.
     *
     * @param query The query to execute.
     * @param <R>   The type of the result.
     * @return The result of the query.
     */
    protected <R> R executeQuery(EntityManagerFunction<R> query) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return query.apply(em);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * A functional interface for consuming an {@link EntityManager}.
     */
    @FunctionalInterface
    protected interface EntityManagerConsumer {
        /**
         * Accepts an {@code EntityManager}.
         *
         * @param em The {@code EntityManager}.
         */
        void accept(EntityManager em);
    }

    /**
     * A functional interface for applying a function to an {@link EntityManager}.
     *
     * @param <R> The type of the result.
     */
    @FunctionalInterface
    protected interface EntityManagerFunction<R> {
        /**
         * Applies a function to an {@code EntityManager}.
         *
         * @param em The {@code EntityManager}.
         * @return The result of the function.
         */
        R apply(EntityManager em);
    }
}