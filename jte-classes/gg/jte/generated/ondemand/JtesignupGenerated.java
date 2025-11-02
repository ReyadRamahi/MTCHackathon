package gg.jte.generated.ondemand;
public final class JtesignupGenerated {
	public static final String JTE_NAME = "signup.jte";
	public static final int[] JTE_LINE_INFO = {9,9,9,9,9,9,9,58,58,58,58,58,58};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor) {
		jteOutput.writeContent("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\">\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n  <title>Sign Up - Amaaneh</title>\n  <link rel=\"stylesheet\" href=\"/styles.css\">\n</head>\n<body>\n  ");
		gg.jte.generated.ondemand.JteheaderGenerated.render(jteOutput, jteHtmlInterceptor, null);
		jteOutput.writeContent("\n\n  <main class=\"form-container\">\n    <div class=\"form-card\">\n      <h1 class=\"form-title\">Create Account</h1>\n\n      <form method=\"post\" action=\"/signup\">\n        <div class=\"form-group\">\n          <label class=\"form-label\" for=\"email\">Email *</label>\n          <input\n            class=\"form-input\"\n            type=\"email\"\n            id=\"email\"\n            name=\"email\"\n            required\n          />\n        </div>\n\n        <div class=\"form-group\">\n          <label class=\"form-label\" for=\"display\">Display Name</label>\n          <input\n            class=\"form-input\"\n            type=\"text\"\n            id=\"display\"\n            name=\"display\"\n            placeholder=\"Optional\"\n          />\n        </div>\n\n        <div class=\"form-group\">\n          <label class=\"form-label\" for=\"password\">Password *</label>\n          <input\n            class=\"form-input\"\n            type=\"password\"\n            id=\"password\"\n            name=\"password\"\n            required\n          />\n        </div>\n\n        <button class=\"form-submit\" type=\"submit\">Sign Up</button>\n      </form>\n\n      <div class=\"form-footer\">\n        Already have an account? <a href=\"/login\">Log in</a>\n      </div>\n    </div>\n  </main>\n</body>\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		render(jteOutput, jteHtmlInterceptor);
	}
}
