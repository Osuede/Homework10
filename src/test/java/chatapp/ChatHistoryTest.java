package chatapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class ChatHistoryTest {

    private final ChatServer server = new ChatServer();
    private final User alice = new User("Alice", server);
    private final User bob = new User("Bob", server);
    private final ChatHistory history = new ChatHistory();

    @Test
    void startsEmpty() {
        assertTrue(history.getMessages().isEmpty());
    }

    @Test
    void keepsMessagesInTheOrderTheyWereAdded() {
        Message first = new Message(alice, List.of(bob), "first");
        Message second = new Message(bob, List.of(alice), "second");
        history.addMessage(first);
        history.addMessage(second);
        assertEquals(List.of(first, second), history.getMessages());
    }

    @Test
    void getMessagesCannotBeUsedToChangeTheHistory() {
        history.addMessage(new Message(alice, List.of(bob), "hi"));
        assertThrows(UnsupportedOperationException.class, () -> history.getMessages().clear());
        assertEquals(1, history.getMessages().size(), "the history should still have its message");
    }
}
