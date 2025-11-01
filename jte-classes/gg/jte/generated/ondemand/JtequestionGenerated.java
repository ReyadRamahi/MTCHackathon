package gg.jte.generated.ondemand;
public final class JtequestionGenerated {
	public static final String JTE_NAME = "question.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,8,8,8,8,12,12,18,18,18,18,22,22,22,24,24,24,24,30,30,30,32,32,33,33,33,34,34,37,37,37,38,38,39,39,39,39,40,40,47,47,47,47,47,47,50,50,54,54,56,56,58,58,58,60,60,60,63,63,65,65,67,67,68,68,68,68,79,79,84,84,88,88,93,93,93,0,1,1,1,1};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, app.User me, app.Post post) {
		jteOutput.writeContent("\n<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\">\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n  <title>");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(post.getTitle());
		jteOutput.writeContent(" - Amaanah</title>\n  <link rel=\"stylesheet\" href=\"/styles.css\">\n</head>\n<body>\n  ");
		gg.jte.generated.ondemand.JteheaderGenerated.render(jteOutput, jteHtmlInterceptor, me);
		jteOutput.writeContent("\n\n  <main class=\"container\">\n    <div class=\"post-wrapper\">\n      <article class=\"post-card\">\n        <div class=\"vote-box\">\n          <form method=\"post\" action=\"/q/");
		jteOutput.setContext("form", "action");
		jteOutput.writeUserContent(post.getId());
		jteOutput.setContext("form", null);
		jteOutput.writeContent("/upvote\">\n            <button class=\"vote-btn up\" type=\"submit\" title=\"Upvote\">▲</button>\n          </form>\n\n          <span class=\"vote-score\">");
		jteOutput.setContext("span", null);
		jteOutput.writeUserContent(post.getScore());
		jteOutput.writeContent("</span>\n\n          <form method=\"post\" action=\"/q/");
		jteOutput.setContext("form", "action");
		jteOutput.writeUserContent(post.getId());
		jteOutput.setContext("form", null);
		jteOutput.writeContent("/downvote\">\n            <button class=\"vote-btn down\" type=\"submit\" title=\"Downvote\">▼</button>\n          </form>\n        </div>\n\n        <div class=\"post-body\">\n          <h1 class=\"post-heading\">");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(post.getTitle());
		jteOutput.writeContent("</h1>\n\n          ");
		if (!post.getBody().isEmpty()) {
			jteOutput.writeContent("\n            <p class=\"post-text\">");
			jteOutput.setContext("p", null);
			jteOutput.writeUserContent(post.getBody());
			jteOutput.writeContent("</p>\n          ");
		}
		jteOutput.writeContent("\n\n          <div class=\"post-meta\">\n            Posted ");
		jteOutput.setContext("div", null);
		jteOutput.writeUserContent(post.getCreatedAtDisplay());
		jteOutput.writeContent("\n            ");
		if (me != null && post.getOwnerUid() != null && post.getOwnerUid().equals(me.getId())) {
			jteOutput.writeContent("\n              • <a href=\"/q/");
			jteOutput.setContext("a", "href");
			jteOutput.writeUserContent(post.getId());
			jteOutput.setContext("a", null);
			jteOutput.writeContent("/edit\" style=\"color: #3d7a52; font-weight: 600;\">Edit</a>\n            ");
		}
		jteOutput.writeContent("\n          </div>\n        </div>\n      </article>\n\n      <section class=\"comments-section\">\n        <h2 class=\"section-title\">\n          ");
		jteOutput.setContext("h2", null);
		jteOutput.writeUserContent(post.getComments().size());
		jteOutput.writeContent(" Comment");
		jteOutput.setContext("h2", null);
		jteOutput.writeUserContent(post.getComments().size() == 1 ? "" : "s");
		jteOutput.writeContent("\n        </h2>\n\n        ");
		if (post.getComments().isEmpty()) {
			jteOutput.writeContent("\n          <p style=\"color: #9a8b6f; font-size: 14px; margin-bottom: 20px;\">\n            No comments yet. Be the first to respond!\n          </p>\n        ");
		} else {
			jteOutput.writeContent("\n          <ul class=\"comment-list\">\n            ");
			for (app.Post.Comment c : post.getCommentsNewestFirst()) {
				jteOutput.writeContent("\n              <li class=\"comment-item\">\n                <div class=\"comment-body\">");
				jteOutput.setContext("div", null);
				jteOutput.writeUserContent(c.getBody());
				jteOutput.writeContent("</div>\n                <div class=\"comment-meta\">\n                  <strong style=\"color: #3d7a52;\">Scholar</strong> • ");
				jteOutput.setContext("div", null);
				jteOutput.writeUserContent(c.getCreatedAtDisplay());
				jteOutput.writeContent("\n                </div>\n              </li>\n            ");
			}
			jteOutput.writeContent("\n          </ul>\n        ");
		}
		jteOutput.writeContent("\n\n        ");
		if (me != null && (me.getRole() == app.Role.SCHOLAR || me.getRole() == app.Role.ADMIN)) {
			jteOutput.writeContent("\n          <form class=\"comment-form\" method=\"post\" action=\"/q/");
			jteOutput.setContext("form", "action");
			jteOutput.writeUserContent(post.getId());
			jteOutput.setContext("form", null);
			jteOutput.writeContent("/comment\">\n            <textarea\n              name=\"body\"\n              rows=\"4\"\n              placeholder=\"What are your thoughts?\"\n              required\n            ></textarea>\n            <div class=\"form-actions\">\n              <button class=\"btn btn-primary\" type=\"submit\">Comment</button>\n            </div>\n          </form>\n        ");
		} else if (me != null && me.getRole() == app.Role.USER) {
			jteOutput.writeContent("\n          <div style=\"background: #f9f6f0; padding: 16px; border-radius: 4px; text-align: center; color: #9a8b6f;\">\n            <p>Only verified scholars can comment.</p>\n            <a href=\"/verify\" style=\"color: #3d7a52; font-weight: 600;\">Request verification</a>\n          </div>\n        ");
		} else {
			jteOutput.writeContent("\n          <div style=\"background: #f9f6f0; padding: 16px; border-radius: 4px; text-align: center; color: #9a8b6f;\">\n            <p><a href=\"/login\" style=\"color: #3d7a52; font-weight: 600;\">Log in</a> to comment</p>\n          </div>\n        ");
		}
		jteOutput.writeContent("\n      </section>\n    </div>\n  </main>\n</body>\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		app.User me = (app.User)params.get("me");
		app.Post post = (app.Post)params.get("post");
		render(jteOutput, jteHtmlInterceptor, me, post);
	}
}
