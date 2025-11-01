package gg.jte.generated.ondemand;
public final class JtehomeGenerated {
	public static final String JTE_NAME = "home.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,2,21,21,21,23,23,26,26,29,29,29,29,33,33,33,35,35,35,35,41,41,41,41,41,41,41,43,43,44,44,44,45,45,48,48,48,52,52,56,56,56,0,1,1,1,1};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, app.User me, app.HomeView vmimport header.jte;
) {
		jteOutput.writeContent("\n@header(me)\n<!DOCTYPE html>\n<html>\n<head>\n  <meta charset=\"UTF-8\">\n  <title>Amaanah • Newest</title>\n  <link rel=\"stylesheet\" href=\"/styles.css\">\n</head>\n<body class=\"site\">\n<header class=\"site-header\">\n  <a class=\"home-link\" href=\"/\">Amaanah</a> ·\n  <a class=\"ask-link\" href=\"/ask\">Ask anonymously</a>\n</header>\n\n<main class=\"feed\">\n  <h1 class=\"page-title\">Newest questions</h1>\n\n  ");
		if (!vm.hasPosts()) {
			jteOutput.writeContent("\n    <p class=\"empty\">No questions yet.</p>\n  ");
		}
		jteOutput.writeContent("\n\n  <ul class=\"post-list\">\n    ");
		for (app.Post p : vm.getPostsNewestFirst()) {
			jteOutput.writeContent("\n      <li class=\"post-row\">\n        <div class=\"vote-col\">\n          <form method=\"post\" action=\"/q/");
			jteOutput.setContext("form", "action");
			jteOutput.writeUserContent(p.getId());
			jteOutput.setContext("form", null);
			jteOutput.writeContent("/upvote\">\n            <button class=\"vote up\" title=\"Upvote\" aria-label=\"Upvote\">▲</button>\n          </form>\n\n          <div class=\"score\">");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(p.getScore());
			jteOutput.writeContent("</div>\n\n          <form method=\"post\" action=\"/q/");
			jteOutput.setContext("form", "action");
			jteOutput.writeUserContent(p.getId());
			jteOutput.setContext("form", null);
			jteOutput.writeContent("/downvote\">\n            <button class=\"vote down\" title=\"Downvote\" aria-label=\"Downvote\">▼</button>\n          </form>\n        </div>\n\n        <div class=\"content-col\">\n          <a class=\"title\" href=\"/q/");
			jteOutput.setContext("a", "href");
			jteOutput.writeUserContent(p.getId());
			jteOutput.setContext("a", null);
			jteOutput.writeContent("\">");
			jteOutput.setContext("a", null);
			jteOutput.writeUserContent(p.getTitle());
			jteOutput.writeContent("</a>\n\n          ");
			if (!p.getSnippet().isEmpty()) {
				jteOutput.writeContent("\n            <p class=\"snippet\">");
				jteOutput.setContext("p", null);
				jteOutput.writeUserContent(p.getSnippet());
				jteOutput.writeContent("</p>\n          ");
			}
			jteOutput.writeContent("\n\n          <div class=\"meta\">\n            Posted ");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(p.getCreatedAtDisplay());
			jteOutput.writeContent("\n          </div>\n        </div>\n      </li>\n    ");
		}
		jteOutput.writeContent("\n  </ul>\n</main>\n</body>\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		app.User me = (app.User)params.get("me");
		app.HomeView vm = (app.HomeView)params.get("vm");
		render(jteOutput, jteHtmlInterceptor, me, vm);
	}
}
