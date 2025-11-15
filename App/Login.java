import javax.swing.*;

public class Login extends Register {
    private int failedAttempts = 0;
    private boolean locked = false;

    // Constructor
    public Login(String username, String password) {
        this.regUsername = username;
        this.regPassword = password;
    }

    // Authenticate user with lockout logic
    public boolean authenticate(String username, String password) {
        if (locked) return false;

        boolean success = loginUser(username, password);
        if (!success) {
            failedAttempts++;
            if (failedAttempts >= 3) locked = true;
        } else {
            failedAttempts = 0;
        }
        return success;
    }

    // Check lockout status
    public boolean isLocked() {
        return locked;
    }

    public static void main(String[] args) {
        Login login = new Login("", "");

        // --- Registration Phase ---
        String firstName = JOptionPane.showInputDialog("Enter First Name:");
        String lastName = JOptionPane.showInputDialog("Enter Last Name:");
        String username = JOptionPane.showInputDialog("Create Username:");
        String password = JOptionPane.showInputDialog("Create Password:");
        String cell = JOptionPane.showInputDialog("Enter Cellphone (with country code, e.g., +27721234567):");

        String regMessage = login.registerUser(username, password, cell, firstName, lastName);
        JOptionPane.showMessageDialog(null, regMessage);

        if (!regMessage.equals("User successfully registered!")) return;

        // --- Login Phase ---
        boolean loginStatus = false;
        for (int attempts = 1; attempts <= 3; attempts++) {
            String loginUsername = JOptionPane.showInputDialog("Enter Username to Login:");
            String loginPassword = JOptionPane.showInputDialog("Enter Password to Login:");

            loginStatus = login.authenticate(loginUsername, loginPassword);

            if (loginStatus) break;

            if (login.isLocked()) {
                JOptionPane.showMessageDialog(null, "Too many failed attempts. Program will now exit.");
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect username or password. Attempts left: " + (3 - attempts));
            }
        }

        if (!loginStatus) return;

        JOptionPane.showMessageDialog(null, login.returnLoginStatus(true));

        // --- Message Management Phase ---
        boolean running = true;
        while (running) {
            String option = JOptionPane.showInputDialog(
                    "Choose an action:\n" +
                    "1 - Send Message\n" +
                    "2 - Store Message\n" +
                    "3 - Disregard Message\n" +
                    "4 - View All Sent Messages\n" +
                    "5 - View Longest Message\n" +
                    "6 - Delete Message by Hash\n" +
                    "0 - Exit"
            );

            if (option == null) continue;

            switch (option) {
                case "1", "2", "3" -> {
                    String recipient = JOptionPane.showInputDialog("Enter recipient number:");
                    String text = JOptionPane.showInputDialog("Enter your message:");

                    Message msg = new Message(Message.returnTotalMessages() + 1);
                    msg.checkRecipientCell(recipient);
                    msg.checkMessageLength(text);
                    msg.createMessageHash();

                    String result = switch (option) {
                        case "1" -> msg.sendMessage("send");
                        case "2" -> msg.sendMessage("store");
                        default -> msg.sendMessage("disregard");
                    };

                    JOptionPane.showMessageDialog(null, result);
                }
                case "4" -> {
                    String report = Message.displayFullReport();
                    JOptionPane.showMessageDialog(null, report);
                }
                case "5" -> {
                    String longest = Message.getLongestMessage();
                    JOptionPane.showMessageDialog(null, "Longest message:\n" + longest);
                }
                case "6" -> {
                    String hash = JOptionPane.showInputDialog("Enter message hash to delete:");
                    String delResult = Message.deleteByHash(hash);
                    JOptionPane.showMessageDialog(null, delResult);
                }
                case "0" -> running = false;
                default -> JOptionPane.showMessageDialog(null, "Invalid option.");
            }
        }

        JOptionPane.showMessageDialog(null, "Goodbye!");
    }
}
