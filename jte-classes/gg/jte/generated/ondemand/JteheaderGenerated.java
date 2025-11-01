package gg.jte.generated.ondemand;
public final class JteheaderGenerated {
	public static final String JTE_NAME = "header.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,11,11,11,11,11,15,15,15,15,17,17,17,20,20,20,28,28,33,33,36,36,37,37,39,39,59,59,59,0,0,0,0};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, app.User me) {
		jteOutput.writeContent("\n<header class=\"header\">\n  <div class=\"header-content\">\n    <a class=\"brand\" href=\"/\">Amaaneh</a>\n\n    <nav class=\"header-nav\">\n      <a class=\"header-link\" href=\"/ask\">+ Ask Question</a>\n\n      <button\n        id=\"profileBtn\"\n        class=\"profile-btn ");
		jteOutput.setContext("button", "class");
		jteOutput.writeUserContent(me != null ? "is-auth" : "");
		jteOutput.setContext("button", null);
		jteOutput.writeContent("\"\n        type=\"button\"\n        aria-haspopup=\"true\"\n        aria-expanded=\"false\">\n        <span class=\"status-dot ");
		jteOutput.setContext("span", "class");
		jteOutput.writeUserContent(me != null ? "online" : "offline");
		jteOutput.setContext("span", null);
		jteOutput.writeContent("\"></span>\n        <span class=\"profile-name\">\n          ");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(me == null || me.isAnon() ? "anon" : me.getDisplayName());
		jteOutput.writeContent("\n        </span>\n        <span class=\"badge role\">\n          ");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(me == null ? "USER" : me.getRole().name());
		jteOutput.writeContent("\n        </span>\n        <svg class=\"chev\" viewBox=\"0 0 20 20\" width=\"16\" height=\"16\" aria-hidden=\"true\">\n          <path d=\"M5 7l5 6 5-6\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"/>\n        </svg>\n      </button>\n\n      <div id=\"profileMenu\" class=\"menu\" role=\"menu\" aria-labelledby=\"profileBtn\">\n        ");
		if (me != null) {
			jteOutput.writeContent("\n          <a role=\"menuitem\" class=\"menu-item\" href=\"/verify\">Get Verified</a>\n          <form role=\"menuitem\" class=\"menu-item as-form\" method=\"post\" action=\"/logout\">\n            <button type=\"submit\">Log Out</button>\n          </form>\n        ");
		} else {
			jteOutput.writeContent("\n          <a role=\"menuitem\" class=\"menu-item\" href=\"/login\">Log In</a>\n          <a role=\"menuitem\" class=\"menu-item\" href=\"/signup\">Sign Up</a>\n        ");
		}
		jteOutput.writeContent("\n        ");
		if (me != null && me.getRole() == app.Role.ADMIN) {
			jteOutput.writeContent("\n          <a class=\"menu-item\" href=\"/admin/verify\">Review Verifications</a>\n        ");
		}
		jteOutput.writeContent("\n      </div>\n    </nav>\n  </div>\n</header>\n\n<script>\n  (function () {\n    const btn  = document.getElementById('profileBtn');\n    const menu = document.getElementById('profileMenu');\n    const open = () => { menu.classList.add('open');  btn.setAttribute('aria-expanded','true');  btn.classList.add('open'); };\n    const shut = () => { menu.classList.remove('open'); btn.setAttribute('aria-expanded','false'); btn.classList.remove('open'); };\n\n    btn.addEventListener('click', (e) => {\n      e.stopPropagation();\n      menu.classList.contains('open') ? shut() : open();\n    });\n    document.addEventListener('click', shut);\n    menu.addEventListener('click', (e) => e.stopPropagation());\n  })();\n</script>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		app.User me = (app.User)params.get("me");
		render(jteOutput, jteHtmlInterceptor, me);
	}
}
