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
    private final User omar = new User("Omar", server);
    private final User kyhle = new User("Kyhle", server);
    private final User ghina = new User("Ghina", server);
    private final LocalDateTime time = LocalDateTime.of(2026, 11, 16, 14, 5, 9);

    @Test
    void storesSenderRecipientsTimestampAndContent() {
        Message message = new Message(omar, List.of(kyhle, ghina), "Hello!", time);
        assertSame(omar, message.getSender());
        assertEquals(List.of(kyhle, ghina), message.getRecipients());
        assertEquals(time, message.getTimestamp());
        assertEquals("Hello!", message.getContent());
    }

    @Test
    void timestampDefaultsToNow() {
        LocalDateTime before = LocalDateTime.now();
        Message message = new Message(omar, List.of(kyhle), "Hi");
        LocalDateTime after = LocalDateTime.now();
        assertFalse(message.getTimestamp().isBefore(before), "timestamp should not be earlier than when the message was made");
        assertFalse(message.getTimestamp().isAfter(after), "timestamp should not be later than when the message was made");
    }

    @Test
    void changingTheOriginalListDoesNotChangeTheMessage() {
        List<User> recipients = new ArrayList<>(List.of(kyhle));
        Message message = new Message(omar, recipients, "Hi", time);
        recipients.add(ghina);
        assertEquals(List.of(kyhle), message.getRecipients(), "the message should keep its own copy of the recipients");
    }

    @Test
    void recipientsCannotBeChangedThroughTheGetter() {
        Message message = new Message(omar, List.of(kyhle), "Hi", time);
        assertThrows(UnsupportedOperationException.class, () -> message.getRecipients().add(ghina));
    }

    @Test
    void duplicateRecipientsAreOnlyKeptOnce() {
        Message message = new Message(omar, List.of(kyhle, ghina, kyhle), "Hi", time);
        assertEquals(List.of(kyhle, ghina), message.getRecipients());
    }

    @Test
    void needsAtLeastOneRecipient() {
        assertThrows(IllegalArgumentException.class, () -> new Message(omar, List.of(), "Hi", time));
    }

    @Test
    void cannotBeSentToTheSender() {
        assertThrows(IllegalArgumentException.class, () -> new Message(omar, List.of(kyhle, omar), "Hi", time));
    }

    @Test
    void toStringShowsTimeSenderRecipientsAndContent() {
        Message message = new Message(omar, List.of(kyhle, ghina), "Hello!", time);
        assertEquals("[14:05:09] Omar -> Kyhle, Ghina: Hello!", message.toString());
    }

    @Test
    void createMementoCopiesContentAndTimestamp() {
        Message message = new Message(omar, List.of(kyhle), "Hello!", time);
        MessageMemento memento = message.createMemento();
        assertEquals("Hello!", memento.getContent());
        assertEquals(time, memento.getTimestamp());
    }

    @Test
    void matchesItsOwnMemento() {
        Message message = new Message(omar, List.of(kyhle), "Hello!", time);
        assertTrue(message.matches(message.createMemento()));
    }

    @Test
    void doesNotMatchAMementoWithDifferentContentOrTime() {
        Message message = new Message(omar, List.of(kyhle), "Hello!", time);
        Message otherContent = new Message(omar, List.of(kyhle), "Bye!", time);
        Message otherTime = new Message(omar, List.of(kyhle), "Hello!", time.plusSeconds(1));
        assertFalse(message.matches(otherContent.createMemento()));
        assertFalse(message.matches(otherTime.createMemento()));
    }
}
