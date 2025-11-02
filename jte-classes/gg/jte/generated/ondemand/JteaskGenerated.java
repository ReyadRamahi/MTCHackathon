package gg.jte.generated.ondemand;
import app.User;
import app.Post;
public final class JteaskGenerated {
	public static final String JTE_NAME = "ask.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,2,2,11,11,11,11,15,15,20,20,20,27,27,27,27,27,27,27,27,27,35,35,35,35,35,35,35,35,35,49,49,49,53,53,53,59,59,59,2,3,4,4,4,4};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, User me, String mode, Post post) {
		jteOutput.writeContent("\n<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\">\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n  <title>");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(mode.equals("edit") ? "Edit Question" : "Ask a Question");
		jteOutput.writeContent(" - Amaaneh</title>\n  <link rel=\"stylesheet\" href=\"/styles.css\">\n</head>\n<body>\n  ");
		gg.jte.generated.ondemand.JteheaderGenerated.render(jteOutput, jteHtmlInterceptor, me);
		jteOutput.writeContent("\n\n  <main class=\"form-container\">\n    <div class=\"form-card\">\n      <h1 class=\"form-title\">\n        ");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(mode.equals("edit") ? "Edit Your Question" : "Ask a Question");
		jteOutput.writeContent("\n      </h1>\n\n      <p style=\"color: #9a8b6f; font-size: 14px; margin-bottom: 20px;\">\n        Your question will be answered by verified scholars in the community.\n      </p>\n\n      <form method=\"post\"");
		var __jte_html_attribute_0 = mode.equals("edit") ? ("/q/" + post.getId() + "/edit") : "/ask";
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
			jteOutput.writeContent(" action=\"");
			jteOutput.setContext("form", "action");
			jteOutput.writeUserContent(__jte_html_attribute_0);
			jteOutput.setContext("form", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\n        <div class=\"form-group\">\n          <label class=\"form-label\" for=\"title\">Title *</label>\n          <input\n            class=\"form-input\"\n            type=\"text\"\n            id=\"title\"\n            name=\"title\"\n           ");
		var __jte_html_attribute_1 = mode.equals("edit") && post != null ? post.getTitle() : "";
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_1)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_1);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("\n            placeholder=\"What's your question?\"\n            required\n          />\n        </div>\n\n        <div class=\"form-group\">\n          <label class=\"form-label\" for=\"body\">Details (optional)</label>\n          <textarea\n            class=\"form-textarea\"\n            id=\"body\"\n            name=\"body\"\n            rows=\"8\"\n            placeholder=\"Provide additional context or details about your question...\"\n          >");
		jteOutput.setContext("textarea", null);
		jteOutput.writeUserContent(mode.equals("edit") && post != null ? post.getBody() : "");
		jteOutput.writeContent("</textarea>\n        </div>\n\n        <button class=\"form-submit\" type=\"submit\">\n          ");
		jteOutput.setContext("button", null);
		jteOutput.writeUserContent(mode.equals("edit") ? "Save Changes" : "Post Question");
		jteOutput.writeContent("\n        </button>\n      </form>\n    </div>\n  </main>\n</body>\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		User me = (User)params.get("me");
		String mode = (String)params.getOrDefault("mode", "create");
		Post post = (Post)params.getOrDefault("post", null);
		render(jteOutput, jteHtmlInterceptor, me, mode, post);
	}
}
