package com.habitflow;

import java.util.*;

public class HabitTrackerBackend {

    static class User {
        int id;
        String name, email, password;

        User(int id, String name, String email, String password) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.password = password;
        }
    }

    static class Habit {
        int id, userId;
        String name, description;

        Habit(int id, int userId, String name, String description) {
            this.id = id;
            this.userId = userId;
            this.name = name;
            this.description = description;
        }
    }

    static List<User> users = new ArrayList<>();
    static List<Habit> habits = new ArrayList<>();
    static Map<Integer, Integer> missedDays = new HashMap<>();

    static int userCounter = 1;
    static int habitCounter = 1;

    // USERS
    public static String addUser(String name, String email, String password) {
        for (User u : users) {
            if (u.email.equals(email)) return "❌ User already exists!";
        }
        users.add(new User(userCounter++, name, email, password));
        return "✅ User added!";
    }

    public static List<String[]> getAllUsersAsArray() {
        List<String[]> list = new ArrayList<>();
        for (User u : users) {
            list.add(new String[]{
                String.valueOf(u.id), u.name, u.email
            });
        }
        return list;
    }

    public static void deleteUser(int userId) {
        users.removeIf(u -> u.id == userId);
    }

    // HABITS
    public static void addHabit(int userId, String name, String desc) {
        habits.add(new Habit(habitCounter++, userId, name, desc));
        System.out.println("✅ Habit added!");
    }

    public static void viewHabits(int userId) {
        System.out.println("\n--- HABITS ---");
        for (Habit h : habits) {
            if (h.userId == userId) {
                System.out.println(h.id + " | " + h.name + " | " + h.description);
            }
        }
    }

    // FEATURES
    public static void completeHabit(int userId, int habitId) {
        missedDays.put(habitId, 0);
        System.out.println("🔥 Habit completed! Missed days reset.");
    }

    public static void useJokerDay(int userId, int habitId) {
        int missed = missedDays.getOrDefault(habitId, 0);
        missedDays.put(habitId, missed + 1);
        System.out.println("🃏 Joker used! Missed days: " + (missed + 1));
    }

    public static int getMissedDays(int habitId) {
        return missedDays.getOrDefault(habitId, 0);
    }

    public static void habitSuggestions(int userId) {
        System.out.println("🤖 Try improving consistency!");
    }

    public static void moodTracking(int userId) {
        System.out.println("😊 Mood tracking active!");
    }

    public static void progressBoard() {
        System.out.println("📊 Progress feature coming soon!");
    }

    public static void leaderboard() {
        System.out.println("🏅 Leaderboard coming soon!");
    }
}