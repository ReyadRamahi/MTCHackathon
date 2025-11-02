package app;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Simple in-memory post model used by Main/Home views. */
public class Post {

    // ---- Core fields ----
    private final int id;
    private String title;
    private String body;
    private final String ownerUid;

    // Voting / moderation
    private int score = 0;
    private boolean hidden = false;

    // Timestamps
    private final Instant createdAt;

    // Comments (optional, kept in memory for now)
    private final List<Comment> comments = new ArrayList<>();

    private static final DateTimeFormatter HUMAN_TIME =
            DateTimeFormatter.ofPattern("MMM d, uuuu h:mm a")
                    .withZone(ZoneId.systemDefault());

    // ---- Constructors expected by Main.java ----

    /** Full constructor used by Main when creating a new post. */
    public Post(int id, String title, String body, String ownerUid) {
        this.id = id;
        this.title = title;
        this.body  = body == null ? "" : body;
        this.ownerUid = ownerUid;
        this.createdAt = Instant.now();
    }

    // ---- Getters / setters used by routes & templates ----
    public int getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body == null ? "" : body; }

    public String getOwnerUid() { return ownerUid; }

    public Instant getCreatedAt() { return createdAt; }
    /** e.g. "Nov 1, 2025 9:24 PM" */
    public String getCreatedAtDisplay() { return HUMAN_TIME.format(createdAt); }

    // Voting
    public int getScore() { return score; }
    /** Apply a net vote delta (+1, 0, -1, etc.). */
    public void applyVoteDelta(int delta) { this.score += delta; }

    // Moderation (optional: used if you auto-hide <= -5)
    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }

    // ---- Comments (lightweight; safe defaults so templates won’t break) ----
    public List<Comment> getCommentsNewestFirst() {
        List<Comment> copy = new ArrayList<>(comments);
        Collections.reverse(copy);
        return copy;
    }
    public void addComment(String authorUid, String text) {
        if (text == null || text.isBlank()) return;
        comments.add(new Comment(authorUid, text));
    }

    /** Minimal comment record so existing templates can call getters. */
    public static class Comment {
        private final String authorUid;
        private final String body;
        private final Instant createdAt = Instant.now();

        public Comment(String authorUid, String body) {
            this.authorUid = authorUid;
            this.body = body;
        }

        public String getBody() { return body; }
        public String getCreatedAtDisplay() {
            return HUMAN_TIME.format(createdAt);
        }

        // If your template calls these, keep harmless defaults:
        public String getAuthorName() { return "Anonymous"; }
        public Role getAuthorRole() { return Role.USER; } // requires app.Role enum present
    }
}