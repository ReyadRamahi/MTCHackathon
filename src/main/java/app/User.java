package app;

public class User {
    public final int id;
    public final String email;
    public final String displayName;
    public final Role role;

    public User(int id, String email, String displayName, Role role) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
    }

    public boolean isScholar() { return role == Role.SCHOLAR || role == Role.ADMIN; }
}