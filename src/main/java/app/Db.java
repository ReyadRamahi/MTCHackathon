package app;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class Db {
    private static Connection conn;

    public static void init(String filePath) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + filePath);
            try (var st = conn.createStatement()) {
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      email TEXT UNIQUE NOT NULL,
                      display_name TEXT NOT NULL,
                      password_hash TEXT NOT NULL,
                      role TEXT NOT NULL DEFAULT 'USER',
                      created_at TEXT NOT NULL
                    )""");
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS answers(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      post_id INTEGER NOT NULL,
                      user_id INTEGER NOT NULL,
                      body TEXT NOT NULL,
                      created_at TEXT NOT NULL
                    )""");
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static int createUser(String email, String displayName, String rawPassword, Role role) throws SQLException {
        var hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        try (var ps = conn.prepareStatement(
                "INSERT INTO users(email, display_name, password_hash, role, created_at) VALUES (?,?,?,?,datetime('now'))",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email.trim().toLowerCase());
            ps.setString(2, displayName.trim());
            ps.setString(3, hash);
            ps.setString(4, role.name());
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) { return rs.next() ? rs.getInt(1) : -1; }
        }
    }

    public static User findUserByEmail(String email) throws SQLException {
        try (var ps = conn.prepareStatement("SELECT * FROM users WHERE email=?")) {
            ps.setString(1, email.trim().toLowerCase());
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapUser(rs);
            }
        }
    }

    public static User findUserById(int id) throws SQLException {
        try (var ps = conn.prepareStatement("SELECT * FROM users WHERE id=?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapUser(rs);
            }
        }
    }

    public static boolean checkPassword(String email, String rawPassword) throws SQLException {
        try (var ps = conn.prepareStatement("SELECT password_hash FROM users WHERE email=?")) {
            ps.setString(1, email.trim().toLowerCase());
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                return BCrypt.checkpw(rawPassword, rs.getString("password_hash"));
            }
        }
    }

    private static User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("display_name"),
                Role.valueOf(rs.getString("role"))
        );
    }

    public static void insertAnswer(int postId, int userId, String body) throws SQLException {
        try (var ps = conn.prepareStatement(
                "INSERT INTO answers(post_id,user_id,body,created_at) VALUES (?,?,?,datetime('now'))")) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            ps.setString(3, body);
            ps.executeUpdate();
        }
    }
}