package chatapp;

import java.util.Arrays;
import java.util.List;

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

    public Message sendMessage(String content, User... recipients) {
        Message message = new Message(this, Arrays.asList(recipients), content);
        chatServer.sendMessage(message);
        chatHistory.addMessage(message);
        chatHistory.saveMemento(message.createMemento());
        return message;
    }

    /** Called by the server when someone sends this user a message. */
    public void receiveMessage(Message message) {
        chatHistory.addMessage(message);
    }

    public MessageMemento undoLastMessage() {
        MessageMemento memento = chatHistory.getLastMessageSent();
        if (memento == null) {
            return null;
        }
        chatServer.undoMessage(this, memento);
        chatHistory.popLastMessageSent();
        chatHistory.removeMessage(this, memento);
        return memento;
    }

    /** Called by the server when another user undoes a message they sent. */
    public void receiveUndo(User sender, MessageMemento memento) {
        chatHistory.removeMessage(sender, memento);
    }

    /** Stops receiving messages from {@code user}. The server enforces it. */
    public void blockUser(User user) {
        chatServer.blockUser(this, user);
    }

    public void unblockUser(User user) {
        chatServer.unblockUser(this, user);
    }

    /** Prints this user's whole chat history. */
    public void viewChatHistory() {
        System.out.println(name + "'s chat history:");
        List<Message> messages = chatHistory.getMessages();
        if (messages.isEmpty()) {
            System.out.println("  (no messages)");
        }
        for (Message message : messages) {
            System.out.println("  " + message);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
