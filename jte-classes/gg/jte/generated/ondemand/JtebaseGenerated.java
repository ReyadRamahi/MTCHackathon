package gg.jte.generated.ondemand;
public final class JtebaseGenerated {
	public static final String JTE_NAME = "base.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,17,17,17,17,20,20,20,0,0,0,0};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, gg.jte.Content content) {
		jteOutput.writeContent("\n<!doctype html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\">\n  <title>Amana</title>\n  <link rel=\"stylesheet\" href=\"/styles.css\">\n</head>\n<body>\n  <header class=\"topbar\">\n    <a class=\"brand\" href=\"/\">Amana</a>\n    <nav class=\"nav\">\n      <a href=\"/ask\">Ask</a>\n    </nav>\n  </header>\n  <main class=\"container\">\n    ");
		jteOutput.setContext("main", null);
		jteOutput.writeUserContent(content);
		jteOutput.writeContent("\n  </main>\n</body>\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		gg.jte.Content content = (gg.jte.Content)params.get("content");
		render(jteOutput, jteHtmlInterceptor, content);
	}
}
