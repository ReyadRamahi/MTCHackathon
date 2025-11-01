package gg.jte.generated.ondemand;
public final class JtehomeGenerated {
	public static final String JTE_NAME = "home.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,12,12,12,17,17,21,21,23,23,26,26,26,26,30,30,30,32,32,32,32,38,38,38,38,38,38,38,40,40,41,41,41,42,42,45,45,45,46,46,47,47,47,47,47,47,48,48,52,52,54,54,57,57,57,0,1,1,1,1};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, app.User me, app.HomeView vm) {
		jteOutput.writeContent("\n<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\">\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n  <title>Amaanah - Ask Questions, Get Scholar Answers</title>\n  <link rel=\"stylesheet\" href=\"/styles.css\">\n</head>\n<body>\n  ");
		gg.jte.generated.ondemand.JteheaderGenerated.render(jteOutput, jteHtmlInterceptor, me);
		jteOutput.writeContent("\n\n  <main class=\"container\">\n    <h1 class=\"page-title\">Recent Questions</h1>\n\n    ");
		if (!vm.hasPosts()) {
			jteOutput.writeContent("\n      <div class=\"empty-state\">\n        <p>No questions yet. Be the first to ask!</p>\n      </div>\n    ");
		} else {
			jteOutput.writeContent("\n      <ul class=\"post-list\">\n        ");
			for (app.Post p : vm.getPostsNewestFirst()) {
				jteOutput.writeContent("\n          <li class=\"post-item\">\n            <div class=\"vote-box\">\n              <form method=\"post\" action=\"/q/");
				jteOutput.setContext("form", "action");
				jteOutput.writeUserContent(p.getId());
				jteOutput.setContext("form", null);
				jteOutput.writeContent("/upvote\">\n                <button class=\"vote-btn up\" type=\"submit\" title=\"Upvote\">▲</button>\n              </form>\n\n              <span class=\"vote-score\">");
				jteOutput.setContext("span", null);
				jteOutput.writeUserContent(p.getScore());
				jteOutput.writeContent("</span>\n\n              <form method=\"post\" action=\"/q/");
				jteOutput.setContext("form", "action");
				jteOutput.writeUserContent(p.getId());
				jteOutput.setContext("form", null);
				jteOutput.writeContent("/downvote\">\n                <button class=\"vote-btn down\" type=\"submit\" title=\"Downvote\">▼</button>\n              </form>\n            </div>\n\n            <div class=\"post-content\">\n              <a class=\"post-title\" href=\"/q/");
				jteOutput.setContext("a", "href");
				jteOutput.writeUserContent(p.getId());
				jteOutput.setContext("a", null);
				jteOutput.writeContent("\">");
				jteOutput.setContext("a", null);
				jteOutput.writeUserContent(p.getTitle());
				jteOutput.writeContent("</a>\n\n              ");
				if (!p.getSnippet().isEmpty()) {
					jteOutput.writeContent("\n                <p class=\"post-snippet\">");
					jteOutput.setContext("p", null);
					jteOutput.writeUserContent(p.getSnippet());
					jteOutput.writeContent("</p>\n              ");
				}
				jteOutput.writeContent("\n\n              <div class=\"post-meta\">\n                Posted ");
				jteOutput.setContext("div", null);
				jteOutput.writeUserContent(p.getCreatedAtDisplay());
				jteOutput.writeContent("\n                ");
				if (!p.getComments().isEmpty()) {
					jteOutput.writeContent("\n                  • ");
					jteOutput.setContext("div", null);
					jteOutput.writeUserContent(p.getComments().size());
					jteOutput.writeContent(" comment");
					jteOutput.setContext("div", null);
					jteOutput.writeUserContent(p.getComments().size() == 1 ? "" : "s");
					jteOutput.writeContent("\n                ");
				}
				jteOutput.writeContent("\n              </div>\n            </div>\n          </li>\n        ");
			}
			jteOutput.writeContent("\n      </ul>\n    ");
		}
		jteOutput.writeContent("\n  </main>\n</body>\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		app.User me = (app.User)params.get("me");
		app.HomeView vm = (app.HomeView)params.get("vm");
		render(jteOutput, jteHtmlInterceptor, me, vm);
	}
}
