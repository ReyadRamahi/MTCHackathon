package gg.jte.generated.ondemand;
public final class JteaskGenerated {
	public static final String JTE_NAME = "ask.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,0,0,11,11,11,13,13,13,13,13,13,13};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor) {
		var content = new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\n<h1>Ask anonymously</h1>\n<form method=\"post\" action=\"/ask\" style=\"display:flex;flex-direction:column;gap:10px;max-width:720px\">\n  <input name=\"title\" placeholder=\"Title\" required\n         style=\"padding:10px;border:1px solid #ddd;border-radius:10px;background:#fff;\">\n  <textarea name=\"body\" rows=\"6\" placeholder=\"Details (optional)\"\n         style=\"padding:10px;border:1px solid #ddd;border-radius:10px;background:#fff;\"></textarea>\n  <button type=\"submit\" style=\"width:max-content;padding:8px 14px;border:1px solid #ddd;border-radius:10px;background:#fff;cursor:pointer\">\n    Post\n  </button>\n</form>\n");
			}
		};
		jteOutput.writeContent("\n\n");
		gg.jte.generated.ondemand.JtebaseGenerated.render(jteOutput, jteHtmlInterceptor, content);
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		render(jteOutput, jteHtmlInterceptor);
	}
}
