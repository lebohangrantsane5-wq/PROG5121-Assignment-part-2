import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Message model and storage logic (JSON-enabled version).
 *
 * Static arrays required by the assignment:
 * 1) sentMessageTexts
 * 2) disregardedMessages
 * 3) storedMessagesArray (loaded from stored_messages.json)
 * 4) messageHashes
 * 5) messageIDs
 */
public class Message {

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private int messageNumber;

    private static int totalMessagesSent = 0;
    private static final int MAX_MESSAGE_LENGTH = 250;

    // Original list of full Message objects
    private static final List<Message> sentMessages = new ArrayList<>();

    // Assignment arrays
    private static final List<String> sentMessageTexts = new ArrayList<>();
    private static final List<String> disregardedMessages = new ArrayList<>();
    private static final List<String> storedMessagesArray = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();

    // Constructor
    public Message(int messageNumber) {
        this.messageNumber = messageNumber;
        generateMessageID();
    }

    // Generate unique 10-digit ID
    private void generateMessageID() {
        long id = (long) (Math.random() * 9000000000L) + 1000000000L;
        this.messageID = String.valueOf(id);
    }

    // Allow test to set a specific ID
    public void setMessageID(String id) {
        if (id != null && id.length() == 10) {
            this.messageID = id;
        }
    }

