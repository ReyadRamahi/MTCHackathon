package app;

import io.javalin.http.Context;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionStore {
    private static final Map<String, User> USERS = new ConcurrentHashMap<>();

    private SessionStore(){}

    public static String getOrCreateUid(Context ctx) {
        String uid = ctx.cookie("uid");
        if (uid == null || uid.isBlank()) {
            uid = UUID.randomUUID().toString();
            // basic cookie (sameSite Lax, 1 year)
            ctx.cookie("uid", uid, (int) Duration.ofDays(365).getSeconds());
        }
        return uid;
    }

    public static User currentUser(Context ctx) {
        String uid = getOrCreateUid(ctx);
        // prefer DB-stored user if present
        try {
            var fromDb = Db.findUserByUid(uid);
            if (fromDb.isPresent()) {
                USERS.put(uid, fromDb.get());
                return fromDb.get();
            }
        } catch (Exception ignored) {}
        // fall back to in-memory anon USER
        return USERS.computeIfAbsent(uid, k -> new User(uid, "anon", Role.USER));
    }

    public static void login(Context ctx, User u) {
        // bind cookie to that user's uid
        ctx.cookie("uid", u.getId(), (int) Duration.ofDays(365).getSeconds());
        USERS.put(u.getId(), u);
    }

    public static void logout(Context ctx) {
        String uid = ctx.cookie("uid");
        if (uid != null) USERS.remove(uid);
        ctx.removeCookie("uid");
    }
}