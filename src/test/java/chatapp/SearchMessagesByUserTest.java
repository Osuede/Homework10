package chatapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

class SearchMessagesByUserTest {

    private final ChatServer server = new ChatServer();
    private final User omar = new User("Omar", server);
    private final User kyhle = new User("Kyhle", server);
    private final User ghina = new User("Ghina", server);

    private final Message omarToKyhle = new Message(omar, List.of(kyhle), "Hi Kyhle");
    private final Message kyhleToOmar = new Message(kyhle, List.of(omar), "Hi Omar");
    private final Message omarToBoth = new Message(omar, List.of(kyhle, ghina), "Hi both");
    private final Message ghinaToOmar = new Message(ghina, List.of(omar), "Hi from Ghina");
    private final List<Message> messages = List.of(omarToKyhle, kyhleToOmar, omarToBoth, ghinaToOmar);

    @Test
    void findsMessagesTheUserSent() {
        Iterator<Message> iterator = new SearchMessagesByUser(List.of(kyhleToOmar, ghinaToOmar), ghina);
        assertEquals(List.of(ghinaToOmar), collect(iterator));
    }

    @Test
    void findsMessagesTheUserReceived() {
        Iterator<Message> iterator = new SearchMessagesByUser(List.of(omarToKyhle, omarToBoth), ghina);
        assertEquals(List.of(omarToBoth), collect(iterator), "Ghina is one of the recipients of the group message");
    }

    @Test
    void findsEveryMatchingMessageInOrder() {
        Iterator<Message> iterator = new SearchMessagesByUser(messages, kyhle);
        assertEquals(List.of(omarToKyhle, kyhleToOmar, omarToBoth), collect(iterator));
    }

    @Test
    void hasNoNextWhenNoMessageInvolvesTheUser() {
        Iterator<Message> iterator = new SearchMessagesByUser(List.of(omarToKyhle, kyhleToOmar), ghina);
        assertFalse(iterator.hasNext());
    }

    @Test
    void hasNoNextForAnEmptyList() {
        Iterator<Message> iterator = new SearchMessagesByUser(List.of(), omar);
        assertFalse(iterator.hasNext());
    }

    @Test
    void callingHasNextManyTimesDoesNotSkipMessages() {
        Iterator<Message> iterator = new SearchMessagesByUser(messages, ghina);
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertSame(omarToBoth, iterator.next());
        assertSame(ghinaToOmar, iterator.next());
    }

    @Test
    void nextWorksWithoutCallingHasNextFirst() {
        Iterator<Message> iterator = new SearchMessagesByUser(messages, ghina);
        assertSame(omarToBoth, iterator.next());
        assertSame(ghinaToOmar, iterator.next());
    }

    @Test
    void nextThrowsWhenThereAreNoMoreMessages() {
        Iterator<Message> iterator = new SearchMessagesByUser(messages, ghina);
        iterator.next();
        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    void cannotRemoveMessagesThroughTheIterator() {
        Iterator<Message> iterator = new SearchMessagesByUser(messages, kyhle);
        iterator.next();
        assertThrows(UnsupportedOperationException.class, () -> iterator.remove());
    }

    private static List<Message> collect(Iterator<Message> iterator) {
        List<Message> found = new ArrayList<>();
        while (iterator.hasNext()) {
            found.add(iterator.next());
        }
        return found;
    }
}
