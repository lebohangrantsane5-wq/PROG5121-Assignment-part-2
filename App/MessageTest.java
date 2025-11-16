import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @BeforeEach
    void resetMessageData() {
        Message.clearAll();
    }

    @Test
    void testSendMessageAndStorage() {
        Message m = new Message("Hello World", "+27830001111");

        String[] sent = Message.getDeveloperSentMessages();
        assertEquals(1, sent.length);
        assertEquals("Hello World", sent[0]);
    }

    @Test
    void testSearchByID() {
        Message m1 = new Message("First Message", "+27835550000");
        Message m2 = new Message("Second Message", "+27835550000");

        assertEquals("First Message", Message.searchByID(1));
        assertEquals("Second Message", Message.searchByID(2));
        assertEquals("Message not found!", Message.searchByID(999));
    }

    @Test
    void testLongestMessage() {
        new Message("Short", "+27830001111");
        new Message("This is the longest message in the list", "+27830002222");
        new Message("Medium size", "+27830003333");

        assertEquals("This is the longest message in the list", Message.getLongestMessage());
    }

    @Test
    void testSearchByRecipient() {
        new Message("Msg A", "+27831110000");
        new Message("Msg B", "+27831110000");
        new Message("Other", "+27832220000");

        List<String> results = Message.searchByRecipient("+27831110000");

        assertEquals(2, results.size());
        assertTrue(results.contains("Msg A"));
        assertTrue(results.contains("Msg B"));
    }

    @Test
    void testSearchByRecipientReturnsEmpty() {
        new Message("A", "+27831110000");

        List<String> results = Message.searchByRecipient("+27839999999");
        assertEquals(0, results.size());
    }

    @Test
    void testGetDeveloperMessages() {
        new Message("Dev1", "+27831112222");
        new Message("Dev2", "+27831112222");

        String[] msgs = Message.getDeveloperSentMessages();

        assertEquals(2, msgs.length);
        assertEquals("Dev1", msgs[0]);
        assertEquals("Dev2", msgs[1]);
    }
}
