// Db.java
package app;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
import java.util.*;

public class Db {
    private static final String URL = "jdbc:sqlite:app.db";

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // call once on boot
    public static void init() throws SQLException {
        try (var conn = get(); var st = conn.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users(
                  id            INTEGER PRIMARY KEY AUTOINCREMENT,
                  email         TEXT    NOT NULL UNIQUE,
                  display_name  TEXT    NOT NULL,
                  password_hash TEXT    NOT NULL,
                  role          TEXT    NOT NULL,
                  created_at    TEXT    NOT NULL DEFAULT (datetime('now'))
                )
            """);
            st.executeUpdate("""
            CREATE TABLE verification_requests(
              id            INTEGER PRIMARY KEY AUTOINCREMENT,
              uid           TEXT    NOT NULL,              -- browser/session uid (owner)
              email         TEXT,                          -- optional contact
              note          TEXT,                          -- user-provided details
              file_path     TEXT,                          -- server-side stored path
              file_name     TEXT,                          -- original filename
              file_sha256   TEXT,                          -- integrity check
              status        TEXT    NOT NULL DEFAULT 'pending',  -- pending|approved|rejected
              created_at    TEXT    NOT NULL DEFAULT (datetime('now')),
              decided_at    TEXT                           -- when approved/rejected
            """);
        }
    }

    public static boolean emailAvailable(String email) throws SQLException {
        try (var conn = get();
             var ps = conn.prepareStatement("SELECT 1 FROM users WHERE email=?")) {
            ps.setString(1, email);
            try (var rs = ps.executeQuery()) { return !rs.next(); }
        }
    }

    public static void insertUser(String email, String displayName, String rawPassword, Role role)
            throws SQLException {
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        try (var conn = get();
             var ps = conn.prepareStatement(
                     "INSERT INTO users(email,display_name,password_hash,role) VALUES (?,?,?,?)")) {
            ps.setString(1, email);
            ps.setString(2, (displayName == null || displayName.isBlank()) ? "anon" : displayName.trim());
            ps.setString(3, hash);
            ps.setString(4, role.name());
            ps.executeUpdate();
        }
    }

    public static User findUserByEmail(String email) throws SQLException {
        try (var conn = get();
             var ps = conn.prepareStatement("SELECT id,email,display_name,role FROM users WHERE email=?")) {
            ps.setString(1, email);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new User(
                        String.valueOf(rs.getInt("id")),
                        rs.getString("display_name"),
                        Role.valueOf(rs.getString("role"))
                );
            }
        }
    }

    public static boolean checkPassword(String email, String rawPassword) throws SQLException {
        try (var conn = get();
             var ps = conn.prepareStatement("SELECT password_hash FROM users WHERE email=?")) {
            ps.setString(1, email);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                return BCrypt.checkpw(rawPassword, rs.getString("password_hash"));
            }
        }
    }
    //helpers for diploma verification
    public static void insertVerificationRequest(
            String uid, String email, String note,
            String filePath, String fileName, String sha256
    ) throws SQLException {
        try (var conn = get(); var ps = conn.prepareStatement(
                "INSERT INTO verification_requests(uid,email,note,file_path,file_name,file_sha256) VALUES(?,?,?,?,?,?)"
        )) {
            ps.setString(1, uid);
            ps.setString(2, email);
            ps.setString(3, note);
            ps.setString(4, filePath);
            ps.setString(5, fileName);
            ps.setString(6, sha256);
            ps.executeUpdate();
        }
    }

    // --- Verification admin helpers ---

    public static java.util.List<java.util.Map<String,Object>> listPendingRequests() throws SQLException {
        try (var conn = get();
             var ps   = conn.prepareStatement(
                     "SELECT id, uid, email, note, file_name, created_at " +
                             "FROM verification_requests WHERE status='pending' ORDER BY created_at ASC")) {
            try (var rs = ps.executeQuery()) {
                var out = new java.util.ArrayList<java.util.Map<String,Object>>();
                while (rs.next()) {
                    out.add(java.util.Map.of(
                            "id",        rs.getInt("id"),
                            "uid",       rs.getString("uid"),
                            "email",     rs.getString("email"),
                            "note",      rs.getString("note"),
                            "file_name", rs.getString("file_name"),
                            "created_at",rs.getString("created_at")
                    ));
                }
                return out;
            }
        }
    }

    public static void approveVerification(int requestId, String reviewerUid) throws SQLException {
        // 1) Get the uid from the verification request
        String uid;
        try (var conn = get();
             var ps = conn.prepareStatement("SELECT uid FROM verification_requests WHERE id=? AND status='pending'")) {
            ps.setInt(1, requestId);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return; // already processed or not found
                uid = rs.getString("uid");
            }
        }

        // 2) Mark the request as approved
        try (var conn = get();
             var ps = conn.prepareStatement(
                     "UPDATE verification_requests SET status='approved', decided_at=CURRENT_TIMESTAMP WHERE id=?")) {
            ps.setInt(1, requestId);
            ps.executeUpdate();
        }

        // 3) IMPORTANT: Promote the user in SessionStore (in-memory)
        //    Since your users are identified by browser UID, not DB id
        SessionStore.promoteToScholar(uid);
    }

    public static void rejectVerification(int requestId, String reviewerUid) throws SQLException {
        try (var conn = get();
             var ps = conn.prepareStatement(
                     "UPDATE verification_requests SET status='rejected', decided_at=CURRENT_TIMESTAMP WHERE id=? AND status='pending'")) {
            ps.setInt(1, requestId);
            ps.executeUpdate();
        }
    }

    public static Map<String,String> getRequest(int id) throws SQLException {
        try (var conn = get(); var ps = conn.prepareStatement(
                "SELECT * FROM verification_requests WHERE id=?"
        )) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return Map.of(
                        "id", String.valueOf(rs.getInt("id")),
                        "uid", rs.getString("uid"),
                        "email", rs.getString("email"),
                        "note", rs.getString("note"),
                        "file_path", rs.getString("file_path"),
                        "file_name", rs.getString("file_name")
                );
            }
        }
    }

    public static void approveRequest(int id, String decisionNote) throws SQLException {
        try (var conn = get(); var ps = conn.prepareStatement(
                "UPDATE verification_requests SET status='approved', decision_note=?, decided_at=CURRENT_TIMESTAMP WHERE id=?"
        )) {
            ps.setString(1, decisionNote);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public static void rejectRequest(int id, String decisionNote) throws SQLException {
        try (var conn = get(); var ps = conn.prepareStatement(
                "UPDATE verification_requests SET status='rejected', decision_note=?, decided_at=CURRENT_TIMESTAMP WHERE id=?"
        )) {
            ps.setString(1, decisionNote);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}