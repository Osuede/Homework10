package chatapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatServer {
    private final List<User> users = new ArrayList<>();
    // blocker -> the senders they have blocked
    private final Map<User, Set<User>> blockLists = new HashMap<>();

    public void registerUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
            log(user.getName() + " registered");
        }
    }

    public void unregisterUser(User user) {
        if (users.remove(user)) {
            log(user.getName() + " unregistered");
        }
    }

    public boolean isRegistered(User user) {
        return users.contains(user);
    }

    public void sendMessage(Message message) {
        User sender = message.getSender();
        if (!isRegistered(sender)) {
            throw new IllegalStateException(sender.getName() + " is not registered with the server");
        }
        for (User recipient : message.getRecipients()) {
            String delivery = sender.getName() + " -> " + recipient.getName() + ": \"" + message.getContent() + "\" ";
            if (!isRegistered(recipient)) {
                log(delivery + "NOT DELIVERED (" + recipient.getName() + " is not registered)");
            } else if (isBlocked(recipient, sender)) {
                log(delivery + "BLOCKED (" + recipient.getName() + " blocked " + sender.getName() + ")");
            } else {
                recipient.receiveMessage(message);
                log(delivery + "delivered");
            }
        }
    }

    /**
     * After this, {@code blocker} no longer receives messages from {@code blocked}.
     */
    public void blockUser(User blocker, User blocked) {
        Set<User> blockList = blockLists.get(blocker);
        if (blockList == null) {
            blockList = new HashSet<>();
            blockLists.put(blocker, blockList);
        }
        blockList.add(blocked);
        log(blocker.getName() + " blocked " + blocked.getName());
    }

    public void unblockUser(User blocker, User blocked) {
        Set<User> blockList = blockLists.get(blocker);
        if (blockList != null && blockList.remove(blocked)) {
            log(blocker.getName() + " unblocked " + blocked.getName());
        }
    }

    public boolean isBlocked(User recipient, User sender) {
        Set<User> blockList = blockLists.get(recipient);
        return blockList != null && blockList.contains(sender);
    }

    /**
     * Tells every other registered user to remove the message the snapshot
     * describes.
     */
    public void undoMessage(User sender, MessageMemento memento) {
        // TODO (Step 3)
        throw new UnsupportedOperationException("TODO: Step 3");
    }

    private void log(String text) {
        System.out.println("[Server] " + text);
    }
}
