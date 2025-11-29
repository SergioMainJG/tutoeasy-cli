package cli.tutoeasy.config.argon2;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for hashing and verifying passwords using the Argon2 algorithm.
 * This class provides methods for creating a password hash and verifying a password against a given hash.
 */
public class Argon2Util {
    /**
     * The length of the salt in bytes.
     */
    private static final int SALT_LENGTH = 16;
    /**
     * The length of the hash in bytes.
     */
    private static final int HASH_LENGTH = 32;
    /**
     * The number of iterations to use for the Argon2 algorithm.
     */
    private static final int ITERATIONS = 3;
    /**
     * The memory cost in kilobytes.
     */
    private static final int MEMORY_KB = 65536; // 64MB
    /**
     * The degree of parallelism to use for the Argon2 algorithm.
     */
    private static final int PARALLELISM = 1;

    /**
     * Hashes a password using the Argon2 algorithm.
     *
     * @param password The password to hash.
     * @return The hashed password in the PHC string format.
     */
    public static String hashingPassword(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY_KB)
                .withParallelism(PARALLELISM)
                .withSalt(salt)
                .build();

        byte[] hash = new byte[HASH_LENGTH];
        Argon2BytesGenerator gen = new Argon2BytesGenerator();
        gen.init(params);
        gen.generateBytes(password.toCharArray(), hash);

        return "$argon2id$v=19"
                + "$m=" + MEMORY_KB + ",t=" + ITERATIONS + ",p=" + PARALLELISM
                + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Verifies a password against a PHC string hash.
     *
     * @param password The password to verify.
     * @param phc      The PHC string hash to verify against.
     * @return {@code true} if the password matches the hash, {@code false} otherwise.
     */
    public static boolean verifyPassword(String password, String phc) {
        try {
            String[] parts = phc.split("\\$");
            String[] paramsSplit = parts[3].split(",");
            int mem = Integer.parseInt(paramsSplit[0].split("=")[1]);
            int iter = Integer.parseInt(paramsSplit[1].split("=")[1]);
            int paral = Integer.parseInt(paramsSplit[2].split("=")[1]);

            byte[] salt = Base64.getDecoder().decode(parts[4]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[5]);

            Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                    .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                    .withIterations(iter)
                    .withMemoryAsKB(mem)
                    .withParallelism(paral)
                    .withSalt(salt)
                    .build();

            byte[] newHash = new byte[expectedHash.length];
            Argon2BytesGenerator gen = new Argon2BytesGenerator();
            gen.init(params);
            gen.generateBytes(password.toCharArray(), newHash);

            return MessageDigest.isEqual(expectedHash, newHash);

        } catch (Exception e) {
            return false;
        }
    }
}
