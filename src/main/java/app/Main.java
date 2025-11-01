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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class Main {

    // -------- In-memory “DB-ish” storage --------
    private static final List<Post> POSTS = new CopyOnWriteArrayList<>();
    //documents
    private static final Path UPLOAD_DIR = Path.of("uploads", "secure").toAbsolutePath();

    // user cookie uid -> (postId -> -1/0/+1) to rate-limit per user
    private static final Map<String, Map<Integer, Integer>> VOTES_BY_USER = new ConcurrentHashMap<>();

    // postId -> ownerUid (so only the author can edit)
    private static final Map<Integer, String> POST_OWNER = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        // ----- JTE engine -----
        Path templatesDir = Path.of("src/main/resources/templates").toAbsolutePath();
        TemplateEngine engine = TemplateEngine.create(new DirectoryCodeResolver(templatesDir), ContentType.Html);
        // Create private upload folder (not in /public)
        try {
            Files.createDirectories(UPLOAD_DIR);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Could not create upload directory: " + UPLOAD_DIR, e);
        }

        // ----- Javalin app -----
        Javalin app = Javalin.create((JavalinConfig cfg) -> {
            cfg.fileRenderer(new JavalinJte(engine)); // render *.jte
            cfg.staticFiles.add("/public");           // /styles.css etc.
        });

        try { Db.init(); } catch (Exception e) { e.printStackTrace(); }

        // ===================== ROUTES =====================

        // Home (feed)
        app.get("/", ctx -> {
            HomeView vm = new HomeView(POSTS);
            render(ctx, "home.jte", mapOf("vm", vm));
        });

        // Ask (GET) – new
        app.get("/ask", ctx -> {
            render(ctx, "ask.jte", mapOf("mode", "new"));
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

        // View a single post
        app.get("/q/{id}", ctx -> {
            Post p = findPostOr404(ctx);
            render(ctx, "question.jte", mapOf("post", p));
        });

        // Add comment (anyone). If logged out, shows as anon/USER.
        app.post("/q/{id}/comment", ctx -> {
            Post p = findPostOr404(ctx);
            String body = param(ctx, "body");
            if (body == null || body.isBlank()) {
                ctx.status(400).result("Comment required");
                return;
            }
            User me = SessionStore.currentUser(ctx);
            String authorName = (me == null) ? "anon" : me.getPublicName();
            Role   authorRole = (me == null) ? Role.USER : me.getRole();
            p.addComment(body, authorName, authorRole);
            ctx.redirect("/q/" + p.getId());
        });

        // Edit (GET) – only owner may see form (ask.jte in "edit" mode)
        app.get("/q/{id}/edit", ctx -> {
            Post p = findPostOr404(ctx);
            String uid = getOrCreateUid(ctx);
            if (!uid.equals(POST_OWNER.getOrDefault(p.getId(), ""))) {
                ctx.status(403).result("Forbidden");
                return;
            }
            render(ctx, "ask.jte", mapOf(
                    "mode", "edit",
                    "post", p
            ));
        });

        // Edit (POST) – only owner may save
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

        // ---------- Verification pages ----------

        // Public: request verification form
        app.get("/verify", ctx -> {
            render(ctx, "verify.jte", null);
        });

        // Public: submit verification request (stub)
        app.post("/verify", ctx -> {
            var me  = SessionStore.currentUser(ctx);
            if (me == null) { ctx.status(401).result("Please log in first."); return; }

            String uid   = SessionStore.getOrCreateUid(ctx); // add a public helper in SessionStore if needed
            String email = ctx.formParam("email");           // optional contact
            String note  = ctx.formParam("details");         // from your form

            var upload = ctx.uploadedFile("diploma");        // <input type="file" name="diploma">
            if (upload == null) { ctx.status(400).result("Please attach a diploma or transcript."); return; }

            // Basic validations
            long maxBytes = 8L * 1024 * 1024;
            if (upload.size() > maxBytes) { ctx.status(413).result("File too large (max 8MB)."); return; }
            var ct = upload.contentType();
            if (ct == null || !(ct.startsWith("image/") || ct.equals("application/pdf"))) {
                ctx.status(415).result("Only PDF or image files allowed.");
                return;
            }

            // Read bytes & store
            byte[] bytes = upload.content().readAllBytes();
            String hash  = sha256(bytes);
            String safeName = java.util.UUID.randomUUID() + "-" + upload.filename().replaceAll("[^A-Za-z0-9._-]","_");
            var where = UPLOAD_DIR.resolve(safeName);
            java.nio.file.Files.write(where, bytes);

            // Record request
            try {
                Db.insertVerificationRequest(uid, email, note, where.toString(), upload.filename(), hash);
                ctx.redirect("/verify?submitted=1");
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Could not save request.");
            }
        });

        // Admin: review queue
        // Serve uploaded verification file (admin-only)
        app.get("/admin/verify/file/{name}", ctx -> {
            var me = SessionStore.currentUser(ctx);
            if (me == null || me.getRole() != Role.ADMIN) { ctx.status(403).result("Forbidden"); return; }

            String name = ctx.pathParam("name");
            // basic allowlist: only filenames we created (no '/')
            if (name.contains("/") || name.contains("..")) { ctx.status(400).result("Bad name"); return; }

            var file = UPLOAD_DIR.resolve(name);
            if (!java.nio.file.Files.exists(file)) { ctx.status(404).result("Not found"); return; }

            // naive content-type guess
            String ct = java.nio.file.Files.probeContentType(file);
            if (ct == null) ct = "application/octet-stream";

            ctx.contentType(ct);
            ctx.result(java.nio.file.Files.newInputStream(file));
        });

// Show pending queue
        app.get("/admin/verify", ctx -> {
            var me   = SessionStore.currentUser(ctx);
            var list = Db.listPendingRequests();       // List<Map<String,Object>>

            Map<String, Object> item = (list == null || list.isEmpty()) ? null : list.get(0);

            String uid   = item == null ? null : (String) item.get("uid");
            String email = item == null ? null : (String) item.get("email");
            String note  = item == null ? null : (String) item.get("note");
            String file  = item == null ? null : (String) item.get("file_name");
            String when  = item == null ? null : String.valueOf(item.get("created_at"));
            String url   = (file == null) ? null : "/uploads/" + file;
            boolean isPdf = file != null && file.toLowerCase().endsWith(".pdf");

            ctx.render("admin_verify.jte", java.util.Map.of(
                    "me", me,
                    "reqs", list,
                    "uid", uid,
                    "email", email,
                    "note", note,
                    "file", file,
                    "when", when,
                    "url", url,
                    "isPdf", isPdf
            ));
        }, Role.ADMIN);

// Approve / Reject
        app.post("/admin/verify/{id}/approve", ctx -> {
            var me = SessionStore.currentUser(ctx);
            if (me == null || me.getRole() != Role.ADMIN) { ctx.status(403).result("Forbidden"); return; }

            int id = Integer.parseInt(ctx.pathParam("id"));
            try { Db.approveVerification(id, me.getId()); }
            catch (Exception e) { e.printStackTrace(); }
            ctx.redirect("/admin/verify");
        });
        app.post("/admin/verify/{id}/reject", ctx -> {
            var me = SessionStore.currentUser(ctx);
            if (me == null || me.getRole() != Role.ADMIN) { ctx.status(403).result("Forbidden"); return; }

            int id = Integer.parseInt(ctx.pathParam("id"));
            try { Db.rejectVerification(id, me.getId()); }
            catch (Exception e) { e.printStackTrace(); }
            ctx.redirect("/admin/verify");
        });
        // admin remove
        // Admin delete a post
        app.post("/admin/posts/{id}/delete", ctx -> {
            var me = SessionStore.currentUser(ctx);
            if (me == null || me.getRole() != Role.ADMIN) { ctx.status(403).result("Forbidden"); return; }

            int id = Integer.parseInt(ctx.pathParam("id"));
            if (id < 0 || id >= POSTS.size()) { ctx.status(404).result("Not found"); return; }

            POSTS.remove(id);
            POST_OWNER.remove(id);
            // Optional: also purge votes for this id
            VOTES_BY_USER.values().forEach(map -> map.remove(id));

            ctx.redirect("/");
        });


        app.post("/admin/posts/{id}/comments/{idx}/delete", ctx -> {
            var me = SessionStore.currentUser(ctx);
            if (me == null || me.getRole() != Role.ADMIN) { ctx.status(403).result("Forbidden"); return; }

            int id  = Integer.parseInt(ctx.pathParam("id"));
            int idx = Integer.parseInt(ctx.pathParam("idx"));

            if (id < 0 || id >= POSTS.size()) { ctx.status(404).result("Not found"); return; }
            var p = POSTS.get(id);
            p.removeCommentAt(idx);

            ctx.redirect("/q/" + id);
        });
        // ---------- Auth: signup / login / logout ----------

        app.get("/signup", ctx -> render(ctx, "signup.jte", null));

        app.post("/signup", ctx -> {
            String email = param(ctx, "email");
            String name  = param(ctx, "displayName");
            String pass  = param(ctx, "password");
            if (email == null || pass == null) { ctx.status(400).result("Missing credentials"); return; }
            if (!Db.emailAvailable(email)) { ctx.status(409).result("Email already in use"); return; }

            try {
                Db.insertUser(email, name, pass, Role.USER);
                User dbUser = Db.findUserByEmail(email);
                SessionStore.login(ctx, dbUser); // binds DB identity to this browser's uid
                ctx.redirect("/");
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Signup failed");
            }
        });

        app.get("/login", ctx -> render(ctx, "login.jte", null));

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
            SessionStore.logout(ctx); // clears “logged-in identity”, keeps anonymous uid cookie
            ctx.redirect("/");
        });

        // Optional: quick demo — promote a known session uid to SCHOLAR
        app.post("/admin/verify/{uid}", ctx -> {
            User me = SessionStore.currentUser(ctx);
            if (me == null || !me.isAdmin()) { ctx.status(403).result("Forbidden"); return; }
            String targetUid = ctx.pathParam("uid");
            SessionStore.promoteToScholar(targetUid);
            ctx.result("OK");
        });


        app.start(7001);
    }

    // ---------------- Helpers ----------------

    /** Always include "me" safely; never pass null into Map.of. */
    private static void render(Context ctx, String template, Map<String, Object> model) {
        Map<String, Object> m = (model == null)
                ? new HashMap<>()
                : new HashMap<>(model);
        m.putIfAbsent("me", SessionStore.currentUser(ctx)); // may be null — that's fine in HashMap/JTE
        ctx.render(template, m);
    }

    /** Tiny convenience for building small models. */
    private static Map<String, Object> mapOf(Object... kv) {
        HashMap<String, Object> m = new HashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            m.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return m;
    }

    private static String param(Context ctx, String name) {
        String v = ctx.formParam(name);
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private static Post findPostOr404(Context ctx) {
        int id;
        try { id = Integer.parseInt(ctx.pathParam("id")); }
        catch (Exception e) { ctx.status(404).result("Not found"); throw new IllegalStateException("Post not found"); }
        if (id < 0 || id >= POSTS.size()) {
            ctx.status(404).result("Not found");
            throw new IllegalStateException("Post not found");
        }
        return POSTS.get(id);
    }

    /** Stable, anonymous per-browser id for ownership + rate-limiting. Not a login. */
    private static String getOrCreateUid(Context ctx) {
        String uid = ctx.cookie("uid");
        if (uid == null || uid.isBlank()) {
            uid = UUID.randomUUID().toString();
            // Keep it simple/portable (advanced flags caused compile issues on your setup)
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
    private static String sha256(byte[] data) {
        try {
            var digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data);

            // Convert bytes → hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 hashing failed", e);
        }
    }
}