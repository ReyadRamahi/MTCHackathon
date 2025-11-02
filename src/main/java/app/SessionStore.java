

package app;

import io.javalin.http.Context;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import io.javalin.http.Cookie;
import io.javalin.http.SameSite;

public final class SessionStore {
    private static final Map<String, User> USERS = new ConcurrentHashMap<>();

    /** Get or create a stable per-browser uid cookie (no login implied). */
    public static String getOrCreateUid(Context ctx) {
        String uid = ctx.cookie("uid");
        if (uid == null || uid.isBlank()) {
            uid = java.util.UUID.randomUUID().toString();
            Cookie c = new Cookie("uid", uid);
            c.setPath("/");
            c.setHttpOnly(true);
            c.setSameSite(SameSite.LAX);
            c.setMaxAge((int) Duration.ofDays(365).getSeconds());
            ctx.cookie(c);
        }
        return uid;
    }

    /** Current logged-in user for this browser, or null if not logged in. */
    public static User currentUser(Context ctx) {
        String uid = ctx.cookie("uid");
        return (uid == null) ? null : USERS.get(uid);   // <-- no computeIfAbsent here
    }

    /** Bind DB user identity to this browser uid (login). */
    public static void login(Context ctx, User dbUser) {
        String uid = getOrCreateUid(ctx); // keep / create cookie
        // store only minimal session view (id = uid so ownership keeps working)
        User sessionUser = new User(uid, dbUser.getDisplayName(), dbUser.getRole());
        USERS.put(uid, sessionUser);
    }

    /** Unbind identity but KEEP the uid cookie so post-ownership still works. */
    public static void logout(Context ctx) {
        String uid = ctx.cookie("uid");
        if (uid != null) USERS.remove(uid);
        // do NOT remove the cookie; otherwise you lose post edit-ownership
    }
    public static void promoteToScholar(String targetUid) {
        User u = USERS.get(targetUid);
        if (u != null) u.setRole(Role.SCHOLAR);
    }

    // SessionStore.java
    public static void setRoleForUid(String uid, Role role) {
        if (uid == null || uid.isBlank() || role == null) return;
        User u = USERS.get(uid);          // <- you already have this cache
        if (u != null) {
            u.setRole(role);              // update in-memory role for that uid
        }
        // If the applicant hasn't visited yet (no User in USERS),
        // it's fine to do nothing here. When they next make a request
        // and you load/create their User, their role can be derived
        // from DB if you later persist it. For now in-memory is enough.
    }

}