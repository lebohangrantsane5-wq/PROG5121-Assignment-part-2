import javax.swing.*;

public class MessageApp {
    public static void main(String[] args) {
        Login login = new Login("", "");

        // Registration
        String firstName = JOptionPane.showInputDialog("Enter First Name:");
        String lastName = JOptionPane.showInputDialog("Enter Last Name:");
        String username = JOptionPane.showInputDialog("Create Username:");
        String password = JOptionPane.showInputDialog("Create Password:");
        String cell = JOptionPane.showInputDialog("Enter Cellphone (+27721234567):");

        String regMessage = login.registerUser(username, password, cell, firstName, lastName);
        JOptionPane.showMessageDialog(null, regMessage);
        if (!regMessage.equals("User successfully registered!")) return;

        // Login
        boolean loginStatus = false;
        for (int i = 1; i <= 3; i++) {
            String loginUsername = JOptionPane.showInputDialog("Enter Username:");
            String loginPassword = JOptionPane.showInputDialog("Enter Password:");
            loginStatus = login.authenticate(loginUsername, loginPassword);

            if (loginStatus) break;
            if (login.isLocked()) {
                JOptionPane.showMessageDialog(null, "Too many failed attempts. Exiting.");
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect. Attempts left: " + (3 - i));
            }
        }

        if (!loginStatus) {
            JOptionPane.showMessageDialog(null, "Login failed.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Welcome to QuickChat!");

        // Number of messages
        int totalMessages = Integer.parseInt(JOptionPane.showInputDialog("How many messages would you like to send?"));

        boolean running = true;
        int messageCount = 0;
        while (running) {
            String menu = "Choose an option:\n1) Send messages\n2) Show recently sent messages\n3) Quit";
            int choice = Integer.parseInt(JOptionPane.showInputDialog(menu));

            switch (choice) {
                case 1:
                    if (messageCount >= totalMessages) {
                        JOptionPane.showMessageDialog(null, "You have already sent the maximum number of messages.");
                        break;
                    }
                    Message message = new Message(messageCount + 1);

                    String recipient = JOptionPane.showInputDialog("Enter recipient (+country code):");
                    JOptionPane.showMessageDialog(null, message.checkRecipientCell(recipient));

                    String text = JOptionPane.showInputDialog("Enter message (max 250 chars):");
                    String lengthCheck = message.checkMessageLength(text);
                    JOptionPane.showMessageDialog(null, lengthCheck);
                    if (lengthCheck.startsWith("Failure")) break;

                    message.createMessageHash();

                    String option = JOptionPane.showInputDialog("Send, Store, or Disregard?");
                    JOptionPane.showMessageDialog(null, message.sendMessage(option));

                    JOptionPane.showMessageDialog(null, message.printMessage());
                    messageCount++;
                    break;

                case 2:
                    JOptionPane.showMessageDialog(null, "Coming soon.");
                    break;

                case 3:
                    JOptionPane.showMessageDialog(null, "Exiting QuickChat. Goodbye!");
                    running = false;
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Invalid selection. Try again.");
            }
        }

        JOptionPane.showMessageDialog(null, "Total messages sent: " + Message.returnTotalMessages());
    }
}
