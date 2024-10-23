package p1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class RegistrationPage extends JFrame implements ActionListener {
    JTextField firstNameField, lastNameField, emailField;
    JPasswordField newPasswordField, confirmPasswordField;
    JButton registerButton, backToLoginButton;
    JLabel titleLabel, firstNameLabel, lastNameLabel, emailLabel, passwordLabel, confirmPasswordLabel;
    public static HashMap<String, String[]> logininfo = new HashMap<>();

    // Database connection details
//    Class.forName("com.mysql.cj.jdbc.Driver");
// Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?" +"user=root");
    static final String DB_URL = "jdbc:mysql://localhost:3306/test?"; // Replace with your database name
    static final String USER = "root"; // Replace with your MySQL username
    static final String PASS = ""; // Replace with your MySQL password

    public RegistrationPage() {
        
        // Set up the JFrame
        setTitle("Registration Page");
        setLayout(null);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 204, 102));

        // Title - "Registration"
        titleLabel = new JLabel("Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBounds(100, 20, 180, 40);
        add(titleLabel);

        // First Name and Last Name Labels and TextFields
        firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        firstNameLabel.setBounds(60, 80, 100, 25);
        add(firstNameLabel);

        firstNameField = new JTextField(20);
        firstNameField.setBounds(185, 80, 150, 25);
        add(firstNameField);

        lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lastNameLabel.setBounds(60, 110, 100, 25);
        add(lastNameLabel);

        lastNameField = new JTextField(20);
        lastNameField.setBounds(185, 110, 150, 25);
        add(lastNameField);

        // Email Label and TextField
        emailLabel = new JLabel("Email id:");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailLabel.setBounds(60, 150, 100, 25);
        add(emailLabel);

        emailField = new JTextField(20);
        emailField.setBounds(185, 150, 150, 25);
        add(emailField);

        // New Password and Confirm Password Labels and PasswordFields
        passwordLabel = new JLabel("New Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordLabel.setBounds(60, 190, 100, 25);
        add(passwordLabel);

        newPasswordField = new JPasswordField(20);
        newPasswordField.setBounds(185, 190, 150, 25);
        add(newPasswordField);

        confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        confirmPasswordLabel.setBounds(60, 220, 150, 25);
        add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setBounds(185, 220, 150, 25);
        add(confirmPasswordField);

        // Register Button
        registerButton = new JButton("Register");
        registerButton.setBounds(140, 260, 100, 30);
        registerButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        registerButton.addActionListener(this);
        add(registerButton);

        // Back to Login Button
        backToLoginButton = new JButton("Back to Login");
        backToLoginButton.setBounds(115, 300, 150, 30);
        backToLoginButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backToLoginButton.addActionListener(this);
        add(backToLoginButton);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            // Validate password confirmation
            String newPassword = String.valueOf(newPasswordField.getPassword());
            String confirmPassword = String.valueOf(confirmPasswordField.getPassword());

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Save to the MySQL database
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                String insertSQL = "INSERT INTO logininfo (Fname, Lname, Email, Password) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(insertSQL);
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, email);
                preparedStatement.setString(4, newPassword);
                preparedStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Registration Successful!");
                this.setVisible(false); // Hide registration form
                new LoginApplet().setVisible(true); // Show login form
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error during registration: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == backToLoginButton) {
            this.setVisible(false);
            new LoginApplet().setVisible(true);
        }
    }

    public static void main(String[] args) {
        new RegistrationPage().setVisible(true);
    }
}

