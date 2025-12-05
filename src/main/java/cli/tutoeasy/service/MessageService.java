package cli.tutoeasy.service;

import cli.tutoeasy.model.dto.*;
import cli.tutoeasy.model.entities.Message;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.repository.ContactRepository;
import cli.tutoeasy.repository.MessageRepository;
import cli.tutoeasy.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling message operations.
 * Provides business logic for sending, receiving, and managing messages.
 */
public class MessageService {

    private final MessageRepository messageRepository;
    private final ContactRepository contactRepository;
    private final NotificationRepository notificationRepository;

    public MessageService(MessageRepository messageRepository, ContactRepository contactRepository, NotificationRepository notificationRepository) {
        this.messageRepository = messageRepository;
        this.contactRepository = contactRepository;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Sends a message to another user
     *
     * @param senderId ID of the sender
     * @param dto      DTO containing receiver username and message content
     * @return ActionResponseDto indicating success or failure
     */
    public ActionResponseDto sendMessage(int senderId, SendMessageDto dto) {
        if (dto.content() == null || dto.content().trim().isEmpty()) {
            return new ActionResponseDto(false, "Message content cannot be empty.");
        }

        if (dto.content().length() > 1000) {
            return new ActionResponseDto(false, "Message is too long (max 1000 characters).");
        }

        User sender = contactRepository.findUserWithCareer(senderId);
        if (sender == null) {
            return new ActionResponseDto(false, "Sender not found.");
        }

        User receiver = contactRepository.findByUsername(dto.receiverUsername());
        if (receiver == null) {
            return new ActionResponseDto(false, "User not found: " + dto.receiverUsername());
        }

        if (sender.getId() == receiver.getId()) {
            return new ActionResponseDto(false, "You cannot send a message to yourself.");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(dto.content().trim());
        message.setCreatedAt(LocalDateTime.now());
        message.setWasRead(false);

        String notificationMessage = String.format(
                "New message from %s",
                sender.getUsername());
        notificationRepository.createNotification(receiver, notificationMessage, "MESSAGE_RECEIVED");

        messageRepository.save(message);

        return new ActionResponseDto(true, "Message sent to " + dto.receiverUsername() + " successfully.");
    }

    /**
     * Gets all received messages (inbox)
     *
     * @param userId ID of the user
     * @return List of received messages
     */
    public List<ConversationMessageDto> getReceivedMessages(int userId) {
        List<Message> messages = messageRepository.findReceivedMessages(userId);

        return messages.stream()
                .map(m -> new ConversationMessageDto(
                        m.getId(),
                        m.getSender().getUsername(),
                        m.getContent(),
                        m.getCreatedAt(),
                        m.isWasRead(),
                        false))
                .collect(Collectors.toList());
    }

    /**
     * Gets messages from a specific user
     *
     * @param receiverId     ID of current user
     * @param senderUsername Username of the sender
     * @return List of messages from that user
     */
    public List<ConversationMessageDto> getMessagesFrom(int receiverId, String senderUsername) {
        User sender = contactRepository.findByUsername(senderUsername);
        if (sender == null) {
            throw new IllegalArgumentException("User not found: " + senderUsername);
        }

        List<Message> messages = messageRepository.findMessagesFrom(receiverId, sender.getId());

        if (!messages.isEmpty()) {
            messageRepository.markAllFromSenderAsRead(receiverId, sender.getId());
        }

        return messages.stream()
                .map(m -> new ConversationMessageDto(
                        m.getId(),
                        m.getSender().getUsername(),
                        m.getContent(),
                        m.getCreatedAt(),
                        true,
                        false))
                .collect(Collectors.toList());
    }

    /**
     * Gets full conversation between current user and another user
     *
     * @param currentUserId ID of current user
     * @param otherUsername Username of the other user
     * @return List of messages in chronological order
     */
    public List<ConversationMessageDto> getConversation(int currentUserId, String otherUsername) {
        User otherUser = contactRepository.findByUsername(otherUsername);
        if (otherUser == null) {
            throw new IllegalArgumentException("User not found: " + otherUsername);
        }

        List<Message> messages = messageRepository.findConversation(currentUserId, otherUser.getId());

        messageRepository.markAllFromSenderAsRead(currentUserId, otherUser.getId());

        return messages.stream()
                .map(m -> {
                    boolean isSent = m.getSender().getId() == currentUserId;
                    String otherName = isSent ? m.getReceiver().getUsername() : m.getSender().getUsername();

                    return new ConversationMessageDto(
                            m.getId(),
                            otherName,
                            m.getContent(),
                            m.getCreatedAt(),
                            m.isWasRead(),
                            isSent);
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets count of unread messages
     *
     * @param userId ID of the user
     * @return Number of unread messages
     */
    public long getUnreadCount(int userId) {
        return messageRepository.countUnreadMessages(userId);
    }

    /**
     * Gets list of unread messages
     *
     * @param userId ID of the user
     * @return List of unread messages
     */
    public List<ConversationMessageDto> getUnreadMessages(int userId) {
        List<Message> messages = messageRepository.findUnreadMessages(userId);

        return messages.stream()
                .map(m -> new ConversationMessageDto(
                        m.getId(),
                        m.getSender().getUsername(),
                        m.getContent(),
                        m.getCreatedAt(),
                        false,
                        false))
                .collect(Collectors.toList());
    }

    /**
     * Marks a specific message as read
     *
     * @param messageId ID of the message
     * @param userId    ID of the user (to verify ownership)
     * @return ActionResponseDto indicating success
     */
    public ActionResponseDto markMessageAsRead(int messageId, int userId) {
        Message message = messageRepository.findById(messageId);

        if (message == null) {
            return new ActionResponseDto(false, "Message not found.");
        }

        if (message.getReceiver().getId() != userId) {
            return new ActionResponseDto(false, "You can only mark your own messages as read.");
        }

        messageRepository.markAsRead(messageId);
        return new ActionResponseDto(true, "Message marked as read.");
    }
}