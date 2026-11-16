package chatapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

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
    void nameIsUsedForGetNameAndToString() {
        assertEquals("Omar", omar.getName());
        assertEquals("Omar", omar.toString());
    }

    @Test
    void sendMessageBuildsTheMessage() {
        Message message = omar.sendMessage("Hello!", kyhle, ghina);
        assertSame(omar, message.getSender());
        assertEquals(List.of(kyhle, ghina), message.getRecipients());
        assertEquals("Hello!", message.getContent());
    }

    @Test
    void sentMessageIsSavedInTheSendersHistory() {
        Message message = omar.sendMessage("Hello!", kyhle);
        assertEquals(List.of(message), omar.getChatHistory().getMessages());
    }

    @Test
    void sentMessageReachesEveryRecipientThroughTheServer() {
        Message message = omar.sendMessage("Hello!", kyhle, ghina);
        assertEquals(List.of(message), kyhle.getChatHistory().getMessages());
        assertEquals(List.of(message), ghina.getChatHistory().getMessages());
    }

    @Test
    void receiveMessageAddsItToTheHistory() {
        Message message = new Message(omar, List.of(kyhle), "Hi");
        kyhle.receiveMessage(message);
        assertEquals(List.of(message), kyhle.getChatHistory().getMessages());
    }

    @Test
    void historyHasSentAndReceivedMessagesInOrder() {
        Message first = omar.sendMessage("Hi Kyhle", kyhle);
        Message second = kyhle.sendMessage("Hi Omar", omar);
        assertEquals(List.of(first, second), omar.getChatHistory().getMessages());
        assertEquals(List.of(first, second), kyhle.getChatHistory().getMessages());
    }

    @Test
    void sendingToNobodyFails() {
        assertThrows(IllegalArgumentException.class, () -> omar.sendMessage("Hello?"));
    }

    @Test
    void failedSendIsNotSavedInTheSendersHistory() {
        server.unregisterUser(omar);
        assertThrows(IllegalStateException.class, () -> omar.sendMessage("Hi", kyhle));
        assertTrue(omar.getChatHistory().getMessages().isEmpty());
    }

    @Test
    void blockUserStopsMessagesFromThatUser() {
        kyhle.blockUser(omar);
        omar.sendMessage("Hi", kyhle);
        assertTrue(kyhle.getChatHistory().getMessages().isEmpty());
    }

    @Test
    void blockedSenderStillSeesTheirOwnMessage() {
        kyhle.blockUser(omar);
        Message message = omar.sendMessage("Hi", kyhle);
        assertEquals(List.of(message), omar.getChatHistory().getMessages());
    }

    @Test
    void unblockUserLetsMessagesThroughAgain() {
        kyhle.blockUser(omar);
        kyhle.unblockUser(omar);
        Message message = omar.sendMessage("Hi", kyhle);
        assertEquals(List.of(message), kyhle.getChatHistory().getMessages());
    }

    @Test
    void viewChatHistoryPrintsEveryMessage() {
        omar.sendMessage("Hi Kyhle", kyhle);
        kyhle.sendMessage("Hi Omar", omar);
        String output = printedBy(() -> omar.viewChatHistory());
        assertTrue(output.contains("Omar's chat history:"));
        assertTrue(output.contains("Omar -> Kyhle: Hi Kyhle"));
        assertTrue(output.contains("Kyhle -> Omar: Hi Omar"));
    }

    @Test
    void viewChatHistorySaysWhenThereAreNoMessages() {
        String output = printedBy(() -> ghina.viewChatHistory());
        assertTrue(output.contains("(no messages)"));
    }

    @Test
    void sendingAMessageSavesAMementoOfIt() {
        Message message = omar.sendMessage("Hello!", kyhle);
        MessageMemento memento = omar.getChatHistory().getLastMessageSent();
        assertEquals("Hello!", memento.getContent());
        assertEquals(message.getTimestamp(), memento.getTimestamp());
    }

    @Test
    void undoLastMessageRemovesItFromTheSenderAndEveryRecipient() {
        omar.sendMessage("Oops, wrong chat", kyhle, ghina);
        omar.undoLastMessage();
        assertTrue(omar.getChatHistory().getMessages().isEmpty());
        assertTrue(kyhle.getChatHistory().getMessages().isEmpty());
        assertTrue(ghina.getChatHistory().getMessages().isEmpty());
    }

    @Test
    void undoLastMessageReturnsTheMementoThatWasUndone() {
        Message message = omar.sendMessage("Oops", kyhle);
        MessageMemento undone = omar.undoLastMessage();
        assertEquals("Oops", undone.getContent());
        assertEquals(message.getTimestamp(), undone.getTimestamp());
    }

    @Test
    void undoOnlyRemovesTheLastMessage() {
        Message first = omar.sendMessage("First", kyhle);
        omar.sendMessage("Second", kyhle);
        omar.undoLastMessage();
        assertEquals(List.of(first), omar.getChatHistory().getMessages());
        assertEquals(List.of(first), kyhle.getChatHistory().getMessages());
    }

    @Test
    void undoingTwiceRemovesTheLastTwoMessages() {
        omar.sendMessage("First", kyhle);
        omar.sendMessage("Second", kyhle);
        omar.undoLastMessage();
        omar.undoLastMessage();
        assertTrue(omar.getChatHistory().getMessages().isEmpty());
        assertTrue(kyhle.getChatHistory().getMessages().isEmpty());
    }

    @Test
    void undoNeverRemovesMessagesFromOtherUsers() {
        Message fromKyhle = kyhle.sendMessage("Hi Omar", omar);
        omar.sendMessage("Oops", kyhle);
        omar.undoLastMessage();
        assertEquals(List.of(fromKyhle), omar.getChatHistory().getMessages());
        assertEquals(List.of(fromKyhle), kyhle.getChatHistory().getMessages());
    }

    @Test
    void undoWithNothingSentReturnsNull() {
        assertNull(omar.undoLastMessage());
    }

    @Test
    void undoWorksEvenIfARecipientBlockedTheSender() {
        kyhle.blockUser(omar);
        omar.sendMessage("Hi both", kyhle, ghina);
        omar.undoLastMessage();
        assertTrue(omar.getChatHistory().getMessages().isEmpty());
        assertTrue(ghina.getChatHistory().getMessages().isEmpty());
    }

    @Test
    void failedUndoChangesNothing() {
        Message message = omar.sendMessage("Oops", kyhle);
        server.unregisterUser(omar);
        assertThrows(IllegalStateException.class, () -> omar.undoLastMessage());
        assertEquals(List.of(message), omar.getChatHistory().getMessages());
        assertEquals(List.of(message), kyhle.getChatHistory().getMessages());
        assertNotNull(omar.getChatHistory().getLastMessageSent(), "the memento should still be there to try again");
    }

    @Test
    void receiveUndoRemovesThatMessageFromTheHistory() {
        Message message = new Message(omar, List.of(kyhle), "Oops");
        kyhle.receiveMessage(message);
        kyhle.receiveUndo(omar, message.createMemento());
        assertTrue(kyhle.getChatHistory().getMessages().isEmpty());
    }

    @Test
    void iteratorFindsMessagesWithAnotherUserInThisUsersHistory() {
        Message toKyhle = omar.sendMessage("Hi Kyhle", kyhle);
        ghina.sendMessage("Hi Omar", omar);
        Message fromKyhle = kyhle.sendMessage("Hi both", omar, ghina);
        assertEquals(List.of(toKyhle, fromKyhle), collect(omar.iterator(kyhle)));
    }

    @Test
    void iteratorGivesTheSameMessagesAsTheChatHistoryIterator() {
        omar.sendMessage("Hi Kyhle", kyhle);
        ghina.sendMessage("Hi Omar", omar);
        kyhle.sendMessage("Hi both", omar, ghina);
        assertEquals(collect(omar.getChatHistory().iterator(kyhle)), collect(omar.iterator(kyhle)));
    }

    @Test
    void iteratorOnlySearchesThisUsersOwnHistory() {
        ghina.blockUser(kyhle);
        Message message = kyhle.sendMessage("Hi both", omar, ghina);
        assertEquals(List.of(message), collect(omar.iterator(kyhle)));
        assertFalse(ghina.iterator(kyhle).hasNext(), "the message never reached Ghina's history");
    }

    @Test
    void iteratorDoesNotFindUndoneMessages() {
        omar.sendMessage("Oops", kyhle);
        omar.undoLastMessage();
        assertFalse(kyhle.iterator(omar).hasNext());
    }

    private static List<Message> collect(Iterator<Message> iterator) {
        List<Message> found = new ArrayList<>();
        while (iterator.hasNext()) {
            found.add(iterator.next());
        }
        return found;
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
