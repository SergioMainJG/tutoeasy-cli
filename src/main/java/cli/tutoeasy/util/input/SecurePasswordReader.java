package cli.tutoeasy.util.input;

import java.io.Console;
import java.io.IOException;

/**
 * Utility class for secure password input.
 * Provides methods to read passwords without displaying them on the console.
 */
public class SecurePasswordReader {

    /**
     * Reads a password securely from the console without displaying it.
     * Uses System.console() when available (terminal mode).
     * Falls back to masked input using a separate thread for IDE/non-terminal environments.
     *
     * @param prompt The prompt message to display to the user
     * @return The password entered by the user
     */
    public static String readPassword(String prompt) {
        Console console = System.console();

        if (console != null) {
            char[] passwordArray = console.readPassword(prompt);
            return new String(passwordArray);
        } else {
            return readPasswordMasked(prompt);
        }
    }

    /**
     * Reads a password with masking using a separate thread.
     * This method creates a background thread that continuously prints backspaces
     * to hide the password characters as they are typed.
     *
     * @param prompt The prompt message to display
     * @return The password entered by the user
     */
    private static String readPasswordMasked(String prompt) {
        final String[] password = {""};
        final boolean[] threadRunning = {true};

        Thread maskingThread = new Thread(() -> {
            while (threadRunning[0]) {
                try {
                    System.out.print("\b*");
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        try {
            System.out.print(prompt);

            maskingThread.start();

            StringBuilder sb = new StringBuilder();
            int c;

            while ((c = System.in.read()) != '\n' && c != '\r') {
                if (c == -1) break;

                if (c == 8 || c == 127) {
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                } else {
                    sb.append((char) c);
                }
            }

            password[0] = sb.toString();

        } catch (IOException e) {
            System.err.println("\nError reading password: " + e.getMessage());
            password[0] = "";
        } finally {
            threadRunning[0] = false;
            try {
                maskingThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println();
        }

        return password[0];
    }

    /**
     * Alternative implementation using EraserThread for better masking.
     * This is more robust and provides better visual feedback.
     *
     * @param prompt The prompt message to display
     * @return The password entered by the user
     */
    public static String readPasswordWithEraserThread(String prompt) {
        Console console = System.console();

        if (console != null) {
            char[] passwordArray = console.readPassword(prompt);
            return new String(passwordArray);
        } else {
            EraserThread eraserThread = new EraserThread(prompt);
            eraserThread.start();

            String password = "";
            try {
                java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(System.in)
                );
                password = reader.readLine();
            } catch (IOException e) {
                System.err.println("Error reading password");
            } finally {
                eraserThread.stopErasing();
            }

            return password;
        }
    }

    /**
     * Thread class that continuously erases console output to hide password input.
     */
    private static class EraserThread extends Thread {
        private boolean running = true;
        private final String prompt;

        public EraserThread(String prompt) {
            this.prompt = prompt;
        }

        @Override
        public void run() {
            while (running) {
                System.out.print("\r" + prompt + " ");
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        public void stopErasing() {
            running = false;
            System.out.print("\r" + prompt + " " + "\n");
        }
    }
}