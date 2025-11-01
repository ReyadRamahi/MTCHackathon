package app;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStore {
    // sid -> userId
    private static final Map<String, Integer> SESSIONS = new ConcurrentHashMap<>();

    public static String create(int userId) {
        String sid = UUID.randomUUID().toString();
        SESSIONS.put(sid, userId);
        return sid;
    }

    public static Integer getUserId(String sid) {
        return sid == null ? null : SESSIONS.get(sid);
    }

    public static void remove(String sid) {
        if (sid != null) SESSIONS.remove(sid);
    }
}