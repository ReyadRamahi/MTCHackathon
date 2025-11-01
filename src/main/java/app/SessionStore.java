package app;

import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.SameSite;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public final class SessionStore {
    private static final Map<String, User> USERS = new ConcurrentHashMap<>();

    public static String getOrCreateUid(Context ctx) {
        String uid = ctx.cookie("uid");
        if (uid == null || uid.isBlank()) {
            uid = UUID.randomUUID().toString();
            Cookie c = new Cookie("uid", uid);
            c.setPath("/");
            c.setHttpOnly(true);
            c.setSameSite(SameSite.LAX);
            c.setMaxAge((int) Duration.ofDays(365).getSeconds());
            ctx.cookie(c);
        }
        return uid;
    }

    public static User currentUser(Context ctx) {
        String uid = getOrCreateUid(ctx);
        return USERS.computeIfAbsent(uid, k -> new User(uid, "anon", Role.USER));
    }

    public static void login(Context ctx, User dbUser) {
        String uid = getOrCreateUid(ctx);                  // keep the same browser uid
        USERS.put(uid, new User(uid, dbUser.getDisplayName(), dbUser.getRole()));
    }

    public static void logout(Context ctx) {
        String uid = ctx.cookie("uid");
        if (uid != null) {
            USERS.remove(uid);
            ctx.removeCookie("uid");
        }
    }

    public static void promoteToScholar(String targetUid) {
        User u = USERS.get(targetUid);
        if (u != null) u.setRole(Role.SCHOLAR);
    }

    // optional helper if you need to inspect
    public static Map<String, User> users() { return USERS; }
}