package app;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    // In-memory “database”
    private static final List<Post> POSTS = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        // JTE template engine (reads straight from /templates during dev)
        var templatesDir = Path.of("src/main/resources/templates").toAbsolutePath();
        var engine = TemplateEngine.create(new DirectoryCodeResolver(templatesDir), ContentType.Html);

        var app = Javalin.create(cfg -> {
            cfg.fileRenderer(new JavalinJte(engine));
            cfg.staticFiles.add("/public"); // serves /styles.css
        });

        // Home (reddit-style list)
        app.get("/", ctx ->
                ctx.render("home.jte", Map.of("vm", new HomeView(POSTS)))
        );

        // Ask form (GET)
        app.get("/ask", ctx -> ctx.render("ask.jte"));

        // Ask form (POST) -> create a new post
        app.post("/ask", ctx -> {
            var title = ctx.formParam("title");
            var body  = ctx.formParam("body");
            if (title != null && !title.isBlank()) {
                int id = POSTS.size(); // simple incremental id for now
                POSTS.add(new Post(id, title.trim(), body == null ? "" : body.trim(), Instant.now()));
            }
            ctx.redirect("/");
        });
        app.post("/upvote/{id}", ctx -> { ctx.redirect("/"); });
        app.post("/downvote/{id}", ctx -> { ctx.redirect("/"); });

        // Detail page
        app.get("/q/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (id < 0 || id >= POSTS.size()) {
                ctx.status(404).result("Question not found");
                return;
            }
            var post = POSTS.get(id);
            ctx.render("question.jte", Map.of("post", post));
        });

        app.start(7001);
    }
}