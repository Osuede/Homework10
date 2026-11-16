package chatapp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class ChatDriverTest {

    @Test
    void mainRunsWithoutErrors() {
        assertDoesNotThrow(() -> printedBy(() -> ChatDriver.main(new String[0])));
    }

    @Test
    void mainShowsEveryPart1Feature() {
        String output = printedBy(() -> ChatDriver.main(new String[0]));
        assertTrue(output.contains("Omar -> Ghina: \"Study group is in room 210.\" delivered"), "send to more than one user");
        assertTrue(output.contains("Omar undid \"Actually, let's meet in room 999.\""), "undo");
        assertTrue(output.contains("Kyhle -> Ghina: \"Who's bringing the whiteboard markers?\" BLOCKED"), "block");
        assertTrue(output.contains("Kyhle -> Ghina: \"Ghina, can you send me the notes?\" delivered"), "unblock");
        assertTrue(output.contains("Omar -> Ghina: \"Good night everyone!\" NOT DELIVERED"), "unregister");
        assertTrue(output.contains("Ghina's chat history:"), "view chat history");
    }

    @Test
    void undoneMessageIsNotInAnyFinalChatHistory() {
        String output = printedBy(() -> ChatDriver.main(new String[0]));
        String histories = output.substring(output.indexOf("===== Chat history of each user ====="));
        assertFalse(histories.contains("room 999"), "the undone message should be gone from every history");
    }

    @Test
    void mainShowsTheIteratorSearchingAHistoryByUser() {
        String output = printedBy(() -> ChatDriver.main(new String[0]));
        String search = output.substring(output.indexOf("Messages in Omar's history sent or received by Ghina:"),
                output.indexOf("Messages in Ghina's history sent or received by Omar:"));
        assertTrue(search.contains("Study group is in room 210."), "Omar sent this to Ghina");
        assertTrue(search.contains("Thanks, I'll bring snacks."), "Ghina sent this to Omar");
        assertFalse(search.contains("Hey Kyhle"), "this one was only between Omar and Kyhle");
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
