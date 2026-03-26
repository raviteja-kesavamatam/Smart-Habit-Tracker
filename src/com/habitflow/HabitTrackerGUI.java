package com.habitflow;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class HabitTrackerGUI extends JFrame {
    
    private JTabbedPane tabbedPane;
    private Color primaryColor = new Color(74, 144, 226);
    private Color successColor = new Color(76, 175, 80);
    private Color dangerColor = new Color(244, 67, 54);
    private Font titleFont = new Font("Arial", Font.BOLD, 16);
    private Font normalFont = new Font("Arial", Font.PLAIN, 14);

    public HabitTrackerGUI() {
        setTitle("🎯 Habit Streak Tracker");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(normalFont);
        
        // Add all tabs
        tabbedPane.addTab("👤 Users", createUserPanel());
        tabbedPane.addTab("✅ Habits", createHabitPanel());
        tabbedPane.addTab("🔥 Complete", createCompletePanel());
        tabbedPane.addTab("🃏 Joker Day", createJokerPanel());
        tabbedPane.addTab("🤖 AI Insights", createAIPanel());
        tabbedPane.addTab("📊 Analytics", createAnalyticsPanel());
        
        add(tabbedPane);
        setVisible(true);
    }

    // ==================== USER PANEL ====================
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Add User Form
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(primaryColor, 2), 
            "Add New User", 
            0, 0, titleFont, primaryColor
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JButton addBtn = createStyledButton("➕ Add User", successColor);
        
        gbc.gridx = 0; gbc.gridy = 0;
        addPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        addPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        addPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        addPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        addPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        addPanel.add(passField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        addPanel.add(addBtn, gbc);
        
        // View/Delete Users
        JPanel viewPanel = new JPanel(new BorderLayout(10, 10));
        viewPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(primaryColor, 2), 
            "User List", 
            0, 0, titleFont, primaryColor
        ));
        
        String[] columns = {"ID", "Name", "Email"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setFont(normalFont);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = createStyledButton("🔄 Refresh", primaryColor);
        JButton deleteBtn = createStyledButton("🗑️ Delete Selected", dangerColor);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(deleteBtn);
        
        viewPanel.add(scrollPane, BorderLayout.CENTER);
        viewPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(addPanel, BorderLayout.NORTH);
        panel.add(viewPanel, BorderLayout.CENTER);
        
        // Event Listeners
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword());
            
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                showMessage("Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String result = HabitTrackerBackend.addUser(name, email, pass);
            showMessage(result, "Result", JOptionPane.INFORMATION_MESSAGE);
            
            nameField.setText("");
            emailField.setText("");
            passField.setText("");
            refreshUserTable(model);
        });
        
        refreshBtn.addActionListener(e -> refreshUserTable(model));
        
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                showMessage("Please select a user to delete!", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int userId = Integer.parseInt(table.getValueAt(row, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this user?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                HabitTrackerBackend.deleteUser(userId);
                showMessage("User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshUserTable(model);
            }
        });
        
        refreshUserTable(model);
        return panel;
    }

    // ==================== HABIT PANEL ====================
    private JPanel createHabitPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Add Habit Form
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(primaryColor, 2), 
            "Add New Habit", 
            0, 0, titleFont, primaryColor
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField userIdField = new JTextField(10);
        JTextField habitNameField = new JTextField(20);
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        JButton addBtn = createStyledButton("➕ Add Habit", successColor);
        
        gbc.gridx = 0; gbc.gridy = 0;
        addPanel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        addPanel.add(userIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        addPanel.add(new JLabel("Habit Name:"), gbc);
        gbc.gridx = 1;
        addPanel.add(habitNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        addPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        addPanel.add(descScroll, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        addPanel.add(addBtn, gbc);
        
        // View Habits
        JPanel viewPanel = new JPanel(new BorderLayout(10, 10));
        viewPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(primaryColor, 2), 
            "View User Habits", 
            0, 0, titleFont, primaryColor
        ));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("User ID:"));
        JTextField searchField = new JTextField(10);
        searchPanel.add(searchField);
        JButton viewBtn = createStyledButton("👁️ View Habits", primaryColor);
        searchPanel.add(viewBtn);
        
        JTextArea habitDisplay = new JTextArea(10, 50);
        habitDisplay.setEditable(false);
        habitDisplay.setFont(normalFont);
        JScrollPane habitScroll = new JScrollPane(habitDisplay);
        
        viewPanel.add(searchPanel, BorderLayout.NORTH);
        viewPanel.add(habitScroll, BorderLayout.CENTER);
        
        panel.add(addPanel, BorderLayout.NORTH);
        panel.add(viewPanel, BorderLayout.CENTER);
        
        // Event Listeners
        addBtn.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(userIdField.getText().trim());
                String habitName = habitNameField.getText().trim();
                String desc = descArea.getText().trim();
                
                if (habitName.isEmpty()) {
                    showMessage("Please enter habit name!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                HabitTrackerBackend.addHabit(userId, habitName, desc);
                showMessage("✅ Habit added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                userIdField.setText("");
                habitNameField.setText("");
                descArea.setText("");
            } catch (NumberFormatException ex) {
                showMessage("Invalid User ID!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        viewBtn.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(searchField.getText().trim());
                habitDisplay.setText("Loading habits...\n");
                
                // Capture console output
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                java.io.PrintStream ps = new java.io.PrintStream(baos);
                java.io.PrintStream old = System.out;
                System.setOut(ps);
                
                HabitTrackerBackend.viewHabits(userId);
                
                System.out.flush();
                System.setOut(old);
                habitDisplay.setText(baos.toString());
            } catch (NumberFormatException ex) {
                showMessage("Invalid User ID!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }

    // ==================== COMPLETE HABIT PANEL ====================
    private JPanel createCompletePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel title = new JLabel("🔥 Complete Your Habit");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(primaryColor);
        
        JTextField userIdField = new JTextField(15);
        JTextField habitIdField = new JTextField(15);
        JButton completeBtn = createStyledButton("✅ Mark Complete", successColor);
        completeBtn.setPreferredSize(new Dimension(200, 40));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        panel.add(userIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Habit ID:"), gbc);
        gbc.gridx = 1;
        panel.add(habitIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(completeBtn, gbc);
        
        completeBtn.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(userIdField.getText().trim());
                int habitId = Integer.parseInt(habitIdField.getText().trim());
                
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                java.io.PrintStream ps = new java.io.PrintStream(baos);
                java.io.PrintStream old = System.out;
                System.setOut(ps);
                
                HabitTrackerBackend.completeHabit(userId, habitId);

                int missed = HabitTrackerBackend.getMissedDays(habitId);
                showMessage("Missed Days: " + missed, "Info", JOptionPane.INFORMATION_MESSAGE);
                
                System.out.flush();
                System.setOut(old);
                
                showMessage(baos.toString(), "Success! 🎉", JOptionPane.INFORMATION_MESSAGE);
                userIdField.setText("");
                habitIdField.setText("");
            } catch (NumberFormatException ex) {
                showMessage("Invalid ID format!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }

    // ==================== JOKER DAY PANEL ====================
    private JPanel createJokerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel title = new JLabel("🃏 Use Joker Day");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(255, 152, 0));
        
        JLabel subtitle = new JLabel("Skip today without breaking your streak!");
        subtitle.setFont(normalFont);
        
        JTextField userIdField = new JTextField(15);
        JTextField habitIdField = new JTextField(15);
        JButton jokerBtn = createStyledButton("🃏 Use Joker", new Color(255, 152, 0));
        jokerBtn.setPreferredSize(new Dimension(200, 40));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);
        
        gbc.gridy = 1;
        panel.add(subtitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        panel.add(userIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Habit ID:"), gbc);
        gbc.gridx = 1;
        panel.add(habitIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(jokerBtn, gbc);
        
        jokerBtn.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(userIdField.getText().trim());
                int habitId = Integer.parseInt(habitIdField.getText().trim());
                
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                java.io.PrintStream ps = new java.io.PrintStream(baos);
                java.io.PrintStream old = System.out;
                System.setOut(ps);
                
                HabitTrackerBackend.useJokerDay(userId, habitId);
                
                System.out.flush();
                System.setOut(old);
                
                showMessage(baos.toString(), "Joker Day! 🃏", JOptionPane.INFORMATION_MESSAGE);
                userIdField.setText("");
                habitIdField.setText("");
            } catch (NumberFormatException ex) {
                showMessage("Invalid ID format!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }

    // ==================== AI INSIGHTS PANEL ====================
    private JPanel createAIPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("User ID:"));
        JTextField userIdField = new JTextField(10);
        topPanel.add(userIdField);
        
        JButton suggestionBtn = createStyledButton("💡 Get Suggestions", primaryColor);
        JButton moodBtn = createStyledButton("😊 Mood Tracking", new Color(156, 39, 176));
        topPanel.add(suggestionBtn);
        topPanel.add(moodBtn);
        
        JTextArea resultArea = new JTextArea(20, 60);
        resultArea.setEditable(false);
        resultArea.setFont(normalFont);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        suggestionBtn.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(userIdField.getText().trim());
                
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                java.io.PrintStream ps = new java.io.PrintStream(baos);
                java.io.PrintStream old = System.out;
                System.setOut(ps);
                
                HabitTrackerBackend.habitSuggestions(userId);
                
                System.out.flush();
                System.setOut(old);
                resultArea.setText(baos.toString());
            } catch (NumberFormatException ex) {
                showMessage("Invalid User ID!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        moodBtn.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(userIdField.getText().trim());
                
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                java.io.PrintStream ps = new java.io.PrintStream(baos);
                java.io.PrintStream old = System.out;
                System.setOut(ps);
                
                HabitTrackerBackend.moodTracking(userId);
                
                System.out.flush();
                System.setOut(old);
                resultArea.setText(baos.toString());
            } catch (NumberFormatException ex) {
                showMessage("Invalid User ID!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }

    // ==================== ANALYTICS PANEL ====================
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton progressBtn = createStyledButton("📊 Progress Board", primaryColor);
        JButton leaderboardBtn = createStyledButton("🏅 Leaderboard", successColor);
        buttonPanel.add(progressBtn);
        buttonPanel.add(leaderboardBtn);
        
        JTextArea resultArea = new JTextArea(25, 70);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        progressBtn.addActionListener(e -> {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream ps = new java.io.PrintStream(baos);
            java.io.PrintStream old = System.out;
            System.setOut(ps);
            
            HabitTrackerBackend.progressBoard();
            
            System.out.flush();
            System.setOut(old);
            resultArea.setText(baos.toString());
        });
        
        leaderboardBtn.addActionListener(e -> {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream ps = new java.io.PrintStream(baos);
            java.io.PrintStream old = System.out;
            System.setOut(ps);
            
            HabitTrackerBackend.leaderboard();
            
            System.out.flush();
            System.setOut(old);
            resultArea.setText(baos.toString());
        });
        
        return panel;
    }

    // ==================== HELPER METHODS ====================
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(normalFont);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 35));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }

    private void refreshUserTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<String[]> users = HabitTrackerBackend.getAllUsersAsArray();
        for (String[] user : users) {
            model.addRow(user);
        }
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    // ==================== MAIN ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new HabitTrackerGUI();
        });
    }
}
