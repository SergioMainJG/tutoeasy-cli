package cli.tutoeasy.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    public static Dotenv getEnv() {
        return Dotenv
                .configure()
                .load();
    }
}
