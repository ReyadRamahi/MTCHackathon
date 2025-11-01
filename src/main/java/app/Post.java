package app;

import java.time.Instant;

public class Post {
    public final int id;
    public final String title;
    public final String body;
    public final Instant createdAt;

    public Post(int id, String title, String body, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.body = body == null ? "" : body;
        this.createdAt = createdAt;
    }

    public String getSnippet() {
        var s = body.strip();
        return s.isEmpty() ? "" : (s.length() > 140 ? s.substring(0, 140) + "..." : s);
    }
    public String getCreatedAtDisplay() {
        var formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
                .withZone(java.time.ZoneId.systemDefault());
        return formatter.format(createdAt);
    }
}