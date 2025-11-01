package app;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.nio.file.Path;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;

public class Main {

    private static final List<Post> POSTS = new java.util.concurrent.CopyOnWriteArrayList<>();
    private static final Map<String, Map<Integer, Integer>> VOTES_BY_USER = new java.util.concurrent.ConcurrentHashMap<>();

    public static void main(String[] args) {

        // --- Template engine setup ---
        Path templatesDir = Path.of("src/main/resources/templates").toAbsolutePath();
        TemplateEngine engine = TemplateEngine.create(new DirectoryCodeResolver(templatesDir), ContentType.Html);

        // --- Javalin app ---
        Javalin app = Javalin.create(cfg -> {
            cfg.fileRenderer(new io.javalin.rendering.template.JavalinJte(engine));
            cfg.staticFiles.add("/public");
        });

        // ===== Authorization Guard (v6) =====
        app.beforeMatched(ctx -> {
            User u = SessionStore.currentUser(ctx);
            Role caller = (u == null) ? Role.USER : u.getRole();
            var required = ctx.routeRoles();
            if (required == null || required.isEmpty()) return; // public route
            if (required.contains(caller)) return;
            ctx.status(403).result("Forbidden");
        });

        // ======= ROUTES =======

        // --- Home feed ---
        app.get("/", ctx -> {
            HomeView vm = new HomeView(POSTS);
            ctx.render("home.jte", Map.of("vm", vm, "me", SessionStore.currentUser(ctx)));
        });

        // --- Ask a question ---
        app.get("/ask", ctx -> ctx.render("ask.jte", Map.of("me", SessionStore.currentUser(ctx))));
        app.post("/ask", ctx -> {
            String title = param(ctx, "title");
            String body = param(ctx, "body");
            if (title == null || title.isBlank()) {
                ctx.status(400).result("Title required");
                return;
            }
            int id = POSTS.size();
            POSTS.add(new Post(id, title, body == null ? "" : body));
            ctx.redirect("/");
        });

        // --- View question ---
        app.get("/q/{id}", ctx -> {
            Post p = findPostOr404(ctx);
            ctx.render("question.jte", Map.of("post", p, "me", SessionStore.currentUser(ctx)));
        });

        // --- Comment (requires SCHOLAR or ADMIN) ---
        app.post("/q/{id}/comment", ctx -> {
            Post p = findPostOr404(ctx);
            String body = param(ctx, "body");
            if (body == null || body.isBlank()) {
                ctx.status(400).result("Comment required");
                return;
            }
            User me = SessionStore.currentUser(ctx);
            if (me.getRole() == Role.USER) {
                ctx.status(403).result("Only verified scholars or admins may comment");
                return;
            }
            p.addComment(body);
            ctx.redirect("/q/" + p.getId());
        }, Role.SCHOLAR, Role.ADMIN);

        // --- Upvote ---
        app.post("/q/{id}/upvote", ctx -> {
            Post p = findPostOr404(ctx);
            String uid = getUserId(ctx);
            int before = getUserVote(uid, p.getId());
            if (before != +1) {
                setUserVote(uid, p.getId(), +1);
                p.applyVoteDelta(+1 - before);
            }
            ctx.redirect("/");
        });

        // --- Downvote ---
        app.post("/q/{id}/downvote", ctx -> {
            Post p = findPostOr404(ctx);
            String uid = getUserId(ctx);
            int before = getUserVote(uid, p.getId());
            if (before != -1) {
                setUserVote(uid, p.getId(), -1);
                p.applyVoteDelta(-1 - before);
            }
            ctx.redirect("/");
        });

        // ===== ACCOUNT MANAGEMENT =====

        app.get("/signup", ctx -> ctx.render("signup.jte"));
        app.post("/signup", Main::handleSignup);

        app.get("/login", ctx -> ctx.render("login.jte"));
        app.post("/login", Main::handleLogin);

        app.post("/logout", ctx -> {
            SessionStore.logout(ctx);
            ctx.redirect("/");
        });

        // ===== VERIFICATION =====
        app.get("/verify", ctx -> {
            User me = SessionStore.currentUser(ctx);
            ctx.render("verify.jte", Map.of("me", me));
        });

        app.post("/verify/request", ctx -> {
            User me = SessionStore.currentUser(ctx);
            String note = param(ctx, "note");
            try {
                Db.createVerificationRequest(me.getId(), note);
                ctx.redirect("/verify");
            } catch (Exception e) {
                ctx.status(500).result("Error submitting request");
            }
        });

        // ===== ADMIN: Approve verification =====
        app.get("/admin/verify", ctx -> {
            requireRole(ctx, Role.ADMIN);
            try {
                var pending = Db.listPendingRequests();
                ctx.render("admin_verify.jte", Map.of("pending", pending, "me", SessionStore.currentUser(ctx)));
            } catch (Exception e) {
                ctx.status(500).result("Error loading verification list");
            }
        });

        app.post("/admin/verify/{id}/approve", ctx -> {
            requireRole(ctx, Role.ADMIN);
            int id = Integer.parseInt(ctx.pathParam("id"));
            try {
                Db.approveVerification(id);
                ctx.redirect("/admin/verify");
            } catch (Exception e) {
                ctx.status(500).result("Error approving");
            }
        });

        app.post("/admin/verify/{id}/reject", ctx -> {
            requireRole(ctx, Role.ADMIN);
            int id = Integer.parseInt(ctx.pathParam("id"));
            try {
                Db.rejectVerification(id);
                ctx.redirect("/admin/verify");
            } catch (Exception e) {
                ctx.status(500).result("Error rejecting");
            }
        });

        app.start(7001);
    }

    // ===== Helper methods =====

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

    private static String getUserId(Context ctx) {
        String uid = ctx.cookie("uid");
        if (uid == null) {
            uid = UUID.randomUUID().toString();
            ctx.cookie("uid", uid);
        }
        return uid;
    }

    private static int getUserVote(String uid, int postId) {
        return VOTES_BY_USER.getOrDefault(uid, Map.of()).getOrDefault(postId, 0);
    }

    private static void setUserVote(String uid, int postId, int value) {
        VOTES_BY_USER.computeIfAbsent(uid, k -> new java.util.concurrent.ConcurrentHashMap<>()).put(postId, value);
    }

    private static void handleSignup(Context ctx) {
        String email = param(ctx, "email");
        String display = param(ctx, "display");
        String password = param(ctx, "password");
        if (email == null || password == null) {
            ctx.status(400).result("Missing email or password");
            return;
        }
        try {
            if (!Db.emailAvailable(email)) {
                ctx.status(400).result("Email already in use");
                return;
            }
            User u = Db.createUser(email, display, password);
            SessionStore.login(ctx, u);
            ctx.redirect("/");
        } catch (Exception e) {
            ctx.status(500).result("Signup failed");
        }
    }

    private static void handleLogin(Context ctx) {
        String email = param(ctx, "email");
        String password = param(ctx, "password");
        try {
            if (!Db.checkPassword(email, password)) {
                ctx.status(401).result("Invalid credentials");
                return;
            }
            User u = Db.findUserByEmail(email).orElseThrow();
            SessionStore.login(ctx, u);
            ctx.redirect("/");
        } catch (Exception e) {
            ctx.status(500).result("Login failed");
        }
    }

    private static void requireRole(Context ctx, Role minRole) {
        User u = SessionStore.currentUser(ctx);
        if (u == null || u.getRole().ordinal() < minRole.ordinal()) {
            ctx.status(403).result("Forbidden");
            throw new IllegalStateException("Forbidden");
        }
    }
}