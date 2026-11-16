package chatapp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class MessageMementoTest {

    private final LocalDateTime time = LocalDateTime.of(2026, 11, 16, 14, 5, 9);

    @Test
    void storesContentAndTimestamp() {
        MessageMemento memento = new MessageMemento("Hello!", time);
        assertEquals("Hello!", memento.getContent());
        assertEquals(time, memento.getTimestamp());
    }

    @Test
    void toStringShowsContentAndTime() {
        MessageMemento memento = new MessageMemento("Hello!", time);
        assertEquals("\"Hello!\" (sent at 14:05:09)", memento.toString());
    }
}
