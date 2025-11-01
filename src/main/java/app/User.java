package app;

import java.time.Instant;
import java.util.Objects;

public class User {
    private final String id;          // equals your uid cookie
    private String displayName;       // shown in UI
    private Role role;                // ADMIN | SCHOLAR | USER
    private final Instant createdAt;

    public User(String id, String displayName, Role role) {
        this.id = id;
        this.displayName = (displayName == null || displayName.isBlank()) ? "anon" : displayName.trim();
        this.role = role == null ? Role.USER : role;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Role getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }

    public void setDisplayName(String name) {
        if (name != null && !name.isBlank()) this.displayName = name.trim();
    }
    public void setRole(Role role) {
        if (role != null) this.role = role;
    }

    public boolean isAdmin()   { return role == Role.ADMIN; }
    public boolean isScholar() { return role == Role.SCHOLAR; }

    public String getPublicName() {
        return switch (role) {
            case ADMIN   -> "Admin";
            case USER    -> "Anonymous";
            case SCHOLAR -> (displayName == null || displayName.isBlank()) ? "Scholar" : displayName;
        };
    }

    @Override public boolean equals(Object o) {
        return (o instanceof User u) && Objects.equals(id, u.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}