package cli.tutoeasy.repository;

import cli.tutoeasy.config.JPAUtil;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

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
    public List<User> findAllTutors() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.rol = :rol", User.class)
                    .setParameter("rol", UserRole.tutor)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<User> findAllAdmins() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.rol = :rol", User.class)
                    .setParameter("rol", UserRole.admin)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}

