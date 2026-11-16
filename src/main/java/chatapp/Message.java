package chatapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Message {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

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
        List<User> uniqueRecipients = List.copyOf(new LinkedHashSet<>(recipients));
        if (uniqueRecipients.isEmpty()) {
            throw new IllegalArgumentException("A message needs at least one recipient");
        }
        if (uniqueRecipients.contains(sender)) {
            throw new IllegalArgumentException(sender.getName() + " can't send a message to themselves");
        }
        this.sender = sender;
        this.recipients = uniqueRecipients;
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

    /**
     * Memento: true if the snapshot was taken from a message with this content and
     * timestamp.
     */
    public boolean matches(MessageMemento memento) {
        // TODO (Step 3)
        throw new UnsupportedOperationException("TODO: Step 3");
    }

    @Override
    public String toString() {
        List<String> names = new ArrayList<>();
        for (User recipient : recipients) {
            names.add(recipient.getName());
        }
        return "[" + timestamp.format(TIME_FORMAT) + "] " + sender.getName() + " -> "
                + String.join(", ", names) + ": " + content;
    }
}
