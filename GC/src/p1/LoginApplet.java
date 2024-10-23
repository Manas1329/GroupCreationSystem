package p1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class LoginApplet extends JFrame implements ActionListener {
    JTextField emailField;
    JPasswordField passwordField;
    JButton loginButton, registerButton;
    JLabel emailLabel, passwordLabel, loginLabel, createAccountLabel;
    public static HashMap<String, String[]> logininfo = new HashMap<>();


    public LoginApplet() {
        // Set up the JFrame
        setTitle("Login Page");
        setLayout(null); // Absolute positioning
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 204, 102));

        // Title - "Login"
        loginLabel = new JLabel("Login", JLabel.CENTER);
        loginLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        loginLabel.setBounds(110, 20, 150, 40);
        add(loginLabel);

        // Email Label and TextField
        emailLabel = new JLabel("Email id :");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailLabel.setBounds(90, 80, 80, 25);
        add(emailLabel);

        emailField = new JTextField(20);
        emailField.setBounds(180, 80, 150, 25);
        add(emailField);

        // Password Label and PasswordField
        passwordLabel = new JLabel("Password :");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordLabel.setBounds(90, 120, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(180, 120, 150, 25);
        add(passwordField);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(140, 160, 100, 30);
        loginButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        loginButton.addActionListener(this);
        add(loginButton);

        // "Don't have an account? Create one" label
        createAccountLabel = new JLabel("Don't have an account? Create one", JLabel.CENTER);
        createAccountLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        createAccountLabel.setBounds(90, 200, 200, 20);
        add(createAccountLabel);

        // Register Button
        registerButton = new JButton("Register");
        registerButton.setBounds(140, 230, 100, 30);
        registerButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        registerButton.addActionListener(this);
        add(registerButton);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String email = emailField.getText();
            String password = String.valueOf(passwordField.getPassword());

            if (validateLogin(email, password)) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                this.setVisible(false);
                new HomeSwing(email).setVisible(true); // Open Home Page after login
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == registerButton) {
            this.setVisible(false);
            new RegistrationPage().setVisible(true); // Redirect to Registration Page
        }
    }

    private boolean validateLogin(String email, String password) {
        String DB_URL = "jdbc:mysql://localhost:3306/test?"; // Replace with your DB name
        String USER = "root"; // Replace with your MySQL username
        String PASS = ""; // Replace with your MySQL password

        boolean isValid = false;

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "SELECT Password FROM logininfo WHERE Email = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("Password");
                isValid = storedPassword.equals(password);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return isValid;
    }

    public static void main(String[] args) {
        new LoginApplet().setVisible(true);
    }
}
