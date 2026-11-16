package chatapp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * One chat message: who sent it, who it is for, when it was sent, and what it says.
 *
 * <p>Memento pattern role: ORIGINATOR. A message can take a snapshot of itself
 * ({@link #createMemento()}) and check whether a snapshot was taken from it
 * ({@link #matches(MessageMemento)}).
 */
public class Message {
    private final User sender;
    private final List<User> recipients;
    private final LocalDateTime timestamp;
    private final String content;

    /** Creates a message stamped with the current time. */
    public Message(User sender, List<User> recipients, String content) {
        this(sender, recipients, content, LocalDateTime.now());
    }

    /** Creates a message with a specific timestamp (handy in tests). */
    public Message(User sender, List<User> recipients, String content, LocalDateTime timestamp) {
        // TODO (Step 1): check the arguments and keep an unmodifiable copy of the recipients
        this.sender = sender;
        this.recipients = recipients;
        this.content = content;
        this.timestamp = timestamp;
    }

    public User getSender() {
        return sender;
    }

    public List<User> getRecipients() {
        return recipients;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    /** Memento: takes a snapshot of this message's content and timestamp. */
    public MessageMemento createMemento() {
        // TODO (Step 3)
        throw new UnsupportedOperationException("TODO: Step 3");
    }

    /** Memento: true if the snapshot was taken from a message with this content and timestamp. */
    public boolean matches(MessageMemento memento) {
        // TODO (Step 3)
        throw new UnsupportedOperationException("TODO: Step 3");
    }

    // TODO (Step 1): toString() that prints like "[14:05:09] Alice -> Bob, Charlie: Hello!"
}
