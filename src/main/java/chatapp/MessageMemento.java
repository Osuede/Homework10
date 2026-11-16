package chatapp;

import java.time.LocalDateTime;

/**
 * Memento pattern role: MEMENTO.
 *
 * <p>A frozen snapshot of a message a user sent: its content and timestamp.
 * The fields are final and there are no setters, so a snapshot can never change
 * after it is taken. {@link ChatHistory} keeps these snapshots so the user can
 * undo the last message they sent.
 */
public class MessageMemento {
    private final String content;
    private final LocalDateTime timestamp;

    // Package-private: only classes in chatapp (in practice, Message.createMemento) make snapshots.
    MessageMemento(String content, LocalDateTime timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // TODO (Step 3): toString() for printing which message was undone
}
