package cli.tutoeasy;

import jakarta.persistence.EntityManager;
import cli.tutoeasy.config.JPAUtil;

public class Main {
    public static void main(String[] args) {
        System.out.println("Intentando iniciar la conexión JPA/Hibernate...");

        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();

            System.out.println("Conexión exitosa a la base de datos MySQL.");


        } catch (Exception e) {
            System.err.println("Fallo en la conexión o inicialización de Hibernate.");
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            JPAUtil.shutdown();
        }

        System.out.println("Aplicación terminada.");
    }
}