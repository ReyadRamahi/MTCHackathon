package app;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Db {

    private static Connection conn() throws SQLException {
        // your existing connection method
        // e.g., return DriverManager.getConnection("jdbc:sqlite:app.db");
        throw new UnsupportedOperationException("wire to your DB");
    }

    // ----- USERS -----

    public static Optional<User> findUserByEmail(String email) throws SQLException {
        String sql = "SELECT uid,email,display_name,role FROM users WHERE email = ?";
        try (var c = conn(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUserNoId(rs));
                }
                return Optional.empty();
            }
        }
    }

    public static Optional<User> findUserByUid(String uid) throws SQLException {
        String sql = "SELECT uid,email,display_name,role FROM users WHERE uid = ?";
        try (var c = conn(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, uid);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapUserNoId(rs));
                return Optional.empty();
            }
        }
    }

    public static boolean emailAvailable(String email) throws SQLException {
        return findUserByEmail(email).isEmpty();
    }

    public static User createUser(String email, String displayName, String rawPassword) throws SQLException {
        String uid = UUID.randomUUID().toString();
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        String sql = "INSERT INTO users(uid,email,display_name,password_hash,role) VALUES (?,?,?,?,?)";
        try (var c = conn(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, uid);
            ps.setString(2, email);
            ps.setString(3, displayName == null || displayName.isBlank() ? "anon" : displayName.trim());
            ps.setString(4, hash);
            ps.setString(5, Role.USER.name());
            ps.executeUpdate();
        }
        return findUserByUid(uid).orElseThrow();
    }

    public static boolean checkPassword(String email, String rawPassword) throws SQLException {
        String sql = "SELECT password_hash FROM users WHERE email = ?";
        try (var c = conn(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                return BCrypt.checkpw(rawPassword, rs.getString("password_hash"));
            }
        }
    }

    public static void setRole(String uid, Role newRole) throws SQLException {
        String sql = "UPDATE users SET role = ? WHERE uid = ?";
        try (var c = conn(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, newRole.name());
            ps.setString(2, uid);
            ps.executeUpdate();
        }
    }

    private static User mapUserNoId(ResultSet rs) throws SQLException {
        String uid = rs.getString("uid");
        String disp = rs.getString("display_name");
        Role role = Role.valueOf(rs.getString("role").toUpperCase());
        return new User(uid, disp, role);
    }

    // ----- VERIFICATION -----

    public static void createVerificationRequest(String uid, String note) throws SQLException {
        String sql = "INSERT INTO verification_requests(uid,note,status) VALUES (?,?, 'PENDING')";
        try (var c = conn(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, uid);
            ps.setString(2, note);
            ps.executeUpdate();
        }
    }

    public static List<VerificationRequest> listPendingRequests() throws SQLException {
        String sql = "SELECT id, uid, note, status, created_at FROM verification_requests WHERE status = 'PENDING' ORDER BY created_at ASC";
        try (var c = conn(); var ps = c.prepareStatement(sql); var rs = ps.executeQuery()) {
            List<VerificationRequest> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new VerificationRequest(
                        rs.getInt("id"),
                        rs.getString("uid"),
                        rs.getString("note"),
                        rs.getString("status"),
                        rs.getString("created_at")
                ));
            }
            return out;
        }
    }

    public static void approveVerification(int reqId) throws SQLException {
        String getUid = "SELECT uid FROM verification_requests WHERE id = ?";
        String approve = "UPDATE verification_requests SET status='APPROVED', decided_at=datetime('now') WHERE id = ?";
        try (var c = conn()) {
            c.setAutoCommit(false);
            String uid;
            try (var ps = c.prepareStatement(getUid)) {
                ps.setInt(1, reqId);
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) { c.rollback(); throw new SQLException("Request not found"); }
                    uid = rs.getString("uid");
                }
            }
            try (var ps = c.prepareStatement(approve)) {
                ps.setInt(1, reqId);
                ps.executeUpdate();
            }
            try (var ps = c.prepareStatement("UPDATE users SET role='SCHOLAR' WHERE uid=?")) {
                ps.setString(1, uid);
                ps.executeUpdate();
            }
            c.commit();
        }
    }

    public static void rejectVerification(int reqId) throws SQLException {
        String sql = "UPDATE verification_requests SET status='REJECTED', decided_at=datetime('now') WHERE id = ?";
        try (var c = conn(); var ps = c.prepareStatement(sql)) {
            ps.setInt(1, reqId);
            ps.executeUpdate();
        }
    }

    // simple DTO
    public record VerificationRequest(int id, String uid, String note, String status, String createdAt) {}
}