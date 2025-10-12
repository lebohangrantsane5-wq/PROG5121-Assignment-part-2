import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    private Message message;

    @BeforeEach
    void setup() {
        message = new Message(1); // message number 1
    }

    @Test
    void testGenerateMessageIDLength() {
        message.generateMessageID();
        assertEquals(10, message.getMessageID().length(), "Message ID should be 10 digits");
    }

    @Test
    void testCheckRecipientCellSuccess() {
        String result = message.checkRecipientCell("+27718693002");
        assertEquals("Success: Cell phone number successfully captured.", result);
    }

    @Test
    void testCheckRecipientCellFailure() {
        String result = message.checkRecipientCell("08575975889");
        assertTrue(result.startsWith("Failure: Cell phone number is incorrectly formatted"));
    }

    @Test
    void testCheckMessageLengthSuccess() {
        String result = message.checkMessageLength("Hi Mike, can you join us for dinner tonight");
        assertEquals("Success: Message ready to send.", result);
    }

    @Test
    void testCheckMessageLengthFailure() {
        String longText = "A".repeat(251);
        String result = message.checkMessageLength(longText);
        assertTrue(result.startsWith("Failure: Message exceeds 250 characters"));
    }

    @Test
    void testCreateMessageHash() {
        message.checkMessageLength("Hi Mike, can you join us for dinner tonight");
        String hash = message.createMessageHash();
        assertNotNull(hash);
        assertTrue(hash.matches("\\d{2}:1:[A-Z]{2}"), "Message hash should match format: 00:1:HI");
    }

    @Test
    void testSendMessageSend() {
        message.checkMessageLength("Hi Mike, can you join us for dinner tonight");
        String result = message.sendMessage("send");
        assertEquals("Message successfully sent.", result);
    }

    @Test
    void testSendMessageStore() {
        message.checkMessageLength("Hi Mike, can you join us for dinner tonight");
        String result = message.sendMessage("store");
        assertEquals("Message successfully stored.", result);
    }

    @Test
    void testSendMessageDisregard() {
        message.checkMessageLength("Hi Mike, can you join us for dinner tonight");
        String result = message.sendMessage("disregard");
        assertEquals("Press 0 to delete message.", result);
    }

    @Test
    void testTotalMessagesSent() {
        int initialTotal = Message.returnTotalMessages();
        message.checkMessageLength("Hi Mike, can you join us for dinner tonight");
        message.sendMessage("send");
        assertEquals(initialTotal + 1, Message.returnTotalMessages());
    }
}
