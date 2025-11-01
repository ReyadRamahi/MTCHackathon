package app;

import io.javalin.security.RouteRole;

public enum Role implements RouteRole {
    ADMIN, SCHOLAR, USER
}