import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Message {

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private int messageNumber;

    private static int totalMessagesSent = 0;
    private static final int MAX_MESSAGE_LENGTH = 250;
    private static final List<Message> sentMessages = new ArrayList<>();

    // Constructor
    public Message(int messageNumber) {
        this.messageNumber = messageNumber;
        generateMessageID();
    }

    // Generate Unique 10-digit Message ID
    public void generateMessageID() {
        long id = (long) (Math.random() * 9000000000L) + 1000000000L;
        this.messageID = String.valueOf(id);
    }

    // Getter for messageID
    public String getMessageID() {
        return messageID;
    }

    // Getter for messageText
    public String getMessageText() {
        return messageText;
    }

    // Getter for messageHash
    public String getMessageHash() {
        return messageHash;
    }

    // Getter for recipient
    public String getRecipient() {
        return recipient;
    }

    // Check Message ID length
    public boolean checkMessageID() {
        return messageID != null && messageID.length() == 10;
    }

    // Check Recipient Cell format
    public String checkRecipientCell(String cell) {
        if (cell.matches("\\+[0-9]{7,12}")) {
            this.recipient = cell;
            return "Success: Cell phone number successfully captured.";
        } else {
            return "Failure: Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    // Check message length
    public String checkMessageLength(String msg) {
        if (msg.length() > MAX_MESSAGE_LENGTH) {
            int excess = msg.length() - MAX_MESSAGE_LENGTH;
            return "Failure: Message exceeds 250 characters by " + excess + ", please reduce size.";
        } else {
            this.messageText = msg;
            return "Success: Message ready to send.";
        }
    }

    // Create Message Hash: first 2 digits of ID + ":" + messageNumber + ":" + first/last letters in caps
    public String createMessageHash() {
        char firstChar = messageText.charAt(0);
        char lastChar = messageText.charAt(messageText.length() - 1);
        this.messageHash = messageID.substring(0, 2) + ":" + messageNumber + ":" +
                (Character.toUpperCase(firstChar)) + (Character.toUpperCase(lastChar));
        return this.messageHash;
    }

    // Send / Store / Disregard message
    public String sendMessage(String option) {
        switch (option.toLowerCase()) {
            case "send":
                totalMessagesSent++;
                sentMessages.add(this);
                return "Message successfully sent.";
            case "store":
                storeMessage();
                return "Message successfully stored.";
            case "disregard":
                return "Press 0 to delete message.";
            default:
                return "Invalid option.";
        }
    }

    // Store message in JSON
    public void storeMessage() {
        String json = "{"
                + "\"messageID\":\"" + messageID + "\","
                + "\"recipient\":\"" + recipient + "\","
                + "\"messageText\":\"" + messageText + "\","
                + "\"messageHash\":\"" + messageHash + "\"}";
        try (FileWriter writer = new FileWriter("stored_messages.json", true)) {
            writer.write(json + System.lineSeparator());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error storing message: " + e.getMessage());
        }
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
        return sentMessages;
    }

    // Set message number
    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }
}
