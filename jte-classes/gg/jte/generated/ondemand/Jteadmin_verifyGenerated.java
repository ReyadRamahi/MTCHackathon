package gg.jte.generated.ondemand;
import java.util.List;
import java.util.Map;
public final class Jteadmin_verifyGenerated {
	public static final String JTE_NAME = "admin_verify.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,2,2,14,14,14,22,22,27,27,28,28,30,32,32,35,35,35,35,39,39,42,42,42,42,45,45,48,52,52,52,58,58,58,64,64,64,69,69,69,72,72,75,75,75,77,77,79,79,83,83,83,86,86,89,89,89,89,93,93,93,93,98,98,98,98,105,105,108,108,111,111,111,2,3,3,3,3};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, app.User me, List<Map<String,Object>> reqs) {
		jteOutput.writeContent("\n<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\">\n  <title>Verification Queue – Amaanah</title>\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n  <link rel=\"stylesheet\" href=\"/styles.css\">\n</head>\n<body>\n  ");
		gg.jte.generated.ondemand.JteheaderGenerated.render(jteOutput, jteHtmlInterceptor, me);
		jteOutput.writeContent("\n\n  <main class=\"container\" style=\"max-width:1100px;\">\n    <div style=\"margin-bottom:24px;\">\n      <h1 class=\"page-title\" style=\"font-size:24px;margin-bottom:8px;\">Verification Queue</h1>\n      <p style=\"color:#9a8b6f;font-size:14px;\">Review pending scholar verification requests</p>\n    </div>\n\n    ");
		if (reqs == null || reqs.isEmpty()) {
			jteOutput.writeContent("\n      <div class=\"card\" style=\"padding:24px;text-align:center;\">\n        <h2 style=\"margin:0 0 6px;\">✅ All caught up!</h2>\n        <p class=\"muted\">No pending verification requests at the moment.</p>\n      </div>\n    ");
		} else {
			jteOutput.writeContent("\n      ");
			for (Map<String,Object> req : reqs) {
				jteOutput.writeContent("\n        <section class=\"card\" style=\"display:grid;grid-template-columns:1.3fr 1fr;gap:16px;padding:16px;margin-bottom:16px;\">\n          ");
				jteOutput.writeContent("\n          <div style=\"background:#f9f6f0;border:1px dashed #e2d8c6;border-radius:10px;display:flex;align-items:center;justify-content:center;min-height:520px;\">\n            ");
				if (req.get("file_name") != null && ((String)req.get("file_name")).toLowerCase().endsWith(".pdf")) {
					jteOutput.writeContent("\n              <iframe\n                class=\"doc-frame\"\n                src=\"/uploads/");
					jteOutput.setContext("iframe", "src");
					jteOutput.writeUserContent((String)req.get("file_name"));
					jteOutput.setContext("iframe", null);
					jteOutput.writeContent("#view=FitH\"\n                title=\"Uploaded document\"\n                style=\"width:100%;height:78vh;border:0;border-radius:8px;background:#fff;\">\n              </iframe>\n            ");
				} else {
					jteOutput.writeContent("\n              <img\n                class=\"doc-img\"\n                src=\"/uploads/");
					jteOutput.setContext("img", "src");
					jteOutput.writeUserContent((String)req.get("file_name"));
					jteOutput.setContext("img", null);
					jteOutput.writeContent("\"\n                alt=\"Uploaded document\"\n                style=\"max-width:100%;max-height:78vh;border-radius:8px;\">\n            ");
				}
				jteOutput.writeContent("\n          </div>\n\n          ");
				jteOutput.writeContent("\n          <aside style=\"display:flex;flex-direction:column;gap:10px;\">\n            <div style=\"display:flex;gap:8px;align-items:baseline;\">\n              <strong style=\"color:#3d7a52;min-width:86px;\">Request ID</strong>\n              <span>");
				jteOutput.setContext("span", null);
				jteOutput.writeUserContent(String.valueOf(req.get("id")));
				jteOutput.writeContent("</span>\n            </div>\n\n            <div style=\"display:flex;gap:8px;align-items:baseline;\">\n              <strong style=\"color:#3d7a52;min-width:86px;\">UID</strong>\n              <code style=\"font-size:12px;background:#f3efe7;padding:2px 6px;border-radius:4px;\">\n                ");
				jteOutput.setContext("code", null);
				jteOutput.writeUserContent((String)req.get("uid"));
				jteOutput.writeContent("\n              </code>\n            </div>\n\n            <div style=\"display:flex;gap:8px;align-items:baseline;\">\n              <strong style=\"color:#3d7a52;min-width:86px;\">File</strong>\n              <span>");
				jteOutput.setContext("span", null);
				jteOutput.writeUserContent((String)req.get("file_name"));
				jteOutput.writeContent("</span>\n            </div>\n\n            <div style=\"display:flex;gap:8px;align-items:baseline;\">\n              <strong style=\"color:#3d7a52;min-width:86px;\">Submitted</strong>\n              <span class=\"muted\">");
				jteOutput.setContext("span", null);
				jteOutput.writeUserContent(String.valueOf(req.get("created_at")));
				jteOutput.writeContent("</span>\n            </div>\n\n            ");
				if (req.get("email") != null && !((String)req.get("email")).isBlank()) {
					jteOutput.writeContent("\n              <div style=\"display:flex;gap:8px;align-items:baseline;\">\n                <strong style=\"color:#3d7a52;min-width:86px;\">Email</strong>\n                <span>");
					jteOutput.setContext("span", null);
					jteOutput.writeUserContent((String)req.get("email"));
					jteOutput.writeContent("</span>\n              </div>\n            ");
				}
				jteOutput.writeContent("\n\n            ");
				if (req.get("note") != null && !((String)req.get("note")).isBlank()) {
					jteOutput.writeContent("\n              <div>\n                <strong style=\"color:#3d7a52;\">Notes</strong>\n                <p style=\"margin:8px 0 0;padding:10px;background:#f9f6f0;border-radius:6px;line-height:1.45;\">\n                  ");
					jteOutput.setContext("p", null);
					jteOutput.writeUserContent((String)req.get("note"));
					jteOutput.writeContent("\n                </p>\n              </div>\n            ");
				}
				jteOutput.writeContent("\n\n            <div style=\"margin-top:auto;padding-top:14px;border-top:1px solid #eee8dd;display:flex;gap:10px;justify-content:flex-end;\">\n              <a class=\"btn btn-ghost\" href=\"/uploads/");
				jteOutput.setContext("a", "href");
				jteOutput.writeUserContent((String)req.get("file_name"));
				jteOutput.setContext("a", null);
				jteOutput.writeContent("\" target=\"_blank\" rel=\"noopener\" style=\"text-decoration:none;\">\n                Open in new tab\n              </a>\n\n              <form method=\"post\" action=\"/admin/verify/reject/");
				jteOutput.setContext("form", "action");
				jteOutput.writeUserContent(String.valueOf(req.get("id")));
				jteOutput.setContext("form", null);
				jteOutput.writeContent("\" style=\"margin:0;\">\n                <input type=\"hidden\" name=\"decisionNote\" value=\"Rejected by admin\">\n                <button class=\"btn btn-danger\" type=\"submit\">Reject</button>\n              </form>\n\n              <form method=\"post\" action=\"/admin/verify/approve/");
				jteOutput.setContext("form", "action");
				jteOutput.writeUserContent(String.valueOf(req.get("id")));
				jteOutput.setContext("form", null);
				jteOutput.writeContent("\" style=\"margin:0;\">\n                <input type=\"hidden\" name=\"decisionNote\" value=\"Approved by admin\">\n                <button class=\"btn btn-primary\" type=\"submit\">Approve</button>\n              </form>\n            </div>\n          </aside>\n        </section>\n      ");
			}
			jteOutput.writeContent("\n\n      <p class=\"muted\" style=\"margin-top:8px;\">Tip: Approve/Reject will refresh to the next pending request automatically.</p>\n    ");
		}
		jteOutput.writeContent("\n  </main>\n</body>\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		app.User me = (app.User)params.get("me");
		List<Map<String,Object>> reqs = (List<Map<String,Object>>)params.get("reqs");
		render(jteOutput, jteHtmlInterceptor, me, reqs);
	}
}
