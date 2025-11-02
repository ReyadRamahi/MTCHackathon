package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** View-model wrapper for the home feed. */
public class HomeView {
    private final List<Post> posts;

    public HomeView(List<Post> posts) {
        this.posts = posts;
    }

    public boolean hasPosts() {
        return !posts.isEmpty();
    }

    /** Return newest first without mutating the original backing list. */
    public List<Post> getPostsNewestFirst() {
        var copy = new ArrayList<>(posts);
        Collections.reverse(copy);
        return copy;
    }
}