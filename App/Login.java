import javax.swing.*;

public class Login extends Register {
    private int failedAttempts = 0;
    private boolean locked = false;

    // Constructor that matches LoginTest
    public Login(String username, String password) {
        this.regUsername = username;
        this.regPassword = password;
    }

    // Method that matches LoginTest
    public boolean authenticate(String username, String password) {
        if (locked) {
            return false; // already locked out
        }

        boolean success = loginUser(username, password);
        if (!success) {
            failedAttempts++;
            if (failedAttempts >= 3) {
                locked = true; // lock after 3 failures
            }
        } else {
            failedAttempts = 0; // reset counter if login succeeds
        }

        return success;
    }

    //  Helper method so tests can check lockout status
    public boolean isLocked() {
        return locked;
    }

    public static void main(String[] args) {
        Login login = new Login("", ""); // default constructor values

        // Registration phase
        String firstName = JOptionPane.showInputDialog("Enter First Name:");
        String lastName = JOptionPane.showInputDialog("Enter Last Name:");
        String username = JOptionPane.showInputDialog("Create Username:");
        String password = JOptionPane.showInputDialog("Create Password:");
        String cell = JOptionPane.showInputDialog("Enter Cellphone (with country code, e.g., +27721234567):");

        String regMessage = login.registerUser(username, password, cell, firstName, lastName);
        JOptionPane.showMessageDialog(null, regMessage);

        if (!regMessage.equals("User successfully registered!")) {
            return;
        }

        // Login phase with 3 attempts
        boolean loginStatus = false;
        for (int attempts = 1; attempts <= 3; attempts++) {
            String loginUsername = JOptionPane.showInputDialog("Enter Username to Login:");
            String loginPassword = JOptionPane.showInputDialog("Enter Password to Login:");

            loginStatus = login.authenticate(loginUsername, loginPassword); // now uses lockout logic

            if (loginStatus) {
                break; // stop loop if login successful
            } else if (login.isLocked()) {
                JOptionPane.showMessageDialog(null, "Too many failed attempts. Program will now exit.");
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Incorrect username or password. Attempts left: " + (3 - attempts));
            }
        }

        if (loginStatus) {
            String statusMessage = login.returnLoginStatus(true);
            JOptionPane.showMessageDialog(null, statusMessage);
        }
    }
}
