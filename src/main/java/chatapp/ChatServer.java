package chatapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mediator pattern role: MEDIATOR.
 *
 * <p>The central chat server. Users never talk to each other directly. They hand
 * everything to the server, and the server decides what happens:
 * <ul>
 *   <li>who is registered and allowed to send and receive</li>
 *   <li>who has blocked whom</li>
 *   <li>delivering a message to one or more recipients</li>
 *   <li>removing a message from everyone when its sender undoes it</li>
 * </ul>
 */
public class ChatServer {
    private final List<User> users = new ArrayList<>();
    // blocker -> the senders they have blocked
    private final Map<User, Set<User>> blockedSenders = new HashMap<>();

    public void registerUser(User user) {
        // TODO (Step 2)
        throw new UnsupportedOperationException("TODO: Step 2");
    }

    public void unregisterUser(User user) {
        // TODO (Step 2)
        throw new UnsupportedOperationException("TODO: Step 2");
    }

    public boolean isRegistered(User user) {
        // TODO (Step 2)
        throw new UnsupportedOperationException("TODO: Step 2");
    }

    /**
     * Delivers the message to each recipient, skipping anyone who is not registered
     * or who has blocked the sender.
     *
     * @throws IllegalStateException if the sender is not registered
     */
    public void sendMessage(Message message) {
        // TODO (Step 2)
        throw new UnsupportedOperationException("TODO: Step 2");
    }

    /** After this, {@code blocker} no longer receives messages from {@code blocked}. */
    public void blockUser(User blocker, User blocked) {
        // TODO (Step 2)
        throw new UnsupportedOperationException("TODO: Step 2");
    }

    public void unblockUser(User blocker, User blocked) {
        // TODO (Step 2)
        throw new UnsupportedOperationException("TODO: Step 2");
    }

    public boolean isBlocked(User recipient, User sender) {
        // TODO (Step 2)
        throw new UnsupportedOperationException("TODO: Step 2");
    }

    /** Tells every other registered user to remove the message the snapshot describes. */
    public void undoMessage(User sender, MessageMemento memento) {
        // TODO (Step 3)
        throw new UnsupportedOperationException("TODO: Step 3");
    }
}
