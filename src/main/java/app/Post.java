package app;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Post {
    private final int id;
    private final String title;
    private final String body;
    private final Instant createdAt = Instant.now();
    private final AtomicInteger score = new AtomicInteger(0);

    // simple in-memory comments
    private final List<Comment> comments = new CopyOnWriteArrayList<>();

    public Post(int id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body  = body == null ? "" : body;
    }

    // voting
    public int  getScore() { return score.get(); }
    public void applyVoteDelta(int delta) { if (delta != 0) score.addAndGet(delta); }

    // comments
    public void addComment(String text) { comments.add(new Comment(text)); }
    public List<Comment> getComments() { return comments; }
    public List<Comment> getCommentsNewestFirst() {
        var copy = new ArrayList<>(comments);
        Collections.reverse(copy);
        return copy;
    }

    // getters for templates
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getSnippet() {
        if (body.isEmpty()) return "";
        int end = Math.min(240, body.length());
        return body.substring(0, end) + (end < body.length() ? "…" : "");
    }
    public String getCreatedAtDisplay() {
        var fmt = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a").withZone(ZoneId.systemDefault());
        return fmt.format(createdAt);
    }

    // nested comment type
    public static class Comment {
        private final String body;
        private final Instant createdAt = Instant.now();
        public Comment(String body) { this.body = body == null ? "" : body.trim(); }
        public String getBody() { return body; }
        public String getCreatedAtDisplay() {
            var fmt = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a").withZone(ZoneId.systemDefault());
            return fmt.format(createdAt);
        }
    }
}