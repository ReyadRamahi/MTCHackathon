package app;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.rendering.template.JavalinJte;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    // -------- In-memory “DB-ish” storage --------
    private static final List<Post> POSTS = new CopyOnWriteArrayList<>();

    // user cookie uid -> (postId -> -1/0/+1) to rate-limit per user
    private static final Map<String, Map<Integer, Integer>> VOTES_BY_USER = new ConcurrentHashMap<>();

    // postId -> ownerUid (so only the author can edit)
    private static final Map<Integer, String> POST_OWNER = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        // ----- JTE engine -----
        Path templatesDir = Path.of("src/main/resources/templates").toAbsolutePath();
        TemplateEngine engine = TemplateEngine.create(new DirectoryCodeResolver(templatesDir), ContentType.Html);

        // ----- Javalin app -----
        Javalin app = Javalin.create((JavalinConfig cfg) -> {
            cfg.fileRenderer(new JavalinJte(engine)); // render *.jte
            cfg.staticFiles.add("/public");           // /styles.css etc.
        });

        // ----- Routes -----

        // Home (feed)
        app.get("/", ctx -> {
            HomeView vm = new HomeView(POSTS);
            ctx.render("home.jte", Map.of(
                    "vm", vm,
                    "me", SessionStore.currentUser(ctx)   // for header chip
            ));
        });

        // Ask (GET) – create new OR reuse for edit (your ask.jte already supports 'mode' + 'post')
        app.get("/ask", ctx -> {
            ctx.render("ask.jte", Map.of(
                    "mode", "new",
                    "me", SessionStore.currentUser(ctx)
            ));
        });

        // Ask (POST) – create post, record ownership by cookie uid
        app.post("/ask", ctx -> {
            String title = param(ctx, "title");
            String body  = param(ctx, "body");
            if (title == null || title.isBlank()) {
                ctx.status(400).result("Title required");
                return;
            }
            int id = POSTS.size();
            String ownerUid = getOrCreateUid(ctx);
            POSTS.add(new Post(id, title, body, ownerUid));
            POST_OWNER.put(id, ownerUid);

            ctx.redirect("/");
        });

        // View single post
        app.get("/q/{id}", ctx -> {
            Post p = findPostOr404(ctx);
            ctx.render("question.jte", Map.of(
                    "post", p,
                    "me", SessionStore.currentUser(ctx)
            ));
        });

        // Add comment (anyone; later you can restrict to Scholars/Admin)
        app.post("/q/{id}/comment", ctx -> {
            Post p = findPostOr404(ctx);
            String body = param(ctx, "body");
            if (body == null || body.isBlank()) {
                ctx.status(400).result("Comment required");
                return;
            }
            p.addComment(body);
            ctx.redirect("/q/" + p.getId());
        });

        // Edit (GET) – only owner can view edit form (ask.jte in "edit" mode)
        app.get("/q/{id}/edit", ctx -> {
            Post p = findPostOr404(ctx);
            String uid = getOrCreateUid(ctx);
            if (!uid.equals(POST_OWNER.getOrDefault(p.getId(), ""))) {
                ctx.status(403).result("Forbidden");
                return;
            }
            ctx.render("ask.jte", Map.of(
                    "mode", "edit",
                    "post", p,
                    "me", SessionStore.currentUser(ctx)
            ));
        });

        // Edit (POST) – only owner can save
        app.post("/q/{id}/edit", ctx -> {
            Post p = findPostOr404(ctx);
            String uid = getOrCreateUid(ctx);
            if (!uid.equals(POST_OWNER.getOrDefault(p.getId(), ""))) {
                ctx.status(403).result("Forbidden");
                return;
            }
            String title = param(ctx, "title");
            String body  = param(ctx, "body");
            if (title == null || title.isBlank()) {
                ctx.status(400).result("Title required");
                return;
            }
            p.setTitle(title);
            p.setBody(body == null ? "" : body);
            ctx.redirect("/q/" + p.getId());
        });

        // Upvote (stay on feed)
        app.post("/q/{id}/upvote", ctx -> {
            Post p = findPostOr404(ctx);
            String uid = getOrCreateUid(ctx);
            int before = getUserVote(uid, p.getId());   // -1, 0, +1
            if (before != +1) {
                setUserVote(uid, p.getId(), +1);
                p.applyVoteDelta(+1 - before);          // net delta
            }
            ctx.redirect("/");
        });

        // Downvote (stay on feed)
        app.post("/q/{id}/downvote", ctx -> {
            Post p = findPostOr404(ctx);
            String uid = getOrCreateUid(ctx);
            int before = getUserVote(uid, p.getId());   // -1, 0, +1
            if (before != -1) {
                setUserVote(uid, p.getId(), -1);
                p.applyVoteDelta(-1 - before);          // net delta
            }
            ctx.redirect("/");
        });

        // ---------- Auth: signup / login / logout ----------

        app.get("/signup", ctx -> ctx.render("signup.jte", Map.of(
                "me", SessionStore.currentUser(ctx))));

        app.post("/signup", ctx -> {
            String email = param(ctx, "email");
            String name  = param(ctx, "display_name");
            String pass  = param(ctx, "password");
            if (email == null || pass == null) { ctx.status(400).result("Missing credentials"); return; }

            try {
                if (!Db.emailAvailable(email)) { ctx.status(409).result("Email already in use"); return; }
                Db.insertUser(email, name, pass, Role.USER);
                var dbUser = Db.findUserByEmail(email);
                SessionStore.login(ctx, dbUser);
                ctx.redirect("/");
            } catch (Exception e) {
                ctx.status(500).result("Signup failed");
            }
        });


        app.get("/login", ctx -> ctx.render("login.jte", Map.of(
                "me", SessionStore.currentUser(ctx))));

        app.post("/login", ctx -> {
            String email = param(ctx, "email");
            String pass  = param(ctx, "password");
            if (email == null || pass == null) { ctx.status(400).result("Missing credentials"); return; }

            try {
                if (!Db.checkPassword(email, pass)) { ctx.status(401).result("Bad credentials"); return; }
                var dbUser = Db.findUserByEmail(email);
                SessionStore.login(ctx, dbUser);
                ctx.redirect("/");
            } catch (Exception e) {
                ctx.status(500).result("Login failed");
            }
        });



        app.post("/logout", ctx -> {
            SessionStore.logout(ctx);     // keeps uid cookie for ownership/votes
            ctx.redirect("/");
        });

        // Optional: admin promotes a known session uid to SCHOLAR (temporary demo)
        app.post("/admin/verify/{uid}", ctx -> {
            String targetUid = ctx.pathParam("uid");
            SessionStore.promoteToScholar(targetUid);
            ctx.result("OK");
        });

        app.start(7001);
    }

    // ---------------- Helpers ----------------

    private static String param(Context ctx, String name) {
        String v = ctx.formParam(name);
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private static Post findPostOr404(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        if (id < 0 || id >= POSTS.size()) {
            ctx.status(404).result("Not found");
            throw new IllegalStateException("Post not found");
        }
        return POSTS.get(id);
    }

    /** Stable, anonymous per-browser id used for ownership and rate-limiting. */
    private static String getOrCreateUid(Context ctx) {
        String uid = ctx.cookie("uid");
        if (uid == null || uid.isBlank()) {
            uid = UUID.randomUUID().toString();
            // simple cookie set (Javalin v6)
            ctx.cookie("uid", uid);
        }
        return uid;
    }

    private static int getUserVote(String uid, int postId) {
        return VOTES_BY_USER
                .getOrDefault(uid, Map.of())
                .getOrDefault(postId, 0);
    }

    private static void setUserVote(String uid, int postId, int value) {
        VOTES_BY_USER
                .computeIfAbsent(uid, k -> new ConcurrentHashMap<>())
                .put(postId, value);
    }
}