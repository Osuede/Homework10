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
    private final User omar = new User("Omar", server);
    private final User kyhle = new User("Kyhle", server);
    private final User ghina = new User("Ghina", server);

    @BeforeEach
    void registerEveryone() {
        server.registerUser(omar);
        server.registerUser(kyhle);
        server.registerUser(ghina);
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
        server.unregisterUser(kyhle);
        assertFalse(server.isRegistered(kyhle));
    }

    @Test
    void registeringTwiceStillDeliversOnlyOnce() {
        server.registerUser(kyhle);
        server.sendMessage(new Message(omar, List.of(kyhle), "Hi"));
        assertEquals(1, kyhle.getChatHistory().getMessages().size());
    }

    @Test
    void deliversToOneRecipient() {
        Message message = new Message(omar, List.of(kyhle), "Hi Kyhle");
        server.sendMessage(message);
        assertEquals(List.of(message), kyhle.getChatHistory().getMessages());
        assertTrue(ghina.getChatHistory().getMessages().isEmpty(), "Ghina was not a recipient");
    }

    @Test
    void deliversToSeveralRecipients() {
        Message message = new Message(omar, List.of(kyhle, ghina), "Hi both");
        server.sendMessage(message);
        assertEquals(List.of(message), kyhle.getChatHistory().getMessages());
        assertEquals(List.of(message), ghina.getChatHistory().getMessages());
    }

    @Test
    void skipsRecipientsWhoAreNotRegistered() {
        server.unregisterUser(ghina);
        server.sendMessage(new Message(omar, List.of(kyhle, ghina), "Hi both"));
        assertEquals(1, kyhle.getChatHistory().getMessages().size());
        assertTrue(ghina.getChatHistory().getMessages().isEmpty(), "Ghina is not registered");
    }

    @Test
    void senderMustBeRegistered() {
        server.unregisterUser(omar);
        Message message = new Message(omar, List.of(kyhle), "Hi");
        assertThrows(IllegalStateException.class, () -> server.sendMessage(message));
        assertTrue(kyhle.getChatHistory().getMessages().isEmpty());
    }

    @Test
    void doesNotDeliverToSomeoneWhoBlockedTheSender() {
        server.blockUser(kyhle, omar);
        server.sendMessage(new Message(omar, List.of(kyhle, ghina), "Hi both"));
        assertTrue(kyhle.getChatHistory().getMessages().isEmpty(), "Kyhle blocked Omar");
        assertEquals(1, ghina.getChatHistory().getMessages().size(), "Ghina did not block Omar");
    }

    @Test
    void blockingOnlyWorksOneWay() {
        server.blockUser(kyhle, omar);
        server.sendMessage(new Message(kyhle, List.of(omar), "Hi Omar"));
        assertEquals(1, omar.getChatHistory().getMessages().size(), "Omar never blocked Kyhle");
    }

    @Test
    void unblockUserDeliversAgain() {
        server.blockUser(kyhle, omar);
        server.unblockUser(kyhle, omar);
        server.sendMessage(new Message(omar, List.of(kyhle), "Hi"));
        assertEquals(1, kyhle.getChatHistory().getMessages().size());
    }

    @Test
    void isBlockedOnlyReportsActualBlocks() {
        assertFalse(server.isBlocked(kyhle, omar));
        server.blockUser(kyhle, omar);
        assertTrue(server.isBlocked(kyhle, omar));
        assertFalse(server.isBlocked(omar, kyhle), "blocking is one way");
        assertFalse(server.isBlocked(kyhle, ghina), "Kyhle only blocked Omar");
    }

    @Test
    void unblockingSomeoneWhoWasNeverBlockedDoesNothing() {
        server.unblockUser(kyhle, omar);
        assertFalse(server.isBlocked(kyhle, omar));
    }

    @Test
    void undoMessageRemovesItFromEveryRecipient() {
        Message message = new Message(omar, List.of(kyhle, ghina), "oops");
        server.sendMessage(message);
        server.undoMessage(omar, message.createMemento());
        assertTrue(kyhle.getChatHistory().getMessages().isEmpty());
        assertTrue(ghina.getChatHistory().getMessages().isEmpty());
    }

    @Test
    void undoMessageLeavesOtherMessagesAlone() {
        Message keep = new Message(omar, List.of(kyhle), "keep me");
        Message oops = new Message(omar, List.of(kyhle), "oops");
        server.sendMessage(keep);
        server.sendMessage(oops);
        server.undoMessage(omar, oops.createMemento());
        assertEquals(List.of(keep), kyhle.getChatHistory().getMessages());
    }

    @Test
    void undoMessageNeedsARegisteredSender() {
        Message message = new Message(omar, List.of(kyhle), "oops");
        server.sendMessage(message);
        server.unregisterUser(omar);
        assertThrows(IllegalStateException.class, () -> server.undoMessage(omar, message.createMemento()));
        assertEquals(1, kyhle.getChatHistory().getMessages().size(), "Kyhle should still have the message");
    }
}
