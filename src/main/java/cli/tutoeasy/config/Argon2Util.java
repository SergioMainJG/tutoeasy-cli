package cli.tutoeasy.config;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class Argon2Util {
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private static final int ITERATIONS = 3;
    private static final int MEMORY_KB = 65536; // 64MB
    private static final int PARALLELISM = 1;

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
