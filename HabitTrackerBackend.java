import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class HabitTrackerBackend {
    private static final String URL = "jdbc:mysql://localhost:3306/habittracker_app";
    private static final String USER = "root"; 
    private static final String PASSWORD = "1970"; 

    // ✅ Get DB Connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ------------------- USERS -------------------
    public static String addUser(String name, String email, String password) {
    String check = "SELECT * FROM users WHERE email=?";
    String insert = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
    try (Connection conn = getConnection()) {
        PreparedStatement checkStmt = conn.prepareStatement(check);
        checkStmt.setString(1, email);
        ResultSet rs = checkStmt.executeQuery();
        if (rs.next()) {
            return "❌ User already exists with that email!";
        }
        PreparedStatement stmt = conn.prepareStatement(insert);
        stmt.setString(1, name);
        stmt.setString(2, email);
        stmt.setString(3, password);
        stmt.executeUpdate();
        return "✅ User added!";
    } catch (SQLException e) {
        e.printStackTrace();
        return "❌ Error: " + e.getMessage();
    }
}



    public static void viewUsers() {
        String query = "SELECT * FROM users";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n--- USERS ---");
            while (rs.next()) {
                System.out.println(rs.getInt("user_id") + " | " + rs.getString("name") + " | " + rs.getString("email"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public static List<String[]> getAllUsersAsArray() {
    List<String[]> usersList = new ArrayList<>();
    String query = "SELECT user_id, name, email FROM users";
    try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            usersList.add(new String[] {
                String.valueOf(rs.getInt("user_id")),
                rs.getString("name"),
                rs.getString("email")
            });
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return usersList;
}


    // ------------------- HABITS -------------------
    public static void addHabit(int userId, String habitName, String description) {
        String query = "INSERT INTO habits (user_id, habit_name, description) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, habitName);
            stmt.setString(3, description);
            stmt.executeUpdate();
            System.out.println("✅ Habit added!");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void viewHabits(int userId) {
        String query = "SELECT * FROM habits WHERE user_id=?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n--- HABITS ---");
            while (rs.next()) {
                System.out.println(rs.getInt("habit_id") + " | " + rs.getString("habit_name") + " | " + rs.getString("description"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ------------------- GAMIFICATION -------------------
    public static void completeHabit(int userId, int habitId) {
        String query = "INSERT INTO habit_logs (habit_id, log_date, status) VALUES (?, CURDATE(), 'completed')";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, habitId);
            stmt.executeUpdate();
            System.out.println("✅ Habit completed!");

            updateStreak(userId, habitId);
            giveReward(userId, 10);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void useJokerDay(int userId, int habitId) {
        String query = "INSERT INTO habit_logs (habit_id, log_date, status) VALUES (?, CURDATE(), 'joker')";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, habitId);
            stmt.executeUpdate();
            System.out.println("🃏 Joker Day used! Streak safe.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void updateStreak(int userId, int habitId) {
        String check = "SELECT * FROM streaks WHERE user_id=? AND habit_id=?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(check)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, habitId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int streak = rs.getInt("streak_count") + 1;
                String upd = "UPDATE streaks SET streak_count=?, last_updated=CURDATE() WHERE user_id=? AND habit_id=?";
                try (PreparedStatement st = conn.prepareStatement(upd)) {
                    st.setInt(1, streak); st.setInt(2, userId); st.setInt(3, habitId);
                    st.executeUpdate();
                    System.out.println("🔥 Streak: " + streak + " days!");
                    checkBadges(userId, streak);
                }
            } else {
                String ins = "INSERT INTO streaks (user_id, habit_id, streak_count, last_updated) VALUES (?, ?, 1, CURDATE())";
                try (PreparedStatement st = conn.prepareStatement(ins)) {
                    st.setInt(1, userId); st.setInt(2, habitId);
                    st.executeUpdate();
                    System.out.println("🔥 Streak started: 1 day!");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void checkBadges(int userId, int streak) {
        String badge = null;
        if (streak == 7) badge = "1 Week Champion";
        else if (streak == 30) badge = "1 Month Hero";
        else if (streak == 100) badge = "Century Legend";

        if (badge != null) {
            String query = "INSERT INTO rewards (user_id, reward_type, reward_desc) VALUES (?, 'badge', ?)";
            try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setString(2, badge);
                stmt.executeUpdate();
                System.out.println("🏆 Badge earned: " + badge);
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private static void giveReward(int userId, int points) {
        String query = "INSERT INTO rewards (user_id, reward_type, reward_desc) VALUES (?, 'points', ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, points + " points");
            stmt.executeUpdate();
            System.out.println("💰 +" + points + " points!");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ------------------- AI INSIGHTS -------------------
    public static void habitSuggestions(int userId) {
        String query = "SELECT habit_name, COUNT(*) as misses FROM habits h " +
                       "JOIN habit_logs l ON h.habit_id=l.habit_id " +
                       "WHERE h.user_id=? AND l.status='missed' GROUP BY h.habit_id ORDER BY misses DESC LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("🤖 Suggestion: Try improving your habit '" + rs.getString("habit_name") + "'.");
            } else {
                System.out.println("🤖 No missed habits found!");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void moodTracking(int userId) {
        String query = "SELECT l.log_date, l.status, a.insight_text FROM habit_logs l " +
                       "JOIN ai_insights a ON l.habit_id=a.habit_id " +
                       "JOIN habits h ON h.habit_id=l.habit_id WHERE h.user_id=?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n🤖 Mood-Habit Tracking:");
            while (rs.next()) {
                System.out.println(rs.getDate("log_date") + " | " + rs.getString("status") + " | " + rs.getString("insight_text"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ------------------- VISUALIZATIONS -------------------
    public static void progressBoard() {
        String query = "SELECT u.name, COUNT(l.log_id) as completed FROM users u " +
                       "JOIN habits h ON u.user_id=h.user_id " +
                       "JOIN habit_logs l ON h.habit_id=l.habit_id " +
                       "WHERE l.status='completed' GROUP BY u.user_id ORDER BY completed DESC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n📊 Progress Board:");
            while (rs.next()) {
                System.out.println(rs.getString("name") + " | " + rs.getInt("completed") + " completions");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void leaderboard() {
        String query = "SELECT u.name, MAX(s.streak_count) as max_streak FROM users u " +
                       "JOIN streaks s ON u.user_id=s.user_id GROUP BY u.user_id ORDER BY max_streak DESC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n🏅 Leaderboard:");
            while (rs.next()) {
                System.out.println(rs.getString("name") + " | " + rs.getInt("max_streak") + " days streak");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ------------------- MAIN MENU -------------------
    // Add this method to your class:
public static void deleteUser(int userId) {
    String query = "DELETE FROM users WHERE user_id = ?";
    try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, userId);
        int rows = stmt.executeUpdate();
        if (rows > 0) {
            System.out.println("🗑️ User deleted!");
        } else {
            System.out.println("❌ User ID not found.");
        }
    } catch (SQLException e) { e.printStackTrace(); }
}

// Your revised main method:
public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    while (true) {
        System.out.println("\n--- Habit Streak Tracker ---");
        System.out.println("1. Add User");
        System.out.println("2. View Users");
        System.out.println("3. Add Habit");
        System.out.println("4. View Habits");
        System.out.println("5. Complete Habit");
        System.out.println("6. Use Joker Day");
        System.out.println("7. AI Suggestions");
        System.out.println("8. Mood Tracking");
        System.out.println("9. Progress Board");
        System.out.println("10. Leaderboard");
        System.out.println("11. Delete User");
        System.out.println("12. Exit");
        System.out.print("Choose: ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("Enter name: ");
                String name = sc.nextLine();
                System.out.print("Enter email: ");
                String email = sc.nextLine();
                System.out.print("Enter password: ");
                String pass = sc.nextLine();
                addUser(name, email, pass);
            }
            case 2 -> viewUsers();
            case 3 -> {
                System.out.print("Enter User ID: ");
                int uid = sc.nextInt(); sc.nextLine();
                System.out.print("Enter Habit Name: ");
                String hname = sc.nextLine();
                System.out.print("Enter Habit Description: ");
                String desc = sc.nextLine();
                addHabit(uid, hname, desc);
            }
            case 4 -> {
                System.out.print("Enter User ID: ");
                int uid = sc.nextInt();
                viewHabits(uid);
            }
            case 5 -> {
                System.out.print("Enter User ID: ");
                int uid = sc.nextInt();
                System.out.print("Enter Habit ID: ");
                int hid = sc.nextInt();
                completeHabit(uid, hid);
            }
            case 6 -> {
                System.out.print("Enter User ID: ");
                int uid = sc.nextInt();
                System.out.print("Enter Habit ID: ");
                int hid = sc.nextInt();
                useJokerDay(uid, hid);
            }
            case 7 -> {
                System.out.print("Enter User ID: ");
                int uid = sc.nextInt();
                habitSuggestions(uid);
            }
            case 8 -> {
                System.out.print("Enter User ID: ");
                int uid = sc.nextInt();
                moodTracking(uid);
            }
            case 9 -> progressBoard();
            case 10 -> leaderboard();
            case 11 -> {
                System.out.print("Enter User ID to delete: ");
                int uid = sc.nextInt();
                deleteUser(uid);
            }
            case 12 -> { System.out.println("👋 Exiting..."); return; }
            default -> System.out.println("❌ Invalid choice!");
        }
    }
}
}