    // Getters
    public String getMessageID() { return messageID; }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }

    public void setMessageNumber(int number) {
        this.messageNumber = number;
    }

    // Check ID
    public boolean checkMessageID() {
        return messageID != null && messageID.length() == 10;
    }

    // Validate recipient
    public String checkRecipientCell(String cell) {
        if (cell != null && cell.matches("\\+[0-9]{7,12}")) {
            this.recipient = cell;
            return "Success: Cell phone number successfully captured.";
        }
        return "Failure: Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    // Validate message length
    public String checkMessageLength(String msg) {
        if (msg == null) return "Failure: Message is null.";
        if (msg.length() > MAX_MESSAGE_LENGTH) {
            int excess = msg.length() - MAX_MESSAGE_LENGTH;
            return "Failure: Message exceeds 250 characters by " + excess + ", please reduce size.";
        }
        this.messageText = msg;
        return "Success: Message ready to send.";
    }

    // Create hash
    public String createMessageHash() {
        if (messageID == null || messageID.length() < 2) generateMessageID();

        if (messageText == null || messageText.isEmpty()) {
            this.messageHash = messageID.substring(0, 2) + ":" + messageNumber + ":??";
            return this.messageHash;
        }

        char first = Character.toUpperCase(messageText.charAt(0));
        char last = Character.toUpperCase(messageText.charAt(messageText.length() - 1));

        this.messageHash = messageID.substring(0, 2) + ":" + messageNumber + ":" + first + last;
        return this.messageHash;
    }

    // Send, store, disregard
    public String sendMessage(String option) {
        if (option == null) return "Invalid option.";

        return switch (option.toLowerCase()) {
            case "send" -> {
                totalMessagesSent++;
                sentMessages.add(this);

                sentMessageTexts.add(messageText == null ? "" : messageText);
                messageHashes.add(messageHash == null ? "" : messageHash);
                messageIDs.add(messageID == null ? "" : messageID);

                yield "Message successfully sent.";
            }

            case "store" -> {
                storeMessageJSON();
                storedMessagesArray.add(messageText == null ? "" : messageText);
                yield "Message successfully stored.";
            }

            case "disregard" -> {
                disregardedMessages.add(messageText == null ? "" : messageText);
                yield "Press 0 to delete message."; // Required by test
            }

            default -> "Invalid option.";
        };
    }

    // Save message to JSON
    private void storeMessageJSON() {
        String json = "{"
                + "\"messageID\":\"" + safe(messageID) + "\","
                + "\"recipient\":\"" + safe(recipient) + "\","
                + "\"messageText\":\"" + safe(messageText) + "\","
                + "\"messageHash\":\"" + safe(messageHash) + "\""
                + "}";

        try (FileWriter writer = new FileWriter("stored_messages.json", true)) {
            writer.write(json + System.lineSeparator());
        } catch (IOException e) {
            try {
                JOptionPane.showMessageDialog(null, "Error writing JSON: " + e.getMessage());
            } catch (java.awt.HeadlessException ignored) {}
        }
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Load stored_messages.json
    public static void loadStoredMessages() {
        storedMessagesArray.clear();
        Path path = Paths.get("stored_messages.json");

        if (!Files.exists(path)) return;

        try {
            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                String message = extractJsonValue(trimmed, "messageText");
                if (message != null) storedMessagesArray.add(message);
            }

        } catch (IOException ignored) {}
    }

    // Extract JSON field value
    private static String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int idx = json.indexOf(pattern);
        if (idx < 0) return null;

        int start = idx + pattern.length();
        int end = json.indexOf("\"", start);
        if (end < 0) return null;

        return json.substring(start, end);
    }

    // Display sender + recipients
    public static String displaySendersAndRecipients() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append("Sender: You\nRecipient: ").append(m.recipient).append("\n\n");
        }
        return sb.toString();
    }

    // Longest message (from all 3 categories)
    public static String getLongestMessage() {
        String longest = "";

        for (String s : sentMessageTexts)
            if (s != null && s.length() > longest.length()) longest = s;

        for (String s : storedMessagesArray)
            if (s != null && s.length() > longest.length()) longest = s;

        for (String s : disregardedMessages)
            if (s != null && s.length() > longest.length()) longest = s;

        if (longest.isEmpty()) return "No messages found.";
        return longest;
    }

    // Search by ID
    public static String searchByMessageID(String id) {
        if (id == null) return "No message found with that ID.";
        for (Message m : sentMessages) {
            if (id.equals(m.messageID)) {
                return "Recipient: " + m.recipient + "\nMessage: " + m.messageText;
            }
        }
        return "No message found with that ID.";
    }

    // Search by recipient
    public static List<String> searchByRecipient(String cell) {
        List<String> results = new ArrayList<>();
        if (cell == null) return results;

        for (Message m : sentMessages) {
            if (cell.equals(m.recipient)) {
                results.add(m.messageText);
            }
        }

        // Stored messages: only messageText available, so include all
        results.addAll(storedMessagesArray);

        return results;
    }

    // Delete by hash
    public static String deleteByHash(String hash) {
        if (hash == null) return "No message found with that hash.";

        for (int i = 0; i < sentMessages.size(); i++) {
            Message m = sentMessages.get(i);

            if (hash.equals(m.messageHash)) {
                String text = m.messageText;

                sentMessageTexts.remove(text);
                messageHashes.remove(hash);
                messageIDs.remove(m.messageID);

                sentMessages.remove(i);

                return "Message \"" + text + "\" Successfully deleted.";
            }
        }

        return "No message found with that hash.";
    }

    // Full report
    public static String displayFullReport() {
        if (sentMessages.isEmpty()) return "No sent messages.";

        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append("Message ID: ").append(m.messageID).append("\n");
            sb.append("Hash: ").append(m.messageHash).append("\n");
            sb.append("Recipient: ").append(m.recipient).append("\n");
            sb.append("Message: ").append(m.messageText).append("\n");
            sb.append("----------------------------------\n");
        }
        return sb.toString();
    }

    // Clear arrays (for unit tests)
    public static void clearAllMemoryData() {
        sentMessages.clear();
        sentMessageTexts.clear();
        disregardedMessages.clear();
        storedMessagesArray.clear();
        messageHashes.clear();
        messageIDs.clear();
        totalMessagesSent = 0;
    }

    public static List<String> getSentMessageTexts() { return new ArrayList<>(sentMessageTexts); }
    public static List<String> getDisregardedMessages() { return new ArrayList<>(disregardedMessages); }
    public static List<String> getStoredMessagesArray() { return new ArrayList<>(storedMessagesArray); }
    public static List<String> getMessageHashes() { return new ArrayList<>(messageHashes); }
    public static List<String> getMessageIDs() { return new ArrayList<>(messageIDs); }
    public static List<Message> getAllMessages() { return new ArrayList<>(sentMessages); }
    public static int returnTotalMessages() { return totalMessagesSent; }

    public Object printMessage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'printMessage'");
    }
}
