import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @BeforeEach
    public void beforeEach() {
        // clear in-memory lists before each test
        Message.clearAllMemoryData();
        // delete stored_messages.json if it exists to control tests
        try {
            Files.deleteIfExists(Paths.get("stored_messages.json"));
        } catch (IOException ignored) {}
    }

    @Test
    public void testGenerateMessageID_and_checkMessageID() {
        Message m = new Message(1);
        assertNotNull(m.getMessageID());
        assertEquals(10, m.getMessageID().length());
        assertTrue(m.checkMessageID());
    }

    @Test
    public void testCheckRecipientCell_and_checkMessageLength_and_createHash() {
        Message m = new Message(1);
        String rc = m.checkRecipientCell("+27821234567");
        assertTrue(rc.startsWith("Success"));
        assertEquals("+27821234567", m.getRecipient());

        String len = m.checkMessageLength("Hello");
        assertTrue(len.startsWith("Success"));
        String hash = m.createMessageHash();
        assertNotNull(hash);
        assertTrue(hash.contains(":1:"));
    }

    @Test
    public void testSendStoreDisregard_and_arrays() throws IOException {
        Message.clearAllMemoryData();
        Message m1 = new Message(1);
        m1.checkRecipientCell("+27720000000");
        m1.checkMessageLength("First message");
        m1.createMessageHash();
        assertEquals("Message successfully sent.", m1.sendMessage("send"));

        Message m2 = new Message(2);
        m2.checkRecipientCell("+27720000001");
        m2.checkMessageLength("Second message stored");
        m2.createMessageHash();
        assertEquals("Message successfully stored.", m2.sendMessage("store"));

        Message m3 = new Message(3);
        m3.checkRecipientCell("+27720000002");
        m3.checkMessageLength("Third message disregarded");
        m3.createMessageHash();
        assertEquals("Message was disregarded.", m3.sendMessage("disregard"));

        List<String> sentTexts = Message.getSentMessageTexts();
        List<String> stored = Message.getStoredMessagesArray();
        List<String> disregarded = Message.getDisregardedMessages();
        List<String> ids = Message.getMessageIDs();
        List<String> hashes = Message.getMessageHashes();

        assertEquals(1, sentTexts.size());
        assertEquals(1, stored.size());
        assertEquals(1, disregarded.size());
        assertEquals(1, ids.size());
        assertEquals(1, hashes.size());
    }

    @Test
    public void testSearchByMessageID_and_searchByRecipient_and_deleteByHash() {
        Message.clearAllMemoryData();
        Message m1 = new Message(1);
        m1.checkRecipientCell("+27710000000");
        m1.checkMessageLength("Alpha");
        m1.createMessageHash();
        m1.sendMessage("send");

        Message m2 = new Message(2);
        m2.checkRecipientCell("+27710000000");
        m2.checkMessageLength("Beta message");
        m2.createMessageHash();
        m2.sendMessage("send");

        // Search by ID
        String id1 = m1.getMessageID();
        String found = Message.searchByMessageID(id1);
        assertTrue(found.contains("Recipient: +27710000000"));
        assertTrue(found.contains("Alpha"));

        // Search by recipient
        List<String> foundList = Message.searchByRecipient("+27710000000");
        assertEquals(2, foundList.size());
        assertTrue(foundList.contains("Alpha"));
        assertTrue(foundList.contains("Beta message"));

        // Delete by hash
        String hash1 = m1.getMessageHash();
        String del = Message.deleteByHash(hash1);
        assertEquals("Message deleted successfully.", del);

        // now one message remains
        assertEquals(1, Message.getAllMessages().size());
    }

    @Test
    public void testGetLongestMessage() {
        Message.clearAllMemoryData();
        Message m1 = new Message(1);
        m1.checkRecipientCell("+27711111111");
        m1.checkMessageLength("short");
        m1.createMessageHash();
        m1.sendMessage("send");

        Message m2 = new Message(2);
        m2.checkRecipientCell("+27722222222");
        m2.checkMessageLength("this is longer message text");
        m2.createMessageHash();
        m2.sendMessage("send");

        String longest = Message.getLongestMessage();
        assertEquals("this is longer message text", longest);
    }

    @Test
    public void testLoadStoredMessages() throws IOException {
        // prepare stored_messages.json
        String json1 = "{\"messageID\":\"1234567890\",\"recipient\":\"+27730000000\",\"messageText\":\"stored one\",\"messageHash\":\"12:1:SO\"}";
        String json2 = "{\"messageID\":\"2234567890\",\"recipient\":\"+27730000001\",\"messageText\":\"stored two\",\"messageHash\":\"22:2:ST\"}";
        Files.write(Paths.get("stored_messages.json"), (json1 + System.lineSeparator() + json2 + System.lineSeparator()).getBytes());

        Message.loadStoredMessages();
        List<String> stored = Message.getStoredMessagesArray();
        assertEquals(2, stored.size());
        assertTrue(stored.contains("stored one"));
        assertTrue(stored.contains("stored two"));

        // cleanup
        Files.deleteIfExists(Paths.get("stored_messages.json"));
    }
}
