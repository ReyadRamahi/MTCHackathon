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
        try (var conn = get()) {
            try (var st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");

                st.execute("""
                CREATE TABLE IF NOT EXISTS posts (
                  id          INTEGER PRIMARY KEY AUTOINCREMENT,
                  title       TEXT,
                  body        TEXT NOT NULL,
                  author_uid  TEXT NOT NULL,
                  created_at  TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                  hidden      INTEGER NOT NULL DEFAULT 0
                )
            """);

                st.execute("""
                CREATE TABLE IF NOT EXISTS comments (
                  id          INTEGER PRIMARY KEY AUTOINCREMENT,
                  post_id     INTEGER NOT NULL,
                  body        TEXT NOT NULL,
                  author_uid  TEXT NOT NULL,
                  created_at  TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                  FOREIGN KEY(post_id) REFERENCES posts(id) ON DELETE CASCADE
                )
            """);

                st.execute("""
                CREATE TABLE IF NOT EXISTS votes (
                  post_id   INTEGER NOT NULL,
                  uid       TEXT NOT NULL,
                  value     INTEGER NOT NULL CHECK (value IN (-1,1)),
                  PRIMARY KEY (post_id, uid),
                  FOREIGN KEY(post_id) REFERENCES posts(id) ON DELETE CASCADE
                )
            """);

                st.execute("""
                CREATE TABLE IF NOT EXISTS verification_requests (
                  id            INTEGER PRIMARY KEY AUTOINCREMENT,
                  uid           TEXT NOT NULL,
                  email         TEXT,
                  note          TEXT,
                  file_path     TEXT NOT NULL,   -- full path on disk
                  file_name     TEXT NOT NULL,   -- original filename
                  status        TEXT NOT NULL DEFAULT 'pending', -- pending/approved/rejected
                  decision_note TEXT,
                  created_at    TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                  decided_at    TEXT
                )
            """);
            }
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
    public static int getPostScore(int postId) throws SQLException {
        try (var conn = get();
             var ps = conn.prepareStatement(
                     "SELECT COALESCE(SUM(value),0) AS score FROM post_votes WHERE post_id=?")) {
            ps.setInt(1, postId);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("score") : 0;
            }
        }
    }

    public static void hidePostForLowScore(int postId) throws SQLException {
        try (var conn = get();
             var ps = conn.prepareStatement(
                     "UPDATE posts SET is_hidden=TRUE, hidden_reason='low_score', hidden_at=CURRENT_TIMESTAMP " +
                             "WHERE id=? AND is_hidden=FALSE")) {
            ps.setInt(1, postId);
            ps.executeUpdate();
        }
    }

    // (Optional) hard-delete and remove any attached file
    public static void deletePost(int postId) throws SQLException {
        String filePath = null;
        try (var conn = get();
             var ps = conn.prepareStatement("SELECT file_path FROM posts WHERE id=?")) {
            ps.setInt(1, postId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) filePath = rs.getString("file_path");
            }
        }
        try (var conn = get();
             var ps = conn.prepareStatement("DELETE FROM posts WHERE id=?")) {
            ps.setInt(1, postId);
            ps.executeUpdate();
        }
        if (filePath != null && !filePath.isBlank()) {
            try { java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(filePath)); }
            catch (Exception e) { System.err.println("Failed to delete file: " + filePath); e.printStackTrace(); }
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
    // Db.java
    public static String getUidByRequestId(int id) throws SQLException {
        try (var conn = get();
             var ps = conn.prepareStatement("SELECT uid FROM verification_requests WHERE id=?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("uid") : null;
            }
        }
    }

    public static String getFilePathByRequestId(int id) throws SQLException {
        try (var conn = get();
             var ps = conn.prepareStatement("SELECT file_path FROM verification_requests WHERE id=?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("file_path") : null;
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
    public static void markPostHidden(int id, boolean hidden) throws SQLException {
        try (var conn = get();
             var ps = conn.prepareStatement("UPDATE posts SET hidden=? WHERE id=?")) {
            ps.setInt(1, hidden ? 1 : 0);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}