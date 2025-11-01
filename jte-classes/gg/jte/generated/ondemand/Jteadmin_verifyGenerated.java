package gg.jte.generated.ondemand;
public final class Jteadmin_verifyGenerated {
	public static final String JTE_NAME = "admin_verify.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,22,22,22,22,27,27,32,32,35,35,35,40,40,41,41,41,41,42,42,43,43,43,43,43,43,43,43,43,44,44,48,48,48,49,49,49,50,50,50,51,51,52,52,52,53,53,54,54,55,55,55,56,56,60,60,60,60,60,60,60,60,60,61,61,61,61,64,64,64,64,72,72,75,75,75,0,1,2,3,4,5,6,7,8,8,8,8};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, app.User me, java.util.List<java.util.Map> reqs, String uid, String email, String note, String file, String when, String url, boolean isPdf) {
		jteOutput.writeContent("\n<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\">\n  <title>Verification Queue – Amaanah</title>\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n  <link href=\"/styles.css\" rel=\"stylesheet\">\n</head>\n<body class=\"admin-body\">\n  <header class=\"admin-header\">\n    <div class=\"admin-header__inner\">\n      <a class=\"brand\" href=\"/\">Amaanah</a>\n      <div class=\"admin-header__who\">Admin · ");
		jteOutput.setContext("div", null);
		jteOutput.writeUserContent(me.getDisplayName());
		jteOutput.writeContent("</div>\n    </div>\n  </header>\n\n  <main class=\"admin-wrap\">\n    ");
		if (reqs == null || reqs.isEmpty()) {
			jteOutput.writeContent("\n      <section class=\"card card--center\">\n        <h2 class=\"title\">No requests in the queue 🎉</h2>\n        <p class=\"muted\">New submissions will appear here automatically.</p>\n      </section>\n    ");
		} else {
			jteOutput.writeContent("\n      <div class=\"admin-head\">\n        <h1 class=\"title\">Verification Queue</h1>\n        <span class=\"muted\">Showing 1 of ");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(reqs.size());
			jteOutput.writeContent(" pending</span>\n      </div>\n\n      <section class=\"card review\">\n        <div class=\"doc\">\n          ");
			if (isPdf) {
				jteOutput.writeContent("\n            <iframe class=\"doc-frame\" src=\"");
				jteOutput.setContext("iframe", "src");
				jteOutput.writeUserContent(url);
				jteOutput.setContext("iframe", null);
				jteOutput.writeContent("#view=FitH\" title=\"Uploaded document\"></iframe>\n          ");
			} else {
				jteOutput.writeContent("\n            <img class=\"doc-img\"");
				var __jte_html_attribute_0 = url;
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
					jteOutput.writeContent(" src=\"");
					jteOutput.setContext("img", "src");
					jteOutput.writeUserContent(__jte_html_attribute_0);
					jteOutput.setContext("img", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent(" alt=\"Uploaded document\">\n          ");
			}
			jteOutput.writeContent("\n        </div>\n\n        <aside class=\"meta\">\n          <div class=\"kv\"><div class=\"k\">UID</div><code class=\"v v--mono\">");
			jteOutput.setContext("code", null);
			jteOutput.writeUserContent(uid);
			jteOutput.writeContent("</code></div>\n          <div class=\"kv\"><div class=\"k\">File</div><span class=\"v\">");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(file);
			jteOutput.writeContent("</span></div>\n          <div class=\"kv\"><div class=\"k\">Submitted</div><span class=\"v muted\">");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(when);
			jteOutput.writeContent("</span></div>\n          ");
			if (email != null && !email.isBlank()) {
				jteOutput.writeContent("\n            <div class=\"kv\"><div class=\"k\">Email</div><span class=\"v\">");
				jteOutput.setContext("span", null);
				jteOutput.writeUserContent(email);
				jteOutput.writeContent("</span></div>\n          ");
			}
			jteOutput.writeContent("\n          ");
			if (note != null && !note.isBlank()) {
				jteOutput.writeContent("\n            <div class=\"kv\"><div class=\"k\">Notes</div><span class=\"v\">");
				jteOutput.setContext("span", null);
				jteOutput.writeUserContent(note);
				jteOutput.writeContent("</span></div>\n          ");
			}
			jteOutput.writeContent("\n          <div class=\"kv\"><div class=\"k\">Status</div><span class=\"pill\">pending</span></div>\n\n          <div class=\"controls\">\n            <a class=\"btn btn-ghost\"");
			var __jte_html_attribute_1 = url;
			if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_1)) {
				jteOutput.writeContent(" href=\"");
				jteOutput.setContext("a", "href");
				jteOutput.writeUserContent(__jte_html_attribute_1);
				jteOutput.setContext("a", null);
				jteOutput.writeContent("\"");
			}
			jteOutput.writeContent(" target=\"_blank\" rel=\"noopener\">Open in new tab</a>\n            <form method=\"post\" action=\"/admin/verify/reject/");
			jteOutput.setContext("form", "action");
			jteOutput.writeUserContent(uid);
			jteOutput.setContext("form", null);
			jteOutput.writeContent("\">\n              <button class=\"btn btn-danger\" type=\"submit\">Reject</button>\n            </form>\n            <form method=\"post\" action=\"/admin/verify/approve/");
			jteOutput.setContext("form", "action");
			jteOutput.writeUserContent(uid);
			jteOutput.setContext("form", null);
			jteOutput.writeContent("\">\n              <button class=\"btn btn-primary\" type=\"submit\">Approve</button>\n            </form>\n          </div>\n        </aside>\n      </section>\n\n      <p class=\"muted tip\">Tip: Approve/Reject refreshes this page and advances to the next request.</p>\n    ");
		}
		jteOutput.writeContent("\n  </main>\n</body>\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		app.User me = (app.User)params.get("me");
		java.util.List<java.util.Map> reqs = (java.util.List<java.util.Map>)params.get("reqs");
		String uid = (String)params.get("uid");
		String email = (String)params.get("email");
		String note = (String)params.get("note");
		String file = (String)params.get("file");
		String when = (String)params.get("when");
		String url = (String)params.get("url");
		boolean isPdf = (boolean)params.get("isPdf");
		render(jteOutput, jteHtmlInterceptor, me, reqs, uid, email, note, file, when, url, isPdf);
	}
}
