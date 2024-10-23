package p1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewGroupSwing extends JFrame {

    private JTable groupTable;
    private DefaultTableModel tableModel;
    private String userEmail;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test"; // Update with your DB name
    private static final String USER = "root"; // Your MySQL username
    private static final String PASS = ""; // Your MySQL password
    private HashMap<String, ArrayList<String[]>> groupInfoDB; // Ensure this is correctly declared

    public ViewGroupSwing(String userEmail, HashMap<String, ArrayList<String[]>> groupInfoDB) {
        this.userEmail = userEmail;

        setTitle("View Groups");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Create table model with column names
        String[] columnNames = {"Group ID", "Project Topic", "Members", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0);
        groupTable = new JTable(tableModel);
        groupTable.setRowHeight(30);
        groupTable.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Populate the table with group data
        populateTable();

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(groupTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add a back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            this.setVisible(false); // Hide current window
            new HomeSwing(userEmail).setVisible(true); // Open HomeSwing
        });
        add(backButton, BorderLayout.SOUTH);
    }

    private void populateTable() {
        HashMap<Integer, String> groupMap = new HashMap<>(); // Map to hold group IDs and topics
        String query = "SELECT g.groupid, g.projecttopic FROM groupinfo g JOIN studentinfo s ON g.groupid = s.groupid WHERE s.smail = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int groupId = rs.getInt("groupid");
                String projectTopic = rs.getString("projecttopic");
                groupMap.put(groupId, projectTopic);
                
                // Now retrieve members for each group
                addGroupMembersToTable(groupId, projectTopic);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addGroupMembersToTable(int groupId, String projectTopic) {
        String query = "SELECT s.sname, s.smail FROM studentinfo s WHERE s.groupid = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            StringBuilder memberList = new StringBuilder();

            while (rs.next()) {
                String studentName = rs.getString("sname");
                String studentEmail = rs.getString("smail");
                memberList.append(studentName).append(" (").append(studentEmail).append("), ");
            }

            if (memberList.length() > 0) {
                memberList.setLength(memberList.length() - 2); // Remove the last comma and space
            }

            // Add row to the table with group ID, project topic, and members
            Object[] rowData = {groupId, projectTopic, memberList.toString(), createActionPanel(groupId)};
            tableModel.addRow(rowData);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private JPanel createActionPanel(int groupId) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open edit dialog or new frame for editing group details
                new EditGroupSwing(groupId, userEmail).setVisible(true);
            }
        });
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteGroup(groupId);
            }
        });

        panel.add(editButton);
        panel.add(deleteButton);
        return panel;
    }

    private void deleteGroup(int groupId) {
        int confirmation = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this group?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            String deleteQuery = "DELETE FROM groupinfo WHERE groupid = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                 
                stmt.setInt(1, groupId);
                stmt.executeUpdate();
                // Refresh the table
                tableModel.setRowCount(0); // Clear the current table data
                populateTable(); // Repopulate the table
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
    // For testing, create a sample HashMap for group information
    HashMap<String, ArrayList<String[]>> sampleGroupData = new HashMap<>();
    ArrayList<String[]> members = new ArrayList<>();
    members.add(new String[]{"Alice", "alice@example.com"});
    members.add(new String[]{"Bob", "bob@example.com"});
    sampleGroupData.put("Group 1 (Project A)", members);

    SwingUtilities.invokeLater(() -> new ViewGroupSwing("testuser@example.com", sampleGroupData).setVisible(true));
}
}

