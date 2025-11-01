package gg.jte.generated.ondemand;
public final class JteaskGenerated {
	public static final String JTE_NAME = "ask.jte";
	public static final int[] JTE_LINE_INFO = {11,11,11,11,11,11,11,11,11,11,11};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor) {
		jteOutput.writeContent("@template(\"base.jte\")\n\n<h1>Ask anonymously</h1>\n<form method=\"post\" action=\"/ask\" style=\"display:flex;flex-direction:column;gap:10px;max-width:720px\">\n  <input name=\"title\" placeholder=\"Title\" required\n         style=\"padding:10px;border:1px solid #ddd;border-radius:10px;background:#fff;\">\n  <textarea name=\"body\" rows=\"6\" placeholder=\"Details (optional)\"\n         style=\"padding:10px;border:1px solid #ddd;border-radius:10px;background:#fff;\"></textarea>\n  <button type=\"submit\" style=\"width:max-content;padding:8px 14px;border:1px solid #ddd;border-radius:10px;background:#fff;cursor:pointer\">\n    Post\n  </button>\n</form>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		render(jteOutput, jteHtmlInterceptor);
	}
}
