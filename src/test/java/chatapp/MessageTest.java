package chatapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class MessageTest {

    private final ChatServer server = new ChatServer();
    private final User alice = new User("Alice", server);
    private final User bob = new User("Bob", server);
    private final User charlie = new User("Charlie", server);
    private final LocalDateTime time = LocalDateTime.of(2026, 11, 16, 14, 5, 9);

    @Test
    void storesSenderRecipientsTimestampAndContent() {
        Message message = new Message(alice, List.of(bob, charlie), "Hello!", time);
        assertSame(alice, message.getSender());
        assertEquals(List.of(bob, charlie), message.getRecipients());
        assertEquals(time, message.getTimestamp());
        assertEquals("Hello!", message.getContent());
    }

    @Test
    void timestampDefaultsToNow() {
        LocalDateTime before = LocalDateTime.now();
        Message message = new Message(alice, List.of(bob), "Hi");
        LocalDateTime after = LocalDateTime.now();
        assertFalse(message.getTimestamp().isBefore(before), "timestamp should not be earlier than when the message was made");
        assertFalse(message.getTimestamp().isAfter(after), "timestamp should not be later than when the message was made");
    }

    @Test
    void changingTheOriginalListDoesNotChangeTheMessage() {
        List<User> recipients = new ArrayList<>(List.of(bob));
        Message message = new Message(alice, recipients, "Hi", time);
        recipients.add(charlie);
        assertEquals(List.of(bob), message.getRecipients(), "the message should keep its own copy of the recipients");
    }

    @Test
    void recipientsCannotBeChangedThroughTheGetter() {
        Message message = new Message(alice, List.of(bob), "Hi", time);
        assertThrows(UnsupportedOperationException.class, () -> message.getRecipients().add(charlie));
    }

    @Test
    void duplicateRecipientsAreOnlyKeptOnce() {
        Message message = new Message(alice, List.of(bob, charlie, bob), "Hi", time);
        assertEquals(List.of(bob, charlie), message.getRecipients());
    }

    @Test
    void needsAtLeastOneRecipient() {
        assertThrows(IllegalArgumentException.class, () -> new Message(alice, List.of(), "Hi", time));
    }

    @Test
    void cannotBeSentToTheSender() {
        assertThrows(IllegalArgumentException.class, () -> new Message(alice, List.of(bob, alice), "Hi", time));
    }

    @Test
    void toStringShowsTimeSenderRecipientsAndContent() {
        Message message = new Message(alice, List.of(bob, charlie), "Hello!", time);
        assertEquals("[14:05:09] Alice -> Bob, Charlie: Hello!", message.toString());
    }

    @Test
    void createMementoCopiesContentAndTimestamp() {
        Message message = new Message(alice, List.of(bob), "Hello!", time);
        MessageMemento memento = message.createMemento();
        assertEquals("Hello!", memento.getContent());
        assertEquals(time, memento.getTimestamp());
    }

    @Test
    void matchesItsOwnMemento() {
        Message message = new Message(alice, List.of(bob), "Hello!", time);
        assertTrue(message.matches(message.createMemento()));
    }

    @Test
    void doesNotMatchAMementoWithDifferentContentOrTime() {
        Message message = new Message(alice, List.of(bob), "Hello!", time);
        Message otherContent = new Message(alice, List.of(bob), "Bye!", time);
        Message otherTime = new Message(alice, List.of(bob), "Hello!", time.plusSeconds(1));
        assertFalse(message.matches(otherContent.createMemento()));
        assertFalse(message.matches(otherTime.createMemento()));
    }
}
