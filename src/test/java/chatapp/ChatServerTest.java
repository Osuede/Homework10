package chatapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatServerTest {

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
    void registerUserMakesTheUserRegistered() {
        User dave = new User("Dave", server);
        assertFalse(server.isRegistered(dave));
        server.registerUser(dave);
        assertTrue(server.isRegistered(dave));
    }

    @Test
    void unregisterUserMakesTheUserUnregistered() {
        server.unregisterUser(bob);
        assertFalse(server.isRegistered(bob));
    }

    @Test
    void registeringTwiceStillDeliversOnlyOnce() {
        server.registerUser(bob);
        server.sendMessage(new Message(alice, List.of(bob), "Hi"));
        assertEquals(1, bob.getChatHistory().getMessages().size());
    }

    @Test
    void deliversToOneRecipient() {
        Message message = new Message(alice, List.of(bob), "Hi Bob");
        server.sendMessage(message);
        assertEquals(List.of(message), bob.getChatHistory().getMessages());
        assertTrue(charlie.getChatHistory().getMessages().isEmpty(), "Charlie was not a recipient");
    }

    @Test
    void deliversToSeveralRecipients() {
        Message message = new Message(alice, List.of(bob, charlie), "Hi both");
        server.sendMessage(message);
        assertEquals(List.of(message), bob.getChatHistory().getMessages());
        assertEquals(List.of(message), charlie.getChatHistory().getMessages());
    }

    @Test
    void skipsRecipientsWhoAreNotRegistered() {
        server.unregisterUser(charlie);
        server.sendMessage(new Message(alice, List.of(bob, charlie), "Hi both"));
        assertEquals(1, bob.getChatHistory().getMessages().size());
        assertTrue(charlie.getChatHistory().getMessages().isEmpty(), "Charlie is not registered");
    }

    @Test
    void senderMustBeRegistered() {
        server.unregisterUser(alice);
        Message message = new Message(alice, List.of(bob), "Hi");
        assertThrows(IllegalStateException.class, () -> server.sendMessage(message));
        assertTrue(bob.getChatHistory().getMessages().isEmpty());
    }

    @Test
    void doesNotDeliverToSomeoneWhoBlockedTheSender() {
        server.blockUser(bob, alice);
        server.sendMessage(new Message(alice, List.of(bob, charlie), "Hi both"));
        assertTrue(bob.getChatHistory().getMessages().isEmpty(), "Bob blocked Alice");
        assertEquals(1, charlie.getChatHistory().getMessages().size(), "Charlie did not block Alice");
    }

    @Test
    void blockingOnlyWorksOneWay() {
        server.blockUser(bob, alice);
        server.sendMessage(new Message(bob, List.of(alice), "Hi Alice"));
        assertEquals(1, alice.getChatHistory().getMessages().size(), "Alice never blocked Bob");
    }

    @Test
    void unblockUserDeliversAgain() {
        server.blockUser(bob, alice);
        server.unblockUser(bob, alice);
        server.sendMessage(new Message(alice, List.of(bob), "Hi"));
        assertEquals(1, bob.getChatHistory().getMessages().size());
    }

    @Test
    void isBlockedOnlyReportsActualBlocks() {
        assertFalse(server.isBlocked(bob, alice));
        server.blockUser(bob, alice);
        assertTrue(server.isBlocked(bob, alice));
        assertFalse(server.isBlocked(alice, bob), "blocking is one way");
        assertFalse(server.isBlocked(bob, charlie), "Bob only blocked Alice");
    }

    @Test
    void unblockingSomeoneWhoWasNeverBlockedDoesNothing() {
        server.unblockUser(bob, alice);
        assertFalse(server.isBlocked(bob, alice));
    }
}
