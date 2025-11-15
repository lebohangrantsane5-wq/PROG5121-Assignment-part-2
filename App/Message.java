import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Message model and storage logic. All static lists are used to satisfy the assignment arrays:
 *
 * Array 1: sentMessageTexts (all sent messages text)
 * Array 2: disregardedMessages (all disregarded messages text)
 * Array 3: storedMessagesArray (loaded from stored_messages.json)
 * Array 4: messageHashes (all message hashes)
 * Array 5: messageIDs (all message IDs)
 */
public class Message {

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private int messageNumber;

    private static int totalMessagesSent = 0;
    private static final int MAX_MESSAGE_LENGTH = 250;

    // Keep the original sentMessages list of Message objects
    private static final List<Message> sentMessages = new ArrayList<>();

    // Requirement arrays (represented as lists)
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

    // Generate Unique 10-digit Message ID
    private void generateMessageID() {
        long id = (long) (Math.random() * 9000000000L) + 1000000000L;
        this.messageID = String.valueOf(id);
    }

    // Getters / Setters
    public String getMessageID() {
        return messageID;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getRecipient() {
        return recipient;
    }

    // Check Message ID length
    public boolean checkMessageID() {
        return messageID != null && messageID.length() == 10;
    }

    // Check Recipient Cell format
    public String checkRecipientCell(String cell) {
        if (cell != null && cell.matches("\\+[0-9]{7,12}")) {
            this.recipient = cell;
            return "Success: Cell phone number successfully captured.";
        } else {
            return "Failure: Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    // Check message length
    public String checkMessageLength(String msg) {
        if (msg == null) return "Failure: Message is null.";
        if (msg.length() > MAX_MESSAGE_LENGTH) {
            int excess = msg.length() - MAX_MESSAGE_LENGTH;
            return "Failure: Message exceeds 250 characters by " + excess + ", please reduce size.";
        } else {
            this.messageText = msg;
            return "Success: Message ready to send.";
        }
    }

    // Create Message Hash: first 2 digits of ID + ":" + number + ":" + first/last letters
    public String createMessageHash() {
        if (messageText == null || messageText.isEmpty()) {
            this.messageHash = messageID.substring(0, 2) + ":" + messageNumber + ":??";
            return this.messageHash;
        }
        char firstChar = messageText.charAt(0);
        char lastChar = messageText.charAt(messageText.length() - 1);
        this.messageHash = messageID.substring(0, 2) + ":" + messageNumber + ":" +
                (Character.toUpperCase(firstChar)) + (Character.toUpperCase(lastChar));
        return this.messageHash;
    }

    // Send / Store / Disregard message — fills the arrays
    public String sendMessage(String option) {
        if (option == null) return "Invalid option.";
        return switch (option.toLowerCase()) {
            case "send" -> {
                totalMessagesSent++;
                sentMessages.add(this);
                sentMessageTexts.add(this.messageText);
                messageHashes.add(this.messageHash);
                messageIDs.add(this.messageID);
                yield "Message successfully sent.";
            }
            case "store" -> {
                storeMessage();
                storedMessagesArray.add(this.messageText == null ? "" : this.messageText);
                yield "Message successfully stored.";
            }
            case "disregard" -> {
                disregardedMessages.add(this.messageText == null ? "" : this.messageText);
                yield "Press 0 to delete message.";   // REQUIRED BY JUnit test
            }
            default -> "Invalid option.";
        };
    }

    // Store message in JSON file (append)
    public void storeMessage() {
        String json = "{"
                + "\"messageID\":\"" + safeJson(messageID) + "\","
                + "\"recipient\":\"" + safeJson(recipient) + "\","
                + "\"messageText\":\"" + safeJson(messageText) + "\","
                + "\"messageHash\":\"" + safeJson(messageHash) + "\"}";
        try (FileWriter writer = new FileWriter("stored_messages.json", true)) {
            writer.write(json + System.lineSeparator());
        } catch (IOException e) {
            try {
                JOptionPane.showMessageDialog(null, "Error storing message: " + e.getMessage());
            } catch (java.awt.HeadlessException ignored) {}
        }
    }

    private String safeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Print full message
    public String printMessage() {
        return "Message ID: " + messageID + "\n" +
                "Message Hash: " + messageHash + "\n" +
                "Recipient: " + recipient + "\n" +
                "Message: " + messageText;
    }

    // Return total messages sent
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Return all messages
    public static List<Message> getAllMessages() {
        return new ArrayList<>(sentMessages);
    }

    // Return requirement arrays
    public static List<String> getSentMessageTexts() {
        return new ArrayList<>(sentMessageTexts);
    }

    public static List<String> getDisregardedMessages() {
        return new ArrayList<>(disregardedMessages);
    }

    public static List<String> getStoredMessagesArray() {
        return new ArrayList<>(storedMessagesArray);
    }

    public static List<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public static List<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    // Load stored_messages.json into storedMessagesArray
    public static void loadStoredMessages() {
        storedMessagesArray.clear();
        Path path = Paths.get("stored_messages.json");
        if (!Files.exists(path)) return;

        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;
                String extracted = extractJsonValue(trimmed, "messageText");
                if (extracted == null) extracted = trimmed;
                storedMessagesArray.add(extracted);
            }
        } catch (IOException e) {
            // ignore read errors
        }
    }

    // Extract JSON value
    private static String extractJsonValue(String jsonLine, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"";
        int idx = jsonLine.indexOf(pattern);
        if (idx < 0) return null;
        int start = idx + pattern.length();
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < jsonLine.length(); i++) {
            char c = jsonLine.charAt(i);
            if (c == '"' && jsonLine.charAt(i - 1) != '\\') {
                return sb.toString();
            }
            sb.append(c);
        }
        return null;
    }

