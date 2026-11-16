package chatapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class ChatHistoryTest {

    private final ChatServer server = new ChatServer();
    private final User omar = new User("Omar", server);
    private final User kyhle = new User("Kyhle", server);
    private final ChatHistory history = new ChatHistory();
    private final LocalDateTime time = LocalDateTime.of(2026, 11, 16, 14, 5, 9);

    @Test
    void startsEmpty() {
        assertTrue(history.getMessages().isEmpty());
    }

    @Test
    void keepsMessagesInTheOrderTheyWereAdded() {
        Message first = new Message(omar, List.of(kyhle), "first");
        Message second = new Message(kyhle, List.of(omar), "second");
        history.addMessage(first);
        history.addMessage(second);
        assertEquals(List.of(first, second), history.getMessages());
    }

    @Test
    void getMessagesCannotBeUsedToChangeTheHistory() {
        history.addMessage(new Message(omar, List.of(kyhle), "hi"));
        assertThrows(UnsupportedOperationException.class, () -> history.getMessages().clear());
        assertEquals(1, history.getMessages().size(), "the history should still have its message");
    }

    @Test
    void hasNoLastMessageSentAtFirst() {
        assertNull(history.getLastMessageSent());
        assertNull(history.popLastMessageSent());
    }

    @Test
    void getLastMessageSentReturnsTheNewestMementoWithoutRemovingIt() {
        MessageMemento first = new Message(omar, List.of(kyhle), "first", time).createMemento();
        MessageMemento second = new Message(omar, List.of(kyhle), "second", time).createMemento();
        history.saveMemento(first);
        history.saveMemento(second);
        assertSame(second, history.getLastMessageSent());
        assertSame(second, history.getLastMessageSent(), "looking at the newest memento should not remove it");
    }

    @Test
    void popLastMessageSentGivesMementosBackNewestFirst() {
        MessageMemento first = new Message(omar, List.of(kyhle), "first", time).createMemento();
        MessageMemento second = new Message(omar, List.of(kyhle), "second", time).createMemento();
        history.saveMemento(first);
        history.saveMemento(second);
        assertSame(second, history.popLastMessageSent());
        assertSame(first, history.popLastMessageSent());
        assertNull(history.popLastMessageSent());
    }

    @Test
    void removeMessageRemovesTheMessageTheMementoCameFrom() {
        Message hello = new Message(omar, List.of(kyhle), "hello", time);
        Message bye = new Message(omar, List.of(kyhle), "bye", time);
        history.addMessage(hello);
        history.addMessage(bye);
        assertTrue(history.removeMessage(omar, hello.createMemento()));
        assertEquals(List.of(bye), history.getMessages());
    }

    @Test
    void removeMessageOnlyRemovesMessagesFromThatSender() {
        Message fromKyhle = new Message(kyhle, List.of(omar), "hi", time);
        Message fromOmar = new Message(omar, List.of(kyhle), "hi", time);
        history.addMessage(fromKyhle);
        assertFalse(history.removeMessage(omar, fromOmar.createMemento()));
        assertEquals(List.of(fromKyhle), history.getMessages());
    }

    @Test
    void removeMessageRemovesOnlyTheNewestCopy() {
        Message older = new Message(omar, List.of(kyhle), "hi", time);
        Message newer = new Message(omar, List.of(kyhle), "hi", time);
        history.addMessage(older);
        history.addMessage(newer);
        assertTrue(history.removeMessage(omar, newer.createMemento()));
        assertEquals(1, history.getMessages().size());
        assertSame(older, history.getMessages().get(0));
    }

    @Test
    void removeMessageReturnsFalseWhenNothingMatches() {
        history.addMessage(new Message(omar, List.of(kyhle), "hi", time));
        MessageMemento other = new Message(omar, List.of(kyhle), "something else", time).createMemento();
        assertFalse(history.removeMessage(omar, other));
        assertEquals(1, history.getMessages().size());
    }
}
