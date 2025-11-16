import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class Message {

    private String message;
    private int messageID;
    private String recipient;

    private static int messageCounter = 1;

    private static final Map<String, List<Message>> sentMessages = new HashMap<>();
    private static final String STORED_FILE = "stored_messages.json";

    // ========== Constructor ==========
    public Message(String message, String recipient) {
        this.message = message;
        this.recipient = recipient;
        this.messageID = messageCounter++;
        sendMessage(this);
        saveMessage(this);
    }

    // ========== Send Message ==========
    private static void sendMessage(Message msg) {
        sentMessages.putIfAbsent(msg.recipient, new ArrayList<>());
        sentMessages.get(msg.recipient).add(msg);
    }

    // ========== Save Message to JSON ==========
    private static void saveMessage(Message msg) {
        JSONArray arr = loadStoredArray();
        JSONObject obj = new JSONObject();
        obj.put("messageID", msg.messageID);
        obj.put("message", msg.message);
        obj.put("recipient", msg.recipient);
        arr.add(obj);
        writeStoredArray(arr);
    }

    // ========== Load stored messages ==========
    private static JSONArray loadStoredArray() {
        try (FileReader reader = new FileReader(STORED_FILE)) {
            Object data = new JSONParser().parse(reader);
            return (JSONArray) data;
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private static void writeStoredArray(JSONArray arr) {
        try (FileWriter writer = new FileWriter(STORED_FILE)) {
            writer.write(arr.toJSONString());
        } catch (Exception ignored) {}
    }

    // ========== REQUIRED METHODS ==========

    public static String[] getDeveloperSentMessages() {
        List<String> output = new ArrayList<>();
        for (List<Message> msgs : sentMessages.values()) {
            for (Message m : msgs) output.add(m.message);
        }
        return output.toArray(new String[0]);
    }

    public static String getLongestMessage() {
        String longest = "";
        JSONArray arr = loadStoredArray();

        // Check stored messages
        for (Object o : arr) {
            JSONObject obj = (JSONObject) o;
            String msg = (String) obj.get("message");
            if (msg.length() > longest.length()) longest = msg;
        }
        return longest;
    }

    public static String searchByID(int id) {
        // Check sent messages
        for (List<Message> list : sentMessages.values()) {
            for (Message m : list) {
                if (m.messageID == id) return m.message;
            }
        }

        // Check stored messages
        JSONArray arr = loadStoredArray();
        for (Object o : arr) {
            JSONObject obj = (JSONObject) o;
            long mid = (long) obj.get("messageID");
            if (mid == id) return (String) obj.get("message");
        }

        return "Message not found!";
    }

    // FIXED ✔ Now filters stored messages by recipient
    public static List<String> searchByRecipient(String cell) {
        List<String> results = new ArrayList<>();

        // Sent messages
        if (sentMessages.containsKey(cell)) {
            for (Message m : sentMessages.get(cell)) {
                results.add(m.message);
            }
        }

        // Stored messages (filter by recipient)
        JSONArray arr = loadStoredArray();
        for (Object o : arr) {
            JSONObject obj = (JSONObject) o;
            String rec = (String) obj.get("recipient");
            String msg = (String) obj.get("message");

            if (rec.equals(cell)) results.add(msg);
        }

        return results;
    }

    // For testing convenience
    public static void clearAll() {
        sentMessages.clear();
        try (FileWriter fw = new FileWriter(STORED_FILE)) {
            fw.write("[]");
        } catch (Exception ignored) {}
        messageCounter = 1;
    }
}