    // a) Display senders and recipients
    public static String displaySendersAndRecipients() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append("Sender: You\n");
            sb.append("Recipient: ").append(m.getRecipient()).append("\n\n");
        }
        return sb.toString();
    }

    // b) Longest sent message
    public static String getLongestMessage() {
        if (sentMessageTexts.isEmpty()) return "No sent messages.";
        String longest = "";
        for (String s : sentMessageTexts) {
            if (s != null && s.length() > longest.length()) longest = s;
        }
        return longest;
    }

    // c) Search by message ID
    public static String searchByMessageID(String id) {
        if (id == null) return "No message found with that ID.";
        for (Message m : sentMessages) {
            if (id.equals(m.getMessageID())) {
                return "Recipient: " + m.getRecipient() + "\nMessage: " + m.getMessageText();
            }
        }
        return "No message found with that ID.";
    }

    // d) Search by recipient
    public static List<String> searchByRecipient(String cell) {
        List<String> results = new ArrayList<>();
        if (cell == null) return results;
        for (Message m : sentMessages) {
            if (cell.equals(m.getRecipient())) {
                results.add(m.getMessageText());
            }
        }
        return results;
    }

    // e) Delete message by hash
    public static String deleteByHash(String hash) {
        if (hash == null) return "No message found with that hash.";
        for (int i = 0; i < sentMessages.size(); i++) {
            Message m = sentMessages.get(i);
            if (hash.equals(m.getMessageHash())) {
                sentMessageTexts.remove(m.getMessageText());
                messageHashes.remove(hash);
                messageIDs.remove(m.getMessageID());
                sentMessages.remove(i);
                return "Message deleted successfully.";
            }
        }
        return "No message found with that hash.";
    }

    // f) Full report
    public static String displayFullReport() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append("Message ID: ").append(m.getMessageID()).append("\n");
            sb.append("Hash: ").append(m.getMessageHash()).append("\n");
            sb.append("Recipient: ").append(m.getRecipient()).append("\n");
            sb.append("Message: ").append(m.getMessageText()).append("\n");
            sb.append("----------------------------------\n");
        }
        if (sb.length() == 0) return "No sent messages.";
        return sb.toString();
    }

    // Utility: clear memory (for tests)
    public static void clearAllMemoryData() {
        sentMessages.clear();
        sentMessageTexts.clear();
        disregardedMessages.clear();
        storedMessagesArray.clear();
        messageHashes.clear();
        messageIDs.clear();
        totalMessagesSent = 0;
    }
}
