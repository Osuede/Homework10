package chatapp;

/**
 * A person using the chat app.
 *
 * <p>Mediator pattern role: COLLEAGUE. A user only talks to the {@link ChatServer}
 * (the mediator), never directly to another user. Even when Alice sends a message
 * to Bob, she just hands it to the server, and the server decides whether Bob gets it.
 *
 * <p>Memento pattern: every message a user sends is snapshotted into their
 * {@link ChatHistory}, so {@link #undoLastMessage()} can take it back later.
 */
public class User {
    private final String name;
    private final ChatServer chatServer; // the mediator: the only object this user talks to
    private final ChatHistory chatHistory = new ChatHistory();

    public User(String name, ChatServer chatServer) {
        this.name = name;
        this.chatServer = chatServer;
    }

    public String getName() {
        return name;
    }

    public ChatHistory getChatHistory() {
        return chatHistory;
    }

    /**
     * Sends a message to one or more users through the server.
     *
     * @return the message that was sent
     */
    public Message sendMessage(String content, User... recipients) {
        // TODO (Step 2)
        throw new UnsupportedOperationException("TODO: Step 2");
    }

    /** Called by the server when someone sends this user a message. */
    public void receiveMessage(Message message) {
        // TODO (Step 2)
        throw new UnsupportedOperationException("TODO: Step 2");
    }

    /**
     * Undoes the last message this user sent, everywhere it was delivered.
     *
     * @return the snapshot of the undone message, or null if there was nothing to undo
     */
    public MessageMemento undoLastMessage() {
        // TODO (Step 3)
        throw new UnsupportedOperationException("TODO: Step 3");
    }

    /** Called by the server when another user undoes a message they sent. */
    public void receiveUndo(User sender, MessageMemento memento) {
        // TODO (Step 3)
        throw new UnsupportedOperationException("TODO: Step 3");
    }

    /** Stops receiving messages from {@code user}. The server enforces it. */
    public void blockUser(User user) {
        // TODO (Step 2)
        throw new UnsupportedOperationException("TODO: Step 2");
    }

    public void unblockUser(User user) {
        // TODO (Step 2)
        throw new UnsupportedOperationException("TODO: Step 2");
    }

    /** Prints this user's whole chat history. */
    public void viewChatHistory() {
        // TODO (Step 2)
        throw new UnsupportedOperationException("TODO: Step 2");
    }

    @Override
    public String toString() {
        return name;
    }
}
