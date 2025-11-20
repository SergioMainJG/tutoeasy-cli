package cli.tutoeasy.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

import cli.tutoeasy.config.EnvConfig.*;

public class JPAUtil {
    private static final String PERSISTENCE_UNIT_NAME = "tutoeasy-cli";
    private static EntityManagerFactory factory;

    static {
        try {

            Map<String, String> envProps = new HashMap<>();
            envProps.put("jakarta.persistence.jdbc.url", EnvConfig.getEnv().get("MYSQL_URL"));
            envProps.put("jakarta.persistence.jdbc.user",EnvConfig.getEnv().get("MYSQL_USER"));
            envProps.put("jakarta.persistence.jdbc.password",EnvConfig.getEnv().get("MYSQL_PASSWORD"));

            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, envProps);
        } catch (Exception ex) {
            System.err.println("La creación de la fábrica de EntityManager falló: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    public static void shutdown() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
