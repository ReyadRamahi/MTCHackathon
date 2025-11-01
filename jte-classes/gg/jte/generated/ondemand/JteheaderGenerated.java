package gg.jte.generated.ondemand;
public final class JteheaderGenerated {
	public static final String JTE_NAME = "header.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,9,9,9,12,12,14,14,14,15,15,15,15,16,16,16,20,20,22,22,24,24,26,26,31,31,34,34,34,0,0,0,0};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, app.User me) {
		jteOutput.writeContent("\n<header class=\"header\">\n  <div class=\"header-content\">\n    <a class=\"brand\" href=\"/\">Amaanah</a>\n\n    <nav class=\"header-nav\">\n      <a class=\"header-link\" href=\"/ask\">+ Ask Question</a>\n\n      ");
		if (me == null) {
			jteOutput.writeContent("\n        <a class=\"btn btn-ghost\" href=\"/login\">Log In</a>\n        <a class=\"btn btn-primary\" href=\"/signup\">Sign Up</a>\n      ");
		} else {
			jteOutput.writeContent("\n        <div class=\"user-info\">\n          <span class=\"user-name\">");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(me.getDisplayName());
			jteOutput.writeContent("</span>\n          <span class=\"role-badge role-");
			jteOutput.setContext("span", "class");
			jteOutput.writeUserContent(me.getRole().name().toLowerCase());
			jteOutput.setContext("span", null);
			jteOutput.writeContent("\">\n            ");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(me.getRole().name());
			jteOutput.writeContent("\n          </span>\n        </div>\n\n        ");
			if (me.getRole() == app.Role.USER) {
				jteOutput.writeContent("\n          <a class=\"header-link\" href=\"/verify\">Get Verified</a>\n        ");
			}
			jteOutput.writeContent("\n\n        ");
			if (me.getRole() == app.Role.ADMIN) {
				jteOutput.writeContent("\n          <a class=\"header-link\" href=\"/admin/verify\">Verify Queue</a>\n        ");
			}
			jteOutput.writeContent("\n\n        <form method=\"post\" action=\"/logout\" style=\"display:inline;\">\n          <button class=\"btn btn-ghost\" type=\"submit\">Log Out</button>\n        </form>\n      ");
		}
		jteOutput.writeContent("\n    </nav>\n  </div>\n</header>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		app.User me = (app.User)params.get("me");
		render(jteOutput, jteHtmlInterceptor, me);
	}
}
