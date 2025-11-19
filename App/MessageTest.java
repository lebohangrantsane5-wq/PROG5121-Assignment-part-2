import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @BeforeEach
    void resetBeforeTests() {
        Message.clearAllMemoryData();
        try {
            Files.deleteIfExists(Paths.get("stored_messages.json"));
        } catch (Exception ignored) {}
    }

    // --------------------------------------------------------------
    // Test 1: Message ID generation + setter
    // --------------------------------------------------------------
    @Test
    void testMessageIDGeneration() {
        Message m = new Message(1);
        assertNotNull(m.getMessageID());
        assertEquals(10, m.getMessageID().length());
        assertTrue(m.checkMessageID());
    }

    @Test
    void testSetMessageID() {
        Message m = new Message(1);
        m.setMessageID("1234567890");
        assertEquals("1234567890", m.getMessageID());
    }

    // --------------------------------------------------------------
    // Test 2: Recipient validation
    // --------------------------------------------------------------
    @Test
    void testRecipientValidation() {
        Message m = new Message(1);

        String ok = m.checkRecipientCell("+27835551234");
        assertEquals("Success: Cell phone number successfully captured.", ok);

        String bad = m.checkRecipientCell("0835551234");
        assertEquals("Failure: Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", bad);
    }

    // --------------------------------------------------------------
    // Test 3: Message length validation
    // --------------------------------------------------------------
    @Test
    void testMessageLength() {
        Message m = new Message(1);

        String ok = m.checkMessageLength("Hello!");
        assertEquals("Success: Message ready to send.", ok);

        String longText = "A".repeat(260);
        String result = m.checkMessageLength(longText);
        assertTrue(result.contains("Failure: Message exceeds 250 characters by"));
    }

    // --------------------------------------------------------------
    // Test 4: Hash creation
    // --------------------------------------------------------------
    @Test
    void testMessageHash() {
        Message m = new Message(1);
        m.setMessageID("1234567890");
        m.checkMessageLength("Hello");

        String hash = m.createMessageHash();
        assertEquals("12:1:HO", hash); // 12 | msgNum=1 | first=H last=O
    }

    // --------------------------------------------------------------
    // Test 5: Send, Store, Disregard
    // --------------------------------------------------------------
    @Test
    void testSendMessage() {
        Message m = new Message(1);
        m.checkRecipientCell("+27830000001");
        m.checkMessageLength("Test");
        m.createMessageHash();

        String res = m.sendMessage("send");
        assertEquals("Message successfully sent.", res);

        assertEquals(1, Message.getSentMessageTexts().size());
        assertEquals("Test", Message.getSentMessageTexts().get(0));
    }

    @Test
    void testStoreMessageCreatesJSONAndArray() {
        Message m = new Message(1);
        m.checkRecipientCell("+27830000001");
        m.checkMessageLength("Store this");
        m.createMessageHash();

        String res = m.sendMessage("store");
        assertEquals("Message successfully stored.", res);

        assertEquals(1, Message.getStoredMessagesArray().size());
        assertTrue(Files.exists(Paths.get("stored_messages.json")));
    }

    @Test
    void testDisregardMessage() {
        Message m = new Message(1);
        m.checkMessageLength("Discard this");

        String res = m.sendMessage("disregard");
        assertEquals("Press 0 to delete message.", res);
        assertEquals(1, Message.getDisregardedMessages().size());
    }

    // --------------------------------------------------------------
    // Test 6: Load stored_messages.json
    // --------------------------------------------------------------
    @Test
    void testLoadStoredMessagesFromJSON() throws Exception {
        // Manually add JSON line
        Files.writeString(Paths.get("stored_messages.json"),
                "{\"messageText\":\"Hello JSON\"}\n");

        Message.loadStoredMessages();

        assertEquals(1, Message.getStoredMessagesArray().size());
        assertEquals("Hello JSON", Message.getStoredMessagesArray().get(0));
    }

    // --------------------------------------------------------------
    // Test 7: Search by ID
    // --------------------------------------------------------------
    @Test
    void testSearchByMessageID() {
        Message m = new Message(1);
        m.setMessageID("1234567890");
        m.checkRecipientCell("+27830000001");
        m.checkMessageLength("Hello ID search");
        m.createMessageHash();
        m.sendMessage("send");

        String result = Message.searchByMessageID("1234567890");
        assertTrue(result.contains("Hello ID search"));
        assertTrue(result.contains("+27830000001"));
    }

    // --------------------------------------------------------------
    // Test 8: Search by Recipient
    // --------------------------------------------------------------
    @Test
    void testSearchByRecipient() {
        Message m1 = new Message(1);
        m1.checkRecipientCell("+27831110000");
        m1.checkMessageLength("A");
        m1.createMessageHash();
        m1.sendMessage("send");

        Message m2 = new Message(2);
        m2.checkRecipientCell("+27831110000");
        m2.checkMessageLength("B");
        m2.createMessageHash();
        m2.sendMessage("send");

        List<String> results = Message.searchByRecipient("+27831110000");

        assertEquals(2, results.size());
        assertTrue(results.contains("A"));
        assertTrue(results.contains("B"));
    }

    // --------------------------------------------------------------
    // Test 9: Longest Message
    // --------------------------------------------------------------
    @Test
    void testLongestMessage() {
        Message m1 = new Message(1);
        m1.checkMessageLength("Short");
        m1.sendMessage("disregard");

        Message m2 = new Message(2);
        m2.checkMessageLength("This is the longest message here.");
        m2.sendMessage("send");

        assertEquals("This is the longest message here.", Message.getLongestMessage());
    }

    // --------------------------------------------------------------
    // Test 10: Delete by Hash
    // --------------------------------------------------------------
    @Test
    void testDeleteByHash() {
        Message m = new Message(1);
        m.setMessageID("1234567890");
        m.checkMessageLength("Delete me");
        m.createMessageHash();
        String hash = m.getMessageHash();
        m.sendMessage("send");

        String res = Message.deleteByHash(hash);
        assertTrue(res.contains("Successfully deleted."));
        assertEquals(0, Message.getAllMessages().size());
    }

    // --------------------------------------------------------------
    // Test 11: Full Report
    // --------------------------------------------------------------
    @Test
    void testFullReport() {
        Message m = new Message(1);
        m.checkRecipientCell("+27830000001");
        m.checkMessageLength("Report");
        m.createMessageHash();
        m.sendMessage("send");

        String report = Message.displayFullReport();
        assertTrue(report.contains("Message ID"));
        assertTrue(report.contains("Recipient"));
        assertTrue(report.contains("Report"));
    }
}
