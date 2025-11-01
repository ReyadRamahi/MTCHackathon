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
    private final String ownerUid;

    public static class Comment {
        private final String body;
        private final Instant createdAt = Instant.now();
        private final String authorName;
        private final Role authorRole;

        public Comment(String body, String authorName, Role authorRole) {
            this.body = body;
            this.authorName = authorName;
            this.authorRole = authorRole;
        }

        public String getBody() { return body; }
        public String getAuthorName() { return authorName; }
        public Role getAuthorRole() { return authorRole; }

        public String getCreatedAtDisplay() {
            var fmt = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
                    .withZone(ZoneId.systemDefault());
            return fmt.format(createdAt);
        }
    }

    public Post(int id, String title, String body, String ownerUid) {
        this.id = id;
        this.title = title == null ? "" : title;
        this.body = body == null ? "" : body;
        this.ownerUid = ownerUid;
        this.createdAt = Instant.now();
    }

    // getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getOwnerUid() { return ownerUid; }
    public int getScore() { return score.get(); }
    public String getCreatedAtDisplay() {
        var fmt = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
                .withZone(ZoneId.systemDefault());
        return fmt.format(createdAt);
    }

    // voting
    public void applyVoteDelta(int delta) { if (delta != 0) score.addAndGet(delta); }

    // editing helpers
    public void setTitle(String title) { if (title != null && !title.isBlank()) this.title = title.trim(); }
    public void setBody(String body)   { this.body = (body == null) ? "" : body; }

    // comments - now with author info
    public void addComment(String body, String authorName, Role authorRole) {
        if (body != null && !body.isBlank()) {
            comments.add(new Comment(body.trim(), authorName, authorRole));
        }
    }

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
    public void removeCommentAt(int idx) {
        if (idx >= 0 && idx < comments.size()) comments.remove(idx);
    }
}