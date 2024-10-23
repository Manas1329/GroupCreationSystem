package p1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditGroupSwing extends JFrame {

    private JTextField projectTopicField;
    private JTextField memberEmailsField; // To add or remove members
    private int groupId;
    private String userEmail;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test"; // Update with your DB name
    private static final String USER = "root"; // Your MySQL username
    private static final String PASS = ""; // Your MySQL password

    public EditGroupSwing(int groupId, String userEmail) {
        this.groupId = groupId;
        this.userEmail = userEmail;

        setTitle("Edit Group");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Create UI components
        JLabel projectTopicLabel = new JLabel("Project Topic:");
        projectTopicField = new JTextField(20);

        JLabel memberEmailsLabel = new JLabel("Member Emails (comma separated):");
        memberEmailsField = new JTextField(20);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(new SaveButtonListener());

        // Layout the components
        setLayout(new GridLayout(5, 2));
        add(projectTopicLabel);
        add(projectTopicField);
        add(memberEmailsLabel);
        add(memberEmailsField);
        add(saveButton);
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String projectTopic = projectTopicField.getText();
            String memberEmails = memberEmailsField.getText();

            // Update the project topic in the database
            updateGroupDetails(projectTopic, memberEmails);
        }
    }

    private void updateGroupDetails(String projectTopic, String memberEmails) {
        String updateQuery = "UPDATE groupinfo SET projecttopic = ? WHERE groupid = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            // Update the group project topic
            stmt.setString(1, projectTopic);
            stmt.setInt(2, groupId);
            stmt.executeUpdate();

            // Optionally, handle member updates (this can be more complex, depending on your requirements)
            updateGroupMembers(memberEmails);

            JOptionPane.showMessageDialog(this, "Group details updated successfully.");
            this.dispose(); // Close the edit window

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating group details: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateGroupMembers(String memberEmails) {
        // Here you can implement logic to update group members based on the input
        // You could split the emails and add/remove members as needed

        // For simplicity, let's assume you are just adding members
        String[] emails = memberEmails.split(",");
        String insertQuery = "INSERT INTO studentinfo (smail, groupid) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            for (String email : emails) {
                stmt.setString(1, email.trim());
                stmt.setInt(2, groupId);
                stmt.executeUpdate();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating members: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EditGroupSwing(1, "example@example.com").setVisible(true)); // Testing purposes
    }
}

