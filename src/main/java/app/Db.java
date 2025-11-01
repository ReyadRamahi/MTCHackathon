package app;

import java.sql.*;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

public class Db {

    // Call this where you create schema:
    // CREATE TABLE IF NOT EXISTS users(
    //   id INTEGER PRIMARY KEY AUTOINCREMENT,
    //   email TEXT UNIQUE NOT NULL,
    //   display_name TEXT NOT NULL,
    //   password_hash TEXT NOT NULL,
    //   role TEXT NOT NULL
    //);

    public static boolean emailAvailable(String email) throws SQLException {
        try (var conn = get(); var ps = conn.prepareStatement("SELECT 1 FROM users WHERE email = ?")) {
            ps.setString(1, email);
            try (var rs = ps.executeQuery()) { return !rs.next(); }
        }
    }

    public static void insertUser(String email, String displayName, String rawPassword, Role role) throws SQLException {
        // Hash password before insert — replace with your hashing if needed
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        try (var conn = get();
             var ps = conn.prepareStatement(
                     "INSERT INTO users(email, display_name, password_hash, role) VALUES(?,?,?,?)")) {
            ps.setString(1, email);
            ps.setString(2, (displayName == null || displayName.isBlank()) ? "anon" : displayName.trim());
            ps.setString(3, hash);
            ps.setString(4, role.name());
            ps.executeUpdate();
        }
    }

    public static User findUserByEmail(String email) throws SQLException {
        try (var conn = get(); var ps = conn.prepareStatement(
                "SELECT id, email, display_name, role FROM users WHERE email = ?")) {
            ps.setString(1, email);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapUser(rs);
            }
        }
    }

    private static User mapUser(ResultSet rs) throws SQLException {
        // id from DB is int; our User.id is the browser uid – for DB user objects
        // we only need displayName + role to mirror into the session.
        String display = rs.getString("display_name");
        Role role = Role.valueOf(rs.getString("role"));
        // The session layer will copy display/role onto the browser-uid.
        return new User("db", display, role);
    }

    // You already had this in your file:
    public static boolean checkPassword(String email, String rawPassword) throws SQLException {
        try (var conn = get(); var ps = conn.prepareStatement(
                "SELECT password_hash FROM users WHERE email = ?")) {
            ps.setString(1, email);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                String hashed = rs.getString("password_hash");
                return BCrypt.checkpw(rawPassword, hashed);
            }
        }
    }

    // Your connection helper (adjust to your project)
    private static Connection get() throws SQLException {
        // example: return DriverManager.getConnection("jdbc:sqlite:app.db");
        return DriverManager.getConnection("jdbc:sqlite:app.db");
    }
}