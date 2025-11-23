package cli.tutoeasy.repository;

import cli.tutoeasy.config.JPAUtil;
import cli.tutoeasy.model.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class UserRepository extends BaseRepository<User> {

    public UserRepository() {
        super(User.class);
    }

    public User findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email",
                            User.class
                    ).setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}

