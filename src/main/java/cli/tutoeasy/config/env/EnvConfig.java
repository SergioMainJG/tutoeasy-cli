package cli.tutoeasy.config.env;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Configuration class for loading environment variables.
 * This class uses the {@link Dotenv} library to load environment variables from a {@code .env} file.
 */
public class EnvConfig {
    /**
     * Returns a {@link Dotenv} instance loaded with the environment variables.
     *
     * @return A {@code Dotenv} instance.
     */
    public static Dotenv getEnv() {
        return Dotenv
                .configure()
                .load();
    }
}
