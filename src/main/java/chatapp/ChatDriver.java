package chatapp;

import java.util.Iterator;

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

        printSection("Search a chat history by user (Iterator)");
        printMessagesWith(omar, ghina);
        printMessagesWith(ghina, omar);
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("===== " + title + " =====");
    }

    private static void printMessagesWith(User owner, User userToSearchWith) {
        System.out.println("Messages in " + owner.getName() + "'s history sent or received by "
                + userToSearchWith.getName() + ":");
        Iterator<Message> iterator = owner.iterator(userToSearchWith);
        while (iterator.hasNext()) {
            System.out.println("  " + iterator.next());
        }
    }
}
