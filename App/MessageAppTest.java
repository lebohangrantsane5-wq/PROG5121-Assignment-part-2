import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageAppTest {

    private Message[] messages;

    @BeforeEach
    void setup() {
        messages = new Message[2];
        messages[0] = new Message(1);
        messages[1] = new Message(2);
    }

    @Test
    void testSendMultipleMessages() {
        // Message 1
        messages[0].checkRecipientCell("+27718693002");
        messages[0].checkMessageLength("Hi Mike, can you join us for dinner tonight");
        messages[0].createMessageHash();
        String result1 = messages[0].sendMessage("send");
        assertEquals("Message successfully sent.", result1);

        // Message 2
        messages[1].checkRecipientCell("+278575975889"); // Invalid format for testing failure
        String recipientResult = messages[1].checkRecipientCell("08575975889");
        assertTrue(recipientResult.startsWith("Failure"));

        messages[1].checkMessageLength("Hi Keegan, did you receive the payment?");
        messages[1].createMessageHash();
        String result2 = messages[1].sendMessage("disregard");
        assertEquals("Press 0 to delete message.", result2);

        // Total messages sent
        assertEquals(1, Message.returnTotalMessages());
    }
}
