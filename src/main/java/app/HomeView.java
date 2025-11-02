package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeView {
    private final List<Post> posts;

    public HomeView(List<Post> posts) {
        this.posts = posts;
    }

    public boolean hasPosts() {
        return !posts.isEmpty();
    }

    // newest first for the feed
    public List<Post> getPostsNewestFirst() {
        var copy = new ArrayList<>(posts);
        Collections.reverse(copy);
        return copy;
    }
}