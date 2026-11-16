package chatapp;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * One user's chat history: every message they sent or received, oldest first.
 *
 * <p>Memento pattern role: CARETAKER. It also keeps a stack of snapshots
 * ({@link MessageMemento}) of the messages its user sent, newest on top.
 * It stores the snapshots and hands back the newest one for undo, but it
 * never changes them.
 */
public class ChatHistory {
    private final List<Message> messages = new ArrayList<>();
    private final Deque<MessageMemento> sentMementos = new ArrayDeque<>();

    /** Adds a message (sent or received) to the end of the history. */
    public void addMessage(Message message) {
        // TODO (Step 1)
        throw new UnsupportedOperationException("TODO: Step 1");
    }

    /** Read-only view of every message, oldest first. */
    public List<Message> getMessages() {
        // TODO (Step 1)
        throw new UnsupportedOperationException("TODO: Step 1");
    }

    /** Caretaker: saves the snapshot of a message this user just sent. */
    public void saveMemento(MessageMemento memento) {
        // TODO (Step 3)
        throw new UnsupportedOperationException("TODO: Step 3");
    }

    /** Returns the snapshot of the last message sent without removing it, or null if there is none. */
    public MessageMemento getLastMessageSent() {
        // TODO (Step 3)
        throw new UnsupportedOperationException("TODO: Step 3");
    }

    /** Removes and returns the snapshot of the last message sent, or null if there is none. */
    public MessageMemento popLastMessageSent() {
        // TODO (Step 3)
        throw new UnsupportedOperationException("TODO: Step 3");
    }

    /**
     * Removes the newest message from {@code sender} that matches the snapshot.
     *
     * @return true if a message was removed
     */
    public boolean removeMessage(User sender, MessageMemento memento) {
        // TODO (Step 3)
        throw new UnsupportedOperationException("TODO: Step 3");
    }
}
