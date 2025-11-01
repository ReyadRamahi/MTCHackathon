package app;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Post {
    private final int id;
    private String title;
    private String body;
    private final Instant createdAt;
    private final AtomicInteger score = new AtomicInteger(0);
    private final List<Comment> comments = new ArrayList<>();

    // NEW: who owns this post (browser/session uid)
    private final String ownerUid;

    public static class Comment {
        private final String body;
        private final Instant createdAt = Instant.now();

        public Comment(String body) { this.body = body; }

        public String getBody() { return body; }

        public String getCreatedAtDisplay() {
            var fmt = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
                    .withZone(ZoneId.systemDefault());
            return fmt.format(createdAt);
        }
    }

    // CHANGE: take ownerUid in the constructor
    public Post(int id, String title, String body, String ownerUid) {
        this.id = id;
        this.title = title == null ? "" : title;
        this.body = body == null ? "" : body;
        this.ownerUid = ownerUid;                 // << store it
        this.createdAt = Instant.now();
    }

    // getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getOwnerUid() { return ownerUid; }   // << NEW
    public int getScore() { return score.get(); }
    public String getCreatedAtDisplay() {
        var fmt = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
                .withZone(ZoneId.systemDefault());
        return fmt.format(createdAt);
    }

    // voting
    public void applyVoteDelta(int delta) { if (delta != 0) score.addAndGet(delta); }

    // editing helpers (you tried to call these)
    public void setTitle(String title) { if (title != null && !title.isBlank()) this.title = title.trim(); }
    public void setBody(String body)   { this.body = (body == null) ? "" : body; }

    // comments
    public void addComment(String body) { if (body != null && !body.isBlank()) comments.add(new Comment(body.trim())); }
    public List<Comment> getComments() { return Collections.unmodifiableList(comments); }
    public List<Comment> getCommentsNewestFirst() {
        var copy = new ArrayList<>(comments);
        Collections.reverse(copy);
        return copy;
    }

    public String getSnippet() {
        if (body == null || body.isEmpty()) return "";
        int end = Math.min(140, body.length());
        return body.substring(0, end) + (end < body.length() ? "…" : "");
    }
}