package app;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class Post {
    public final int id;
    public final String title;
    public final String body;
    public final Instant createdAt;

    // store a Reddit-like "score" (upvotes - downvotes)
    private final AtomicInteger score = new AtomicInteger(0);

    public Post(int id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body == null ? "" : body;
        this.createdAt = Instant.now();
    }

    // Voting API used by the server
    public void applyVoteDelta(int delta) {
        if (delta != 0) score.addAndGet(delta);
    }

    // Compatibility with your template: either call getScore() or getUpvotes()
    public int getScore()    { return score.get(); }
    public int getUpvotes()  { return score.get(); } // keep front-end happy

    public String getCreatedAtDisplay() {
        var fmt = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
                .withZone(ZoneId.systemDefault());
        return fmt.format(createdAt);
    }

    public String getSnippet() {
        if (body == null || body.isEmpty()) return "";
        int end = Math.min(140, body.length());
        return body.substring(0, end) + (end < body.length() ? "…" : "");
    }
}