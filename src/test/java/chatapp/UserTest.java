package chatapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

    private final ChatServer server = new ChatServer();
    private final User alice = new User("Alice", server);
    private final User bob = new User("Bob", server);
    private final User charlie = new User("Charlie", server);

    @BeforeEach
    void registerEveryone() {
        server.registerUser(alice);
        server.registerUser(bob);
        server.registerUser(charlie);
    }

    @Test
    void nameIsUsedForGetNameAndToString() {
        assertEquals("Alice", alice.getName());
        assertEquals("Alice", alice.toString());
    }

    @Test
    void sendMessageBuildsTheMessage() {
        Message message = alice.sendMessage("Hello!", bob, charlie);
        assertSame(alice, message.getSender());
        assertEquals(List.of(bob, charlie), message.getRecipients());
        assertEquals("Hello!", message.getContent());
    }

    @Test
    void sentMessageIsSavedInTheSendersHistory() {
        Message message = alice.sendMessage("Hello!", bob);
        assertEquals(List.of(message), alice.getChatHistory().getMessages());
    }

    @Test
    void sentMessageReachesEveryRecipientThroughTheServer() {
        Message message = alice.sendMessage("Hello!", bob, charlie);
        assertEquals(List.of(message), bob.getChatHistory().getMessages());
        assertEquals(List.of(message), charlie.getChatHistory().getMessages());
    }

    @Test
    void receiveMessageAddsItToTheHistory() {
        Message message = new Message(alice, List.of(bob), "Hi");
        bob.receiveMessage(message);
        assertEquals(List.of(message), bob.getChatHistory().getMessages());
    }

    @Test
    void historyHasSentAndReceivedMessagesInOrder() {
        Message first = alice.sendMessage("Hi Bob", bob);
        Message second = bob.sendMessage("Hi Alice", alice);
        assertEquals(List.of(first, second), alice.getChatHistory().getMessages());
        assertEquals(List.of(first, second), bob.getChatHistory().getMessages());
    }

    @Test
    void sendingToNobodyFails() {
        assertThrows(IllegalArgumentException.class, () -> alice.sendMessage("Hello?"));
    }

    @Test
    void failedSendIsNotSavedInTheSendersHistory() {
        server.unregisterUser(alice);
        assertThrows(IllegalStateException.class, () -> alice.sendMessage("Hi", bob));
        assertTrue(alice.getChatHistory().getMessages().isEmpty());
    }

    @Test
    void blockUserStopsMessagesFromThatUser() {
        bob.blockUser(alice);
        alice.sendMessage("Hi", bob);
        assertTrue(bob.getChatHistory().getMessages().isEmpty());
    }

    @Test
    void blockedSenderStillSeesTheirOwnMessage() {
        bob.blockUser(alice);
        Message message = alice.sendMessage("Hi", bob);
        assertEquals(List.of(message), alice.getChatHistory().getMessages());
    }

    @Test
    void unblockUserLetsMessagesThroughAgain() {
        bob.blockUser(alice);
        bob.unblockUser(alice);
        Message message = alice.sendMessage("Hi", bob);
        assertEquals(List.of(message), bob.getChatHistory().getMessages());
    }

    @Test
    void viewChatHistoryPrintsEveryMessage() {
        alice.sendMessage("Hi Bob", bob);
        bob.sendMessage("Hi Alice", alice);
        String output = printedBy(() -> alice.viewChatHistory());
        assertTrue(output.contains("Alice's chat history:"));
        assertTrue(output.contains("Alice -> Bob: Hi Bob"));
        assertTrue(output.contains("Bob -> Alice: Hi Alice"));
    }

    @Test
    void viewChatHistorySaysWhenThereAreNoMessages() {
        String output = printedBy(() -> charlie.viewChatHistory());
        assertTrue(output.contains("(no messages)"));
    }

    private static String printedBy(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return printed.toString();
    }
}
