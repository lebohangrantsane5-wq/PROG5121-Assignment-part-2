/*
 * This file contains the Register class, which includes user registration and validation methods.
 *
 * Reference:
 * OpenAI. (2025, August 20). Regular expression–based cell phone checker in Java
 * [Large language model response]. ChatGPT. https://chat.openai.com/
 */
 




public class Register {
    protected String regUsername;
    protected String regPassword;
    private String regCell;
    private String firstName;
    private String lastName;

    // --- Check Username ---
    public boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    // --- Check Password ---
    public boolean checkPasswordComplexity(String password) {
        return password.length() >= 8 &&
               password.matches(".*[A-Z].*") &&  // at least one capital letter
               password.matches(".*[0-9].*") &&  // at least one number
               password.matches(".*[^a-zA-Z0-9].*"); // at least one special char
    }

    // --- Check Cellphone ---
    public boolean checkCellPhoneNumber(String cell) {
        return cell.matches("\\+[0-9]{1,3}[0-9]{7,10}");
    } // Regex pattern logic adapted with assistance from ChatGPT (OpenAI, 2025)

    // --- Register User ---
    public String registerUser(String username, String password, String cell, String fName, String lName) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber(cell)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        this.regUsername = username;
        this.regPassword = password;
        this.regCell = cell;
        this.firstName = fName;
        this.lastName = lName;

        return "User successfully registered!";
    }

    // --- Login User ---
    public boolean loginUser(String username, String password) {
        return username.equals(regUsername) && password.equals(regPassword);
    }

    // --- Return Login Status ---
    public String returnLoginStatus(boolean status) {
        if (status) {
            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}
