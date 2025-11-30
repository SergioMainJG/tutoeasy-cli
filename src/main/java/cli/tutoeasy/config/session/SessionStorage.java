package cli.tutoeasy.config.session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Manages the storage of session data.
 * This class provides methods for saving, loading, and clearing session data from a JSON file.
 *
 * @see SessionData
 */
public class SessionStorage {

    /**
     * The path to the session file.
     */
    private static final String SESSION_FILE =
            System.getProperty("user.home") + File.separator + ".tutoeasy_session.json";

    /**
     * The Gson instance for JSON serialization and deserialization.
     */
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Saves the session data to a file.
     *
     * @param data The session data to save.
     */
    public static void save(SessionData data) {
        try (
                Writer writer = new OutputStreamWriter(
                new FileOutputStream(SESSION_FILE),
                StandardCharsets.UTF_8
        )) {
            gson.toJson(data, writer);
        } catch (Exception e) {
            System.out.println("Failed to save session: " + e.getMessage());
        }
    }

    /**
     * Loads the session data from a file.
     *
     * @return The loaded session data, or {@code null} if the file does not exist or an error occurs.
     */
    public static SessionData load() {
        File file = new File(SESSION_FILE);

        if (!file.exists()) return null;

        try (Reader reader = new InputStreamReader(
                new FileInputStream(file),
                StandardCharsets.UTF_8
        )) {
            return gson.fromJson(reader, SessionData.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Clears the session data by deleting the session file.
     */
    public static void clear() {
        File f = new File(SESSION_FILE);
        if (f.exists()) f.delete();
    }
}