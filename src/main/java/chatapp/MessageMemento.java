package chatapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageMemento {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String content;
    private final LocalDateTime timestamp;

    // Package-private: only classes in chatapp (in practice, Message.createMemento)
    // make snapshots.
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

    @Override
    public String toString() {
        return "\"" + content + "\" (sent at " + timestamp.format(TIME_FORMAT) + ")";
    }
}
