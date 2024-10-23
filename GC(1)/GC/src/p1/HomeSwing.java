package p1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeSwing extends JFrame implements ActionListener {
    JLabel welcomeLabel, emailLabel;
    JButton createGroupButton, viewGroupButton, logoutButton;
    String userEmail;
    static final String DB_URL = "jdbc:mysql://localhost:3306/test?"; // Replace with your database name
    static final String USER = "root"; // Replace with your MySQL username
    static final String PASS = ""; // Replace with your MySQL password

    public HomeSwing(String email) {
        this.userEmail = email;

        // Set up the JFrame
        setTitle("Home Page");
        setLayout(null);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 204, 102));

        // Welcome Label
        if (RegistrationPage.logininfo != null && RegistrationPage.logininfo.containsKey(email)) {
            String firstName = RegistrationPage.logininfo.get(email)[0];
            welcomeLabel = new JLabel("Welcome, " + firstName + "!", JLabel.CENTER);
        } else {
            welcomeLabel = new JLabel("Welcome!", JLabel.CENTER);
        }
        
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setBounds(90, 30, 200, 40);
        add(welcomeLabel);

        // Email Label
        emailLabel = new JLabel("Email: " + email, JLabel.CENTER);
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailLabel.setBounds(100, 80, 200, 20);
        add(emailLabel);

        // Create Group Button
        createGroupButton = new JButton("Create Group");
        createGroupButton.setBounds(120, 120, 150, 30);
        createGroupButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        createGroupButton.addActionListener(this);
        add(createGroupButton);

        // View Group Button
        viewGroupButton = new JButton("View Groups");
        viewGroupButton.setBounds(120, 160, 150, 30);
        viewGroupButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        viewGroupButton.addActionListener(this);
        add(viewGroupButton);

        // Logout Button
        logoutButton = new JButton("Logout");
        logoutButton.setBounds(120, 200, 150, 30);
        logoutButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logoutButton.addActionListener(this);
        add(logoutButton);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createGroupButton) {
            this.setVisible(false);
            new ProjectForm(userEmail).setVisible(true); // Open project form to create a group
        } else if (e.getSource() == viewGroupButton) {
            this.setVisible(false);
            HashMap<String, ArrayList<String[]>> groupData = getGroupData();
            if (groupData.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No groups available.", "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                new ViewGroupSwing(userEmail, groupData).setVisible(true); // Open view group form
            }
        } else if (e.getSource() == logoutButton) {
            this.setVisible(false);
            new LoginApplet().setVisible(true); // Go back to login page
        }
    }

    private HashMap<String, ArrayList<String[]>> getGroupData() {
        HashMap<String, ArrayList<String[]>> groupData = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Retrieve all group information
            String query = "SELECT g.groupid, g.projecttopic, s.sname, s.smail FROM groupinfo g JOIN studentinfo s ON g.groupid = s.groupid ORDER BY g.groupid, s.srno";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            int lastGroupId = -1;
            ArrayList<String[]> members = null;

            while (rs.next()) {
                int groupId = rs.getInt("groupid");
                String projectTopic = rs.getString("projecttopic");
                String studentName = rs.getString("sname");
                String studentEmail = rs.getString("smail");

                // If it's a new group, create a new list for its members
                if (groupId != lastGroupId) {
                    if (lastGroupId != -1) {
                        groupData.put("Group " + lastGroupId + " (" + projectTopic + ")", members);
                    }
                    members = new ArrayList<>();
                    lastGroupId = groupId;
                }
                members.add(new String[]{studentName, studentEmail});
            }

            // Add the last group
            if (members != null) {
                groupData.put("Group " + lastGroupId, members);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error retrieving group data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        return groupData;
    }

    public static void main(String[] args) {
        new HomeSwing("example@example.com").setVisible(true); // Testing purposes
    }
}
