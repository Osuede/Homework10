package chatapp;

/**
 * Demo with 3 users (Omar, Kyhle, Ghina) that shows every required feature.
 */
public class ChatDriver {
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        User omar = new User("Omar", server);
        User kyhle = new User("Kyhle", server);
        User ghina = new User("Ghina", server);

        printSection("Register users");
        server.registerUser(omar);
        server.registerUser(kyhle);
        server.registerUser(ghina);

        printSection("Send messages to one or more users");
        omar.sendMessage("Hey Kyhle, are you coming to the study group?", kyhle);
        kyhle.sendMessage("Yes! See you at 5.", omar);
        omar.sendMessage("Study group is in room 210.", kyhle, ghina);
        ghina.sendMessage("Thanks, I'll bring snacks.", omar, kyhle);

        printSection("Undo the last message sent (Memento)");
        omar.sendMessage("Actually, let's meet in room 999.", kyhle, ghina);
        System.out.println("Omar's last message sent: " + omar.getChatHistory().getLastMessageSent());
        ghina.viewChatHistory();
        omar.undoLastMessage();
        ghina.viewChatHistory();
        System.out.println("Omar's last message sent is now: " + omar.getChatHistory().getLastMessageSent());

        printSection("Block a user (Mediator)");
        ghina.blockUser(kyhle);
        kyhle.sendMessage("Who's bringing the whiteboard markers?", omar, ghina);
        ghina.unblockUser(kyhle);
        kyhle.sendMessage("Ghina, can you send me the notes?", ghina);

        printSection("Unregister a user");
        server.unregisterUser(ghina);
        omar.sendMessage("Good night everyone!", kyhle, ghina);

        printSection("Chat history of each user");
        omar.viewChatHistory();
        kyhle.viewChatHistory();
        ghina.viewChatHistory();

        // TODO (Step 5): iterating over a user's history by a specific user (Iterator)
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("===== " + title + " =====");
    }
}
