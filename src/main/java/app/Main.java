package app;

import gg.jte.TemplateEngine;
import gg.jte.ContentType;
import gg.jte.resolve.DirectoryCodeResolver;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.rendering.template.JavalinJte;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    // In-memory "posts" store (id = index)
    private static final List<Post> POSTS = new CopyOnWriteArrayList<>();

    // For voting: uid(cookie) -> (postId -> vote), where vote ∈ {-1,0,+1}
    private static final ConcurrentMap<String, ConcurrentMap<Integer, Integer>> USER_VOTES = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // ---- DB + Templating setup ----
        Db.init("app.db"); // creates/opens SQLite file

        var templatesDir = Path.of("src/main/resources/templates").toAbsolutePath();
        TemplateEngine engine = TemplateEngine.create(new DirectoryCodeResolver(templatesDir), ContentType.Html);

        Javalin app = Javalin.create(cfg -> {
            cfg.staticFiles.add("/public");         // serve /styles.css etc.
            cfg.fileRenderer(new JavalinJte(engine));
        });

        // Attach logged-in user (if any) to every request
        app.before(ctx -> {
            var sid = ctx.cookie("sid");
            var uid = SessionStore.getUserId(sid);
            if (uid != null) {
                try {
                    var me = Db.findUserById(uid);
                    if (me != null) ctx.attribute("user", me);
                } catch (Exception ignored) {}
            }
        });

        // ---- Feed ----
        app.get("/", ctx -> ctx.render("home.jte", Map.of("vm", new HomeView(POSTS))));

        // ---- Ask (anonymous) ----
        app.get("/ask", ctx -> ctx.render("ask.jte"));
        app.post("/ask", ctx -> {
            String title = ctx.formParam("title");
            String body  = ctx.formParam("body");
            if (title != null && !title.trim().isEmpty()) {
                POSTS.add(new Post(POSTS.size(), title.trim(), body == null ? "" : body.trim()));
            }
            ctx.redirect("/");
        });

        // ---- View single question ----
        app.get("/q/{id}", ctx -> {
            int id = parseId(ctx);
            if (!validId(id)) { notFound(ctx); return; }
            ctx.render("question.jte", Map.of("post", POSTS.get(id)));
        });

        // ---- Voting (single vote per user) ----
        app.post("/upvote/{id}",   ctx -> { int id = parseId(ctx); if (!validId(id)) { notFound(ctx); return; } applyVote(ctx, id, +1); });
        app.post("/downvote/{id}", ctx -> { int id = parseId(ctx); if (!validId(id)) { notFound(ctx); return; } applyVote(ctx, id, -1); });
        app.post("/unvote/{id}",   ctx -> { int id = parseId(ctx); if (!validId(id)) { notFound(ctx); return; } applyVote(ctx, id,  0); });

        // ---- Auth pages (quick forms for now) ----
        final String SCHOLAR_INVITE = "UMMAH2025"; // change this

        app.get("/signup", ctx -> ctx.result("""
            <form method='post' action='/signup' style='max-width:360px'>
              <input name='email' placeholder='email' style='width:100%'><br>
              <input name='name'  placeholder='display name' style='width:100%'><br>
              <input type='password' name='password' placeholder='password' style='width:100%'><br>
              <input name='invite' placeholder='scholar invite (optional)' style='width:100%'><br>
              <button type='submit'>Create account</button>
            </form>
        """));

        app.get("/login", ctx -> ctx.result("""
            <form method='post' action='/login' style='max-width:360px'>
              <input name='email' placeholder='email' style='width:100%'><br>
              <input type='password' name='password' placeholder='password' style='width:100%'><br>
              <button type='submit'>Log in</button>
            </form>
        """));

        app.post("/signup", ctx -> {
            var email  = param(ctx, "email");
            var name   = param(ctx, "name");
            var pass   = param(ctx, "password");
            var invite = param(ctx, "invite");
            if (email == null || name == null || pass == null) { ctx.status(400).result("Missing fields"); return; }
            var role = (invite != null && invite.equals(SCHOLAR_INVITE)) ? Role.SCHOLAR : Role.USER;
            try {
                if (Db.findUserByEmail(email) != null) { ctx.status(409).result("Email already registered"); return; }
                int uid = Db.createUser(email, name, pass, role);
                var sid = SessionStore.create(uid);
                ctx.cookie("sid", sid, 60*60*24*7);
                ctx.redirect("/");
            } catch (Exception e) { ctx.status(500).result("Signup error"); }
        });

        app.post("/login", ctx -> {
            var email = param(ctx, "email");
            var pass  = param(ctx, "password");
            if (email == null || pass == null) { ctx.status(400).result("Missing"); return; }
            try {
                if (!Db.checkPassword(email, pass)) { ctx.status(401).result("Invalid credentials"); return; }
                var me  = Db.findUserByEmail(email);
                var sid = SessionStore.create(me.id);
                ctx.cookie("sid", sid, 60*60*24*7);
                ctx.redirect("/");
            } catch (Exception e) { ctx.status(500).result("Login error"); }
        });

        app.post("/logout", ctx -> {
            var sid = ctx.cookie("sid");
            SessionStore.remove(sid);
            ctx.removeCookie("sid");
            ctx.redirect("/");
        });

        // ---- Scholar-only: answer a question ----
        app.post("/q/{id}/answer", ctx -> {
            int id = parseId(ctx);
            if (!validId(id)) { notFound(ctx); return; }

            var me = (User) ctx.attribute("user");
            if (me == null || !me.isScholar()) { ctx.status(403).result("Scholars only"); return; }

            var body = param(ctx, "body");
            if (body == null || body.isBlank()) { ctx.status(400).result("Empty answer"); return; }

            try {
                Db.insertAnswer(id, me.id, body.trim());
                ctx.redirect("/q/" + id);
            } catch (Exception e) { ctx.status(500).result("Could not save answer"); }
        });

        app.start(7001);
    }

    // ---------------- helpers ----------------

    private static void applyVote(Context ctx, int postId, int newVoteRaw) {
        int newVote = Integer.compare(newVoteRaw, 0); // normalize to -1,0,+1
        String uid = getOrSetUserIdCookie(ctx);
        USER_VOTES.putIfAbsent(uid, new ConcurrentHashMap<>());
        var votes = USER_VOTES.get(uid);

        int oldVote = votes.getOrDefault(postId, 0);
        int delta = newVote - oldVote;

        if (delta != 0) {
            POSTS.get(postId).applyVoteDelta(delta);
            votes.put(postId, newVote);
        }
        var back = ctx.header("Referer");
        ctx.redirect(back != null ? back : "/");
    }

    private static String getOrSetUserIdCookie(Context ctx) {
        String uid = ctx.cookie("uid");
        if (uid == null || uid.isBlank()) {
            uid = UUID.randomUUID().toString();
            ctx.cookie("uid", uid, 60 * 60 * 24 * 30); // 30 days
        }
        return uid;
    }

    private static int parseId(Context ctx) {
        try { return Integer.parseInt(ctx.pathParam("id")); }
        catch (Exception e) { return -1; }
    }

    private static boolean validId(int id) {
        return id >= 0 && id < POSTS.size();
    }

    private static void notFound(Context ctx) {
        ctx.status(404).result("Post not found");
    }

    private static String param(Context ctx, String name) {
        var v = ctx.formParam(name);
        return (v == null || v.isBlank()) ? null : v.trim();
    }
}