package cli.tutoeasy.config.hibernate;

import cli.tutoeasy.config.env.EnvConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for managing the JPA {@link EntityManagerFactory}.
 * This class provides a singleton instance of the {@code EntityManagerFactory} and methods for creating {@link EntityManager} instances.
 * It also handles the configuration of the persistence unit and database connection properties.
 *
 * @see EnvConfig
 * @see EntityManager
 * @see EntityManagerFactory
 */
public class JPAUtil {

    /**
     * The name of the persistence unit.
     */
    private static final String PERSISTENCE_UNIT_NAME = "tutoeasy-cli";
    /**
     * The volatile {@code EntityManagerFactory} instance.
     */
    private static volatile EntityManagerFactory factory;
    /**
     * The lock object for synchronizing the initialization of the {@code EntityManagerFactory}.
     */
    private static final Object lock = new Object();

    /**
     * Private constructor to prevent instantiation.
     */
    private JPAUtil() {}

    /**
     * Returns the singleton instance of the {@code EntityManagerFactory}.
     * If the factory has not been initialized, it creates a new one using the properties from {@link EnvConfig}.
     *
     * @return The singleton {@code EntityManagerFactory} instance.
     */
    private static EntityManagerFactory getFactory() {
        if (factory == null) {
            synchronized (lock) {
                if (factory == null) {
                    try {
                        long startTime = System.currentTimeMillis();

                        Map<String, String> envProps = new HashMap<>();
                        envProps.put("jakarta.persistence.jdbc.url", EnvConfig.getEnv().get("MYSQL_URL"));
                        envProps.put("jakarta.persistence.jdbc.user", EnvConfig.getEnv().get("MYSQL_USER"));
                        envProps.put("jakarta.persistence.jdbc.password", EnvConfig.getEnv().get("MYSQL_PASSWORD"));

                        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, envProps);

                        long endTime = System.currentTimeMillis();
                        System.out.println("Service initialized in " + (endTime - startTime) + "ms");
                    } catch (Exception ex) {
                        System.err.println("Error initializing EntityManagerFactory: " + ex.getMessage());
                        throw new ExceptionInInitializerError(ex);
                    }
                }
            }
        }
        return factory;
    }

    /**
     * Returns a new {@link EntityManager} instance.
     *
     * @return A new {@code EntityManager} instance.
     */
    public static EntityManager getEntityManager() {
        return getFactory().createEntityManager();
    }

    /**
     * Closes the {@code EntityManagerFactory}.
     */
    public static void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
            factory = null;
        }
    }
}
