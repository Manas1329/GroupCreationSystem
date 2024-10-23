package p1;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class ProjectForm extends JFrame implements ActionListener {
    
    private String userEmail; // Field to store the email
    // Define form components
    JTextField dateField;
    JTextField subjectField, classField, projectTopicField;
    JComboBox<String> numberOfMembersCombo;
    JTable memberTable;
    JButton submitButton, backButton, clearButton; // Removed viewDataButton
    
    // Define a table model to hold the data
    DefaultTableModel tableModel;

    // Database connection details
    static final String DB_URL = "jdbc:mysql://localhost:3306/test";  // Change DB URL as per your config
    static final String USER = "root"; // Change it if your MySQL username is different
    static final String PASS = ""; // Add your MySQL password here

    public ProjectForm(String userEmail) {
        this.userEmail = userEmail;
        // Set up JFrame
        setTitle("Project Form");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        
        // Number of Members Dropdown
        JLabel membersLabel = new JLabel("No. of Members:");
        membersLabel.setBounds(20, 20, 120, 25);
        add(membersLabel);
        
        String[] membersOptions = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        numberOfMembersCombo = new JComboBox<>(membersOptions);
        numberOfMembersCombo.setBounds(150, 20, 50, 25);
        numberOfMembersCombo.addActionListener(this); // Add ActionListener
        add(numberOfMembersCombo);
        
        // Date Field
        JLabel dateLabel = new JLabel("Date: (dd-mm-yyyy)");
        dateLabel.setBounds(250, 20, 150, 25);
        add(dateLabel);
        
        dateField = new JTextField();
        dateField.setBounds(400, 20, 150, 25);
        add(dateField);
        
        // Subject Field
        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setBounds(20, 60, 100, 25);
        add(subjectLabel);
        
        subjectField = new JTextField();
        subjectField.setBounds(130, 60, 150, 25);
        add(subjectField);
        
        // Class Field
        JLabel classLabel = new JLabel("Class:");
        classLabel.setBounds(20, 100, 100, 25);
        add(classLabel);
        
        classField = new JTextField();
        classField.setBounds(130, 100, 150, 25);
        add(classField);
        
        // Project Topic Field
        JLabel projectTopicLabel = new JLabel("Project Topic:");
        projectTopicLabel.setBounds(20, 140, 100, 25);
        add(projectTopicLabel);
        
        projectTopicField = new JTextField();
        projectTopicField.setBounds(130, 140, 300, 25);
        add(projectTopicField);
     
        // Red Note: Leader Details First!!!
        JLabel noteLabel = new JLabel("Leader Details First!!!");
        noteLabel.setBounds(130, 200, 200, 25);
        noteLabel.setForeground(Color.RED); // Set text color to red
        add(noteLabel);
               
        // Member Table
        String[] columnNames = {"Sr.", "Name", "Email", "14-Digit No.", "Batch", "Roll no", "Gender(M/F)"};
        tableModel = new DefaultTableModel(columnNames, 0); // Initialize with 0 rows
        memberTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBounds(20, 230, 550, 100);
        add(scrollPane);
        
        // Submit Button
        submitButton = new JButton("Submit");
        submitButton.setBounds(350, 340, 100, 30);
        submitButton.addActionListener(this);
        add(submitButton);

        // Back Button (to return to Login/Registration Page)
        backButton = new JButton("Back");
        backButton.setBounds(460, 340, 100, 30);
        backButton.addActionListener(this);
        add(backButton);
        
        // Clear Button
        clearButton = new JButton("Clear");
        clearButton.setBounds(240, 340, 100, 30);
        clearButton.addActionListener(this);
        add(clearButton);
        
        // Update the table based on the default selected number of members
        updateMemberTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == numberOfMembersCombo) {
            // Update the member table when the number of members changes
            updateMemberTable();
        } else if (e.getSource() == submitButton) {
            // Perform form submission action
            submitForm();
        } else if (e.getSource() == backButton) {
            // Navigate back to the login page
            this.setVisible(false);
            new HomeSwing(userEmail).setVisible(true); // Return to login page
        } else if (e.getSource() == clearButton) {
            // Clear all fields and table data
            clearForm();
        }
    }

    private void updateMemberTable() {
        // Get the selected number of members
        int selectedIndex = numberOfMembersCombo.getSelectedIndex();
        int numberOfMembers = selectedIndex + 1; // ComboBox index starts from 0

        // Clear the existing rows
        tableModel.setRowCount(0);
        
        // Add rows based on the selected number of members
        for (int i = 0; i < numberOfMembers; i++) {
            tableModel.addRow(new Object[]{i + 1, "", "", "", "", "", ""}); // Empty fields for user input
        }
    }

    private void submitForm() {
    // Get form data
    String subject = subjectField.getText();
    String className = classField.getText();
    String projectTopic = projectTopicField.getText();
    String dateInput = dateField.getText();

    // Validate input
    if (subject.isEmpty() || className.isEmpty() || projectTopic.isEmpty() || dateInput.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Validate date format
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    dateFormat.setLenient(false); // Prevents lenient parsing

    Date date1;
    try {
        date1 = dateFormat.parse(dateInput); // Parse the date
    } catch (ParseException e) {
        JOptionPane.showMessageDialog(this, "Please enter the date in the format DD-MM-YYYY.", "Date Format Error", JOptionPane.ERROR_MESSAGE);
        return; // Exit if the date format is incorrect
    }

    // Insert data into the database
    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
        // Insert group details into groupinfo table
        String insertGroupSQL = "INSERT INTO groupinfo (noofmem, date, class, subject, projecttopic) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement groupStatement = conn.prepareStatement(insertGroupSQL, Statement.RETURN_GENERATED_KEYS);

        groupStatement.setInt(1, numberOfMembersCombo.getSelectedIndex() + 1); // Number of members
        groupStatement.setDate(2, new java.sql.Date(date1.getTime())); // Date
        groupStatement.setString(3, className); // Class
        groupStatement.setString(4, subject); // Subject
        groupStatement.setString(5, projectTopic); // Project topic

        // Execute group insertion and retrieve the generated groupid
        int affectedRows = groupStatement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Inserting group failed, no rows affected.");
        }

        try (ResultSet generatedKeys = groupStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                int groupId = generatedKeys.getInt(1); // Get the generated groupid

                // Insert each student's details into studentinfo table
                String insertStudentSQL = "INSERT INTO studentinfo (groupid, sname, smail, prn, batch, rollno, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement studentStatement = conn.prepareStatement(insertStudentSQL);

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    studentStatement.setInt(1, groupId); // Use the retrieved groupid
                    studentStatement.setString(2, (String) tableModel.getValueAt(i, 1)); // Name
                    studentStatement.setString(3, (String) tableModel.getValueAt(i, 2)); // Email
                    studentStatement.setString(4, (String) tableModel.getValueAt(i, 3)); // PRN (14-digit number)
                    studentStatement.setString(5, (String) tableModel.getValueAt(i, 4)); // Batch
                    studentStatement.setInt(6, Integer.parseInt((String) tableModel.getValueAt(i, 5))); // Roll No
                    studentStatement.setString(7, (String) tableModel.getValueAt(i, 6)); // Gender

                    // Execute the student insertion query
                    studentStatement.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Project details and student information submitted successfully!");
            } else {
                throw new SQLException("Inserting group failed, no ID obtained.");
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error while inserting data into the database.\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}


    // Method to clear all the fields and table
    private void clearForm() {
        // Clear the text fields
        subjectField.setText("");
        classField.setText("");
        projectTopicField.setText("");
        dateField.setText(""); // Clear the email field
        
        // Clear the table data
        tableModel.setRowCount(0);
        
        // Reset the member table based on the number of members selected
        updateMemberTable();
    }

    public static void main(String[] args) {
        new ProjectForm("example@example.com").setVisible(true);
    }
}

