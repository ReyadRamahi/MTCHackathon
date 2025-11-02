package app;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.javalin.rendering.template.TemplateUtil.model;

public class Main {

    // -------- In-memory “DB-ish” storage --------
    private static final List<Post> POSTS = new CopyOnWriteArrayList<>();

    // user cookie uid -> (postId -> -1/0/+1) to rate-limit per user
    private static final Map<String, Map<Integer, Integer>> VOTES_BY_USER = new ConcurrentHashMap<>();

    // postId -> ownerUid (so only the author can edit)
    private static final Map<Integer, String> POST_OWNER = new ConcurrentHashMap<>();

    // uploads directory
    private static final Path UPLOAD_DIR = Path.of("uploads").toAbsolutePath();

    public static void main(String[] args) throws Exception {

        // ----- Ensure uploads dir exists -----
        Files.createDirectories(UPLOAD_DIR);

        // ----- JTE engine -----
        Path templatesDir = Path.of("src/main/resources/templates").toAbsolutePath();
        TemplateEngine engine = TemplateEngine.create(new DirectoryCodeResolver(templatesDir), ContentType.Html);

        // ----- Javalin app -----
        Javalin app = Javalin.create((JavalinConfig cfg) -> {
            cfg.fileRenderer(new JavalinJte(engine)); // render *.jte

            // /public (styles.css, etc.)
            cfg.staticFiles.add("/public");

            // serve local "uploads" directory at /uploads
            cfg.staticFiles.add(s -> {
                s.hostedPath = "/uploads";
                s.directory  = UPLOAD_DIR.toAbsolutePath().toString();
                s.location   = io.javalin.http.staticfiles.Location.EXTERNAL;
                s.precompress = false;
                s.headers.put("Cache-Control", "public, max-age=3600");
            });
        });

        // DB boot
        try { Db.init(); } catch (Exception e) { e.printStackTrace(); }

        // ----- Routes -----

        // Home (feed)
        app.get("/", ctx -> {
            HomeView vm = new HomeView(POSTS);
            ctx.render("home.jte", model(
                    "vm", vm,
                    "me", SessionStore.currentUser(ctx)
            ));
        });

        // Ask (GET) – create new
        app.get("/ask", ctx -> {
            ctx.render("ask.jte", model(
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
            ctx.render("question.jte", model(
                    "post", p,
                    "me", SessionStore.currentUser(ctx)
            ));
        });

        // Add comment
        app.post("/q/{id}/comment", ctx -> {
            Post p = findPostOr404(ctx);
            String body = param(ctx, "body");
            if (body == null || body.isBlank()) {
                ctx.status(400).result("Comment required");
                return;
            }

            User me = SessionStore.currentUser(ctx);
            String authorName = me.getPublicName();
            Role authorRole = me.getRole();

            p.addComment(body, authorName, authorRole);
            ctx.redirect("/q/" + p.getId());
        });

        // Edit (GET)
        app.get("/q/{id}/edit", ctx -> {
            Post p = findPostOr404(ctx);
            String uid = getOrCreateUid(ctx);
            if (!uid.equals(POST_OWNER.getOrDefault(p.getId(), ""))) {
                ctx.status(403).result("Forbidden");
                return;
            }
            ctx.render("ask.jte", model(
                    "mode", "edit",
                    "post", p,
                    "me", SessionStore.currentUser(ctx)
            ));
        });

        // Edit (POST)
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

        // Upvote
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

        // Downvote
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

        // --- Verification pages ---

        // Public: request verification form
        app.get("/verify", ctx ->
                ctx.render("verify.jte", model(
                        "me", SessionStore.currentUser(ctx)
                ))
        );

        // Public: submit verification request (stores file & row)
        app.post("/verify", ctx -> {
            var me = SessionStore.currentUser(ctx);

            var upload = ctx.uploadedFile("doc");
            if (upload == null) {
                ctx.status(400).result("File required");
                return;
            }

            String contentType = upload.contentType();
            boolean okType = contentType != null &&
                    (contentType.equals("application/pdf") ||
                            contentType.startsWith("image/"));
            if (!okType || upload.size() > 8 * 1024 * 1024) {
                ctx.status(415).result("Only PDF or image files <= 8MB allowed.");
                return;
            }

            byte[] bytes = upload.content().readAllBytes();
            String safeName = java.util.UUID.randomUUID() + "-" +
                    upload.filename().replaceAll("[^A-Za-z0-9._-]", "-");
            Path where = UPLOAD_DIR.resolve(safeName);
            Files.write(where, bytes);

            String email = ctx.formParam("email");
            String note  = ctx.formParam("note");

            try {
                Db.insertVerificationRequest(me.getId(), email, note, where.toString(), upload.filename(), sha256(bytes));
                ctx.redirect("/verify?submitted=1");
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Could not save request.");
            }
        });

        // Admin: review queue
        app.get("/admin/verify", ctx -> {
            var me   = SessionStore.currentUser(ctx);
            var reqs = Db.listPendingRequests(); // returns List<Map<String,Object>>
            if (reqs == null) reqs = java.util.Collections.emptyList();
            ctx.render("admin_verify.jte", java.util.Map.of("me", me, "reqs", reqs));
        }, Role.ADMIN);

        // Admin: approve
        app.post("/admin/verify/approve/{id}", ctx -> {
            int requestId = Integer.parseInt(ctx.pathParam("id"));
            String note   = ctx.formParam("decisionNote");
            if (note == null || note.isBlank()) note = "Approved by admin";
            Db.approveRequest(requestId, note);
            ctx.redirect("/admin/verify");
        }, Role.ADMIN);

// Admin: reject
        app.post("/admin/verify/reject/{id}", ctx -> {
            int requestId = Integer.parseInt(ctx.pathParam("id"));
            String note   = ctx.formParam("decisionNote");
            if (note == null || note.isBlank()) note = "Rejected by admin";
            Db.rejectRequest(requestId, note);
            ctx.redirect("/admin/verify");
        }, Role.ADMIN);
        // ---------- Auth: signup / login / logout ----------

        app.get("/signup", ctx ->
                ctx.render("signup.jte", model(
                        "me", SessionStore.currentUser(ctx)
                ))
        );

        app.post("/signup", ctx -> {
            String email = param(ctx, "email");
            String name  = param(ctx, "displayName");
            String pass  = param(ctx, "password");
            if (email == null || pass == null) { ctx.status(400).result("Missing credentials"); return; }

            if (!Db.emailAvailable(email)) { ctx.status(409).result("Email already in use"); return; }

            try {
                Db.insertUser(email, name, pass, Role.USER);
                User dbUser = Db.findUserByEmail(email);
                SessionStore.login(ctx, dbUser);
                ctx.redirect("/");
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Signup failed");
            }
        });

        app.get("/login", ctx ->
                ctx.render("login.jte", model(
                        "me", SessionStore.currentUser(ctx)
                ))
        );

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

            // Create cookie using new Javalin 6 API (no chaining)
            ctx.cookie("uid", uid, 60 * 60 * 24 * 365); // name, value, max-age
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

    private static String sha256(byte[] bytes) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            var d  = md.digest(bytes);
            var sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}