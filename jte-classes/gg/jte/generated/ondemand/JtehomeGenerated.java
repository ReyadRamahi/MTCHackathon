package gg.jte.generated.ondemand;
public final class JtehomeGenerated {
	public static final String JTE_NAME = "home.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,2,2,2,2,6,6,8,8,11,11,14,14,14,14,17,17,17,18,18,18,18,23,23,23,23,23,23,23,24,24,24,25,25,26,26,26,27,27,30,30,32,32,32,34,34,34,34,0,0,0,0};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, app.HomeView vm) {
		jteOutput.writeContent("\n");
		var content = new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\n<h1>Newest questions</h1>\n<p><a href=\"/ask\">Ask anonymously</a></p>\n\n");
				if (!vm.hasPosts()) {
					jteOutput.writeContent("\n  <p>No questions yet.</p>\n");
				}
				jteOutput.writeContent("\n\n<ul class=\"feed\">\n  ");
				for (app.Post p : vm.getPostsNewestFirst()) {
					jteOutput.writeContent("\n    <li class=\"post\">\n      <div class=\"votes\">\n        <form method=\"post\" action=\"/upvote/");
					jteOutput.setContext("form", "action");
					jteOutput.writeUserContent(p.id);
					jteOutput.setContext("form", null);
					jteOutput.writeContent("\">\n          <button class=\"vote-btn\" title=\"Upvote\">&#9650;</button>\n        </form>\n        <div class=\"count\">");
					jteOutput.setContext("div", null);
					jteOutput.writeUserContent(p.getUpvotes());
					jteOutput.writeContent("</div>\n        <form method=\"post\" action=\"/downvote/");
					jteOutput.setContext("form", "action");
					jteOutput.writeUserContent(p.id);
					jteOutput.setContext("form", null);
					jteOutput.writeContent("\">\n          <button class=\"vote-btn\" title=\"Downvote\">&#9660;</button>\n        </form>\n      </div>\n      <div class=\"body\">\n        <a class=\"title\" href=\"/q/");
					jteOutput.setContext("a", "href");
					jteOutput.writeUserContent(p.id);
					jteOutput.setContext("a", null);
					jteOutput.writeContent("\">");
					jteOutput.setContext("a", null);
					jteOutput.writeUserContent(p.title);
					jteOutput.writeContent("</a>\n        <div class=\"meta\">Posted ");
					jteOutput.setContext("div", null);
					jteOutput.writeUserContent(p.getCreatedAtDisplay());
					jteOutput.writeContent("</div>\n        ");
					if (!p.getSnippet().isEmpty()) {
						jteOutput.writeContent("\n          <div class=\"snippet\">");
						jteOutput.setContext("div", null);
						jteOutput.writeUserContent(p.getSnippet());
						jteOutput.writeContent("</div>\n        ");
					}
					jteOutput.writeContent("\n      </div>\n    </li>\n  ");
				}
				jteOutput.writeContent("\n</ul>\n");
			}
		};
		jteOutput.writeContent("\n\n");
		gg.jte.generated.ondemand.JtebaseGenerated.render(jteOutput, jteHtmlInterceptor, content);
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		app.HomeView vm = (app.HomeView)params.get("vm");
		render(jteOutput, jteHtmlInterceptor, vm);
	}
}
