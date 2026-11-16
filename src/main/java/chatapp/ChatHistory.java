package chatapp;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class ChatHistory {
    private final List<Message> messages = new ArrayList<>();
    private final Deque<MessageMemento> sentMementos = new ArrayDeque<>();

    /** Adds a message (sent or received) to the end of the history. */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /** Read-only view of every message, oldest first. */
    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    /** Caretaker: saves the snapshot of a message this user just sent. */
    public void saveMemento(MessageMemento memento) {
        sentMementos.push(memento);
    }

    /**
     * Returns the snapshot of the last message sent without removing it, or null if
     * there is none.
     */
    public MessageMemento getLastMessageSent() {
        return sentMementos.peek();
    }

    /**
     * Removes and returns the snapshot of the last message sent, or null if there
     * is none.
     */
    public MessageMemento popLastMessageSent() {
        if (sentMementos.isEmpty()) {
            return null;
        }
        return sentMementos.pop();
    }

    public boolean removeMessage(User sender, MessageMemento memento) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message message = messages.get(i);
            if (message.getSender().equals(sender) && message.matches(memento)) {
                messages.remove(i);
                return true;
            }
        }
        return false;
    }
}
