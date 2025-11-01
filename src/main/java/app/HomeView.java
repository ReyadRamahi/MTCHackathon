package app;

import java.util.List;
import java.util.Collections;

public class HomeView {
    private final List<Post> posts;
    public HomeView(List<Post> posts) { this.posts = posts; }
    public List<Post> getPostsNewestFirst() {
        var copy = new java.util.ArrayList<>(posts);
        Collections.reverse(copy); // newest first
        return copy;
    }
    public boolean hasPosts() { return !posts.isEmpty(); }
}