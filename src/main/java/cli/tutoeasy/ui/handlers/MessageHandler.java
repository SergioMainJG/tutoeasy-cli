package cli.tutoeasy.ui.handlers;

import cli.tutoeasy.command.AppFactory;
import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.ConversationMessageDto;
import cli.tutoeasy.model.dto.SendMessageDto;
import cli.tutoeasy.service.MessageService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * <p>Handler for message-related operations.</p>
 * <p>Manages viewing inbox, conversations, and sending messages between users.</p>
 */
public class MessageHandler {

    /**
     * <p>Handles viewing messages with multiple options:</p>
     * <ul>
     *     <li>View inbox (all received messages)</li>
     *     <li>View only unread messages</li>
     *     <li>View conversation with specific user</li>
     * </ul>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handleViewMessages(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                        MESSAGES                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        MessageService service = factory.getMessageService();
        int userId = AuthSession.getCurrentUser().getId();

        System.out.println(" MESSAGING OPTIONS");
        System.out.println("─".repeat(60));
        System.out.println("1. View inbox (all received messages)");
        System.out.println("2. View unread messages only");
        System.out.println("3. View conversation with a specific user");
        System.out.println("0. Return to previous menu");
        System.out.println();

        System.out.print("Select an option: ");
        String input = scanner.nextLine().trim();

        try {
            int option = Integer.parseInt(input);

            switch (option) {
                case 1:
                    List<ConversationMessageDto> inbox = service.getReceivedMessages(userId);
                    displayMessages(inbox, "Inbox");
                    break;
                case 2:
                    List<ConversationMessageDto> unread = service.getUnreadMessages(userId);
                    displayMessages(unread, "Unread Messages");
                    break;
                case 3:
                    System.out.print("\nUsername: ");
                    String username = scanner.nextLine().trim();
                    if (!username.isEmpty()) {
                        List<ConversationMessageDto> conversation =
                                service.getConversation(userId, username);
                        displayConversation(conversation, username);
                    } else {
                        System.out.println("\n You must specify a username");
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\n Invalid option");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n Please enter a valid number");
        } catch (Exception e) {
            System.out.println("\n Error: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Handles sending a new message to another user.</p>
     * <p>Validates recipient username and message content.</p>
     *
     * @param scanner Scanner for reading user input.
     * @param factory AppFactory for accessing services.
     */
    public static void handleSendMessage(Scanner scanner, AppFactory factory) {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    SEND MESSAGE                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        System.out.println(" NEW MESSAGE");
        System.out.println("─".repeat(60));
        System.out.println();

        System.out.print("Recipient username: ");
        String toUsername = scanner.nextLine().trim();

        if (toUsername.isEmpty()) {
            System.out.println("\n The username cannot be empty");
            System.out.println("   You must specify the message recipient.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (toUsername.equalsIgnoreCase(AuthSession.getCurrentUser().getUsername())) {
            System.out.println("\n You cannot send a message to yourself");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("\nMessage (max 1000 characters):");
        System.out.println(" Tip: Write your message and press Enter when finished");
        System.out.print("> ");
        String content = scanner.nextLine().trim();

        if (content.isEmpty()) {
            System.out.println("\n The message cannot be empty");
            System.out.println("   You must write something to send.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        if (content.length() > 1000) {
            System.out.println("\n  The message is too long");
            System.out.println("   Current length: " + content.length() + " characters");
            System.out.println("   Max allowed: 1000 characters");
            System.out.println("   Please shorten your message.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        MessageService service = factory.getMessageService();
        int userId = AuthSession.getCurrentUser().getId();

        SendMessageDto dto = new SendMessageDto(toUsername, content);
        var response = service.sendMessage(userId, dto);

        if (response.success()) {
            System.out.println("\n " + response.message());
            System.out.println("\n Sent message details:");
            System.out.println("   To: " + toUsername);
            System.out.println("   Length: " + content.length() + " characters");
            System.out.println("   Status: Delivered");
        } else {
            System.out.println("\n " + response.message());
            System.out.println("\n Possible causes:");
            System.out.println("   - The recipient user does not exist");
            System.out.println("   - Connection error with the server");
            System.out.println("   - The message exceeds the character limit");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * <p>Displays a list of messages (inbox or unread).</p>
     * <p>Shows sender, date, and preview of message content.</p>
     *
     * @param messages List of messages to display.
     * @param title Title for the message list.
     */
    private static void displayMessages(List<ConversationMessageDto> messages, String title) {
        System.out.println("\n " + title);
        System.out.println("═".repeat(60) + "\n");

        if (messages.isEmpty()) {
            System.out.println(" No messages");
            System.out.println();
            System.out.println(" When you receive messages, they will appear here.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (int i = 0; i < messages.size(); i++) {
            var msg = messages.get(i);
            String readStatus = msg.wasRead() ? "vv" : "[ ]";
            String readLabel = msg.wasRead() ? "Read" : "Unread";

            System.out.println((i + 1) + ". " + readStatus + " From: " + msg.otherUsername() + " (" + readLabel + ")");
            System.out.println("    " + msg.createdAt().format(formatter));
            System.out.println("    " + truncate(msg.content(), 70));
            System.out.println();
        }

        System.out.println("─".repeat(60));
        System.out.println("Total: " + messages.size() + " message(s)");

        long unreadCount = messages.stream().filter(m -> !m.wasRead()).count();
        if (unreadCount > 0) {
            System.out.println(" Unread messages: " + unreadCount);
        }
    }

    /**
     * <p>Displays a full conversation with another user.</p>
     * <p>Shows messages in chronological order with sent/received indicators.</p>
     *
     * @param messages List of messages in the conversation.
     * @param otherUser Username of the other person in conversation.
     */
    private static void displayConversation(List<ConversationMessageDto> messages, String otherUser) {
        System.out.println("\n Conversation with " + otherUser);
        System.out.println("═".repeat(60) + "\n");

        if (messages.isEmpty()) {
            System.out.println(" No messages in this conversation");
            System.out.println();
            System.out.println(" Be the first to start the conversation.");
            System.out.println("   Use the 'Send message' option to write to " + otherUser);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String lastDate = "";

        for (var msg : messages) {
            String currentDate = msg.createdAt().format(dateFormatter);

            if (!currentDate.equals(lastDate)) {
                System.out.println("\n─── " + currentDate + " ───\n");
                lastDate = currentDate;
            }

            String prefix;
            String alignment;
            if (msg.isSent()) {
                prefix = "> You";
                alignment = "    ";
            } else {
                prefix = "< " + msg.otherUsername();
                alignment = "";
            }

            System.out.println(alignment + prefix + " (" + msg.createdAt().format(formatter) + "):");

            String[] lines = wrapText(msg.content(), 56);
            for (String line : lines) {
                System.out.println(alignment + "  " + line);
            }
            System.out.println();
        }

        System.out.println("─".repeat(60));
        System.out.println("Total: " + messages.size() + " message(s) in this conversation");
    }

    /**
     * <p>Truncates text to a maximum length, adding ellipsis if needed.</p>
     *
     * @param text Text to truncate.
     * @param maxLength Maximum length.
     * @return Truncated text.
     */
    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * <p>Wraps text into multiple lines at word boundaries.</p>
     *
     * @param text Text to wrap.
     * @param maxWidth Maximum width per line.
     * @return Array of wrapped lines.
     */
    private static String[] wrapText(String text, int maxWidth) {
        if (text.length() <= maxWidth) {
            return new String[] { text };
        }

        java.util.List<String> lines = new java.util.ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxWidth) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
            }
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines.toArray(new String[0]);
    }

    /**
     * <p>Clears the console screen.</p>
     * <p>Works on Windows (cmd/cls) and Unix-like systems (ANSI escape codes).</p>
     */
    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}