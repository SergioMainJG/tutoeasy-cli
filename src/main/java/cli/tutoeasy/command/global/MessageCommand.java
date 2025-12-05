package cli.tutoeasy.command.global;

import cli.tutoeasy.config.session.AuthSession;
import cli.tutoeasy.model.dto.ConversationMessageDto;
import cli.tutoeasy.model.dto.SendMessageDto;
import cli.tutoeasy.service.MessageService;
import picocli.CommandLine.*;

import java.util.List;

/**
 * Command for managing messages.
 * Allows users to send messages, view inbox, and read conversations.
 */
@Command(name = "message", description = "Send and receive messages", mixinStandardHelpOptions = true)
public class MessageCommand implements Runnable {

  /**
   * Send a message to a user (requires --text).
   */
  @Option(names = { "--to",
      "-t" }, description = "Send a message to a user (requires --text)", paramLabel = "<username>")
  private String toUsername;

  /**
   * Message content (required with --to).
   */
  @Option(names = { "--text", "-m" }, description = "Message content (required with --to)", paramLabel = "<message>")
  private String messageText;

  /**
   * View messages from a specific user.
   */
  @Option(names = { "--from", "-f" }, description = "View messages from a specific user", paramLabel = "<username>")
  private String fromUsername;

  /**
   * View all received messages.
   */
  @Option(names = { "--inbox", "-i" }, description = "View all received messages")
  private boolean showInbox;

  /**
   * View only unread messages.
   */
  @Option(names = { "--unread", "-u" }, description = "View only unread messages")
  private boolean showUnread;

  /**
   * View full conversation with a user.
   */
  @Option(names = { "--conversation",
      "-c" }, description = "View full conversation with a user", paramLabel = "<username>")
  private String conversationUsername;

  /**
   * The service responsible for handling message-related operations.
   */
  private final MessageService messageService;

  public MessageCommand(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  public void run() {
    if (!AuthSession.isLoggedIn()) {
      String msg = Help.Ansi.AUTO.string("@|red You must be logged in to use this command.|@");
      System.out.println(msg);
      return;
    }

    int currentUserId = AuthSession.getCurrentUser().getId();

    try {
      if (toUsername != null) {
        sendMessage(currentUserId);
      } else if (fromUsername != null) {
        viewMessagesFrom(currentUserId);
      } else if (conversationUsername != null) {
        viewConversation(currentUserId);
      } else if (showUnread) {
        viewUnreadMessages(currentUserId);
      } else if (showInbox) {
        viewInbox(currentUserId);
      } else {
        viewInbox(currentUserId);
      }

    } catch (IllegalArgumentException e) {
      String msg = Help.Ansi.AUTO.string("@|red " + e.getMessage() + "|@");
      System.out.println(msg);
    } catch (Exception e) {
      String msg = Help.Ansi.AUTO.string("@|red ERROR: " + e.getMessage() + "|@");
      System.out.println(msg);
    }
  }

  /**
   * Sends a message to another user
   */
  private void sendMessage(int senderId) {
    if (messageText == null || messageText.trim().isEmpty()) {
      String msg = Help.Ansi.AUTO.string(
          "@|yellow Please provide message content with --text=\"your message\"|@");
      System.out.println(msg);
      return;
    }

    SendMessageDto dto = new SendMessageDto(toUsername, messageText);
    var response = messageService.sendMessage(senderId, dto);

    if (response.success()) {
      String msg = Help.Ansi.AUTO.string("@|green " + response.message() + "|@");
      System.out.println(msg);
    } else {
      String msg = Help.Ansi.AUTO.string("@|red " + response.message() + "|@");
      System.out.println(msg);
    }
  }

  /**
   * Views messages from a specific user
   */
  private void viewMessagesFrom(int receiverId) {
    List<ConversationMessageDto> messages = messageService.getMessagesFrom(receiverId, fromUsername);

    if (messages.isEmpty()) {
      String msg = Help.Ansi.AUTO.string(
          "@|yellow No messages from " + fromUsername + "|@");
      System.out.println(msg);
      return;
    }

    System.out.println(Help.Ansi.AUTO.string(
        "\n@|bold,cyan === Messages from " + fromUsername + " ===|@\n"));

    displayMessages(messages);
  }

  /**
   * Views full conversation with a user
   */
  private void viewConversation(int currentUserId) {
    List<ConversationMessageDto> messages = messageService.getConversation(
        currentUserId,
        conversationUsername);

    if (messages.isEmpty()) {
      String msg = Help.Ansi.AUTO.string(
          "@|yellow No conversation with " + conversationUsername + "|@");
      System.out.println(msg);
      return;
    }

    System.out.println(Help.Ansi.AUTO.string(
        "\n@|bold,cyan === Conversation with " + conversationUsername + " ===|@\n"));

    displayConversation(messages);
  }

  /**
   * Views inbox (all received messages)
   */
  private void viewInbox(int userId) {
    List<ConversationMessageDto> messages = messageService.getReceivedMessages(userId);
    long unreadCount = messageService.getUnreadCount(userId);

    if (messages.isEmpty()) {
      String msg = Help.Ansi.AUTO.string("@|yellow Your inbox is empty.|@");
      System.out.println(msg);
      return;
    }

    System.out.println(Help.Ansi.AUTO.string(
        "\n@|bold,cyan === Inbox (" + messages.size() + " messages, " +
            unreadCount + " unread) ===|@\n"));

    displayMessages(messages);
  }

  /**
   * Views only unread messages
   */
  private void viewUnreadMessages(int userId) {
    List<ConversationMessageDto> messages = messageService.getUnreadMessages(userId);

    if (messages.isEmpty()) {
      String msg = Help.Ansi.AUTO.string("@|green No unread messages!|@");
      System.out.println(msg);
      return;
    }

    System.out.println(Help.Ansi.AUTO.string(
        "\n@|bold,yellow === Unread Messages (" + messages.size() + ") ===|@\n"));

    displayMessages(messages);
  }

  /**
   * Displays a list of messages
   */
  private void displayMessages(List<ConversationMessageDto> messages) {
    for (int i = 0; i < messages.size(); i++) {
      ConversationMessageDto msg = messages.get(i);

      String readIndicator = msg.wasRead() ? "✓" : "○";
      String readColor = msg.wasRead() ? "green" : "yellow";

      System.out.println(Help.Ansi.AUTO.string(String.format(
          "@|bold %d.|@ @|blue From: %s|@ @|%s [%s]|@",
          i + 1,
          msg.otherUsername(),
          readColor,
          readIndicator)));

      System.out.println(Help.Ansi.AUTO.string(
          "    @|faint " + msg.createdAt().toString().substring(0, 16) + "|@"));

      System.out.println("    " + msg.content());
      System.out.println();
    }
  }

  /**
   * Displays a conversation (with sent/received indicators)
   */
  private void displayConversation(List<ConversationMessageDto> messages) {
    for (ConversationMessageDto msg : messages) {
      String direction = msg.isSent() ? "→" : "←";
      String color = msg.isSent() ? "green" : "blue";
      String prefix = msg.isSent() ? "You" : msg.otherUsername();

      System.out.println(Help.Ansi.AUTO.string(String.format(
          "@|%s %s %s|@ @|faint [%s]|@",
          color,
          direction,
          prefix,
          msg.createdAt().toString().substring(0, 16))));

      System.out.println("  " + msg.content());
      System.out.println();
    }
  }
}