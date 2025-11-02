package gg.jte.generated.ondemand;
import java.util.List;
import java.util.Map;
public final class Jteadmin_verifyGenerated {
	public static final String JTE_NAME = "admin_verify.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,2,2,36,36,36,39,39,44,44,47,47,47,50,50,52,54,54,57,57,57,57,60,60,63,63,63,63,65,65,68,70,70,70,71,71,71,72,72,72,73,73,73,75,75,76,76,76,77,77,79,79,80,80,80,81,81,86,86,86,86,88,88,88,88,93,93,93,93,100,100,105,105,108,108,108,2,3,3,3,3};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, app.User me, List<Map<String,Object>> reqs) {
		jteOutput.writeContent("\n<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\" />\n  <title>Verification Queue – Amaanah</title>\n  <link rel=\"stylesheet\" href=\"/styles.css\" />\n  <style>\n    .admin-wrap { max-width: 1100px; margin: 24px auto 56px; padding: 0 16px; }\n    .admin-head { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }\n    .muted { color:#777; font-size: 13px; }\n    .card { background:#fff; border:1px solid #e6e0d6; border-radius:12px; box-shadow:0 2px 10px rgba(0,0,0,.03); }\n    .review { display:grid; grid-template-columns: 1.3fr 1fr; gap:16px; padding:16px; }\n    .doc { background:#f9f6f0; border:1px dashed #e0d6c6; border-radius:10px; display:flex; align-items:center; justify-content:center; min-height:540px; }\n    .doc-frame { width:100%; height:78vh; border:0; border-radius:10px; background:#fff; }\n    .doc-img { max-width:100%; max-height:78vh; border-radius:10px; }\n    .meta { padding:12px; display:grid; gap:10px; }\n    .kv { display:flex; gap:8px; align-items:baseline; }\n    .kv .k { font-weight:600; min-width:84px; color:#3d7a52; }\n    .controls { display:flex; gap:10px; padding:14px; border-top:1px solid #eee8dd; justify-content:flex-end; }\n    .btn { cursor:pointer; border-radius:8px; padding:10px 14px; font-weight:600; border:1px solid transparent; }\n    .btn-primary { background:#3d7a52; color:#fff; }\n    .btn-ghost { background:#f4efe6; color:#3d7a52; border-color:#d8d1c6; text-decoration:none; }\n    .btn-danger { background:#fee9e6; color:#b33a2f; border-color:#f5c6c0; }\n    .pill { display:inline-block; padding:2px 8px; border-radius:999px; background:#f4efe6; color:#7a6f5e; font-size:12px; }\n    .empty { padding:40px; text-align:center; }\n    .header-bar { background:#f3efe7; border-bottom:1px solid #e6e0d6; }\n    .header-inner { max-width:1100px; margin:0 auto; padding:12px 16px; display:flex; justify-content:space-between; align-items:center; }\n    .brand { font-weight:800; color:#2d623f; text-decoration:none; font-size:20px; }\n  </style>\n</head>\n<body>\n  ");
		gg.jte.generated.ondemand.JteheaderGenerated.render(jteOutput, jteHtmlInterceptor, me);
		jteOutput.writeContent("\n\n  <main class=\"admin-wrap\">\n    ");
		if (reqs == null || reqs.isEmpty()) {
			jteOutput.writeContent("\n      <div class=\"card empty\">\n        <h2>No requests in the queue 🎉</h2>\n        <p class=\"muted\">When users submit a diploma/transcript, the next request will appear here.</p>\n      </div>\n    ");
		} else {
			jteOutput.writeContent("\n      <div class=\"admin-head\">\n        <h1>Verification Queue</h1>\n        <span class=\"muted\">Showing ");
			jteOutput.setContext("span", null);
			jteOutput.writeUserContent(String.valueOf(reqs.size()));
			jteOutput.writeContent(" pending</span>\n      </div>\n\n      ");
			for (Map<String,Object> req : reqs) {
				jteOutput.writeContent("\n        <section class=\"card review\" style=\"margin-bottom:16px;\">\n          ");
				jteOutput.writeContent("\n          <div class=\"doc\">\n            ");
				if (req.get("file_name") != null && ((String)req.get("file_name")).toLowerCase().endsWith(".pdf")) {
					jteOutput.writeContent("\n              <iframe\n                class=\"doc-frame\"\n                src=\"/admin/verify/file/");
					jteOutput.setContext("iframe", "src");
					jteOutput.writeUserContent(String.valueOf(req.get("id")));
					jteOutput.setContext("iframe", null);
					jteOutput.writeContent("#view=FitH\"\n                title=\"Uploaded document\">\n              </iframe>\n            ");
				} else {
					jteOutput.writeContent("\n              <img\n                class=\"doc-img\"\n                src=\"/admin/verify/file/");
					jteOutput.setContext("img", "src");
					jteOutput.writeUserContent(String.valueOf(req.get("id")));
					jteOutput.setContext("img", null);
					jteOutput.writeContent("\"\n                alt=\"Uploaded document\" />\n            ");
				}
				jteOutput.writeContent("\n          </div>\n\n          ");
				jteOutput.writeContent("\n          <aside class=\"meta\">\n            <div class=\"kv\"><div class=\"k\">Request ID</div><span>");
				jteOutput.setContext("span", null);
				jteOutput.writeUserContent(String.valueOf(req.get("id")));
				jteOutput.writeContent("</span></div>\n            <div class=\"kv\"><div class=\"k\">UID</div><code>");
				jteOutput.setContext("code", null);
				jteOutput.writeUserContent(String.valueOf(req.get("uid")));
				jteOutput.writeContent("</code></div>\n            <div class=\"kv\"><div class=\"k\">File</div><span>");
				jteOutput.setContext("span", null);
				jteOutput.writeUserContent((String)req.get("file_name"));
				jteOutput.writeContent("</span></div>\n            <div class=\"kv\"><div class=\"k\">Submitted</div><span class=\"muted\">");
				jteOutput.setContext("span", null);
				jteOutput.writeUserContent(String.valueOf(req.get("created_at")));
				jteOutput.writeContent("</span></div>\n\n            ");
				if (req.get("email") != null && !((String)req.get("email")).isBlank()) {
					jteOutput.writeContent("\n              <div class=\"kv\"><div class=\"k\">Email</div><span>");
					jteOutput.setContext("span", null);
					jteOutput.writeUserContent((String)req.get("email"));
					jteOutput.writeContent("</span></div>\n            ");
				}
				jteOutput.writeContent("\n\n            ");
				if (req.get("note") != null && !((String)req.get("note")).isBlank()) {
					jteOutput.writeContent("\n              <div class=\"kv\"><div class=\"k\">Notes</div><span>");
					jteOutput.setContext("span", null);
					jteOutput.writeUserContent((String)req.get("note"));
					jteOutput.writeContent("</span></div>\n            ");
				}
				jteOutput.writeContent("\n\n            <div class=\"kv\"><div class=\"k\">Status</div><span class=\"pill\">pending</span></div>\n\n            <div class=\"controls\">\n              <a class=\"btn btn-ghost\" href=\"/admin/verify/file/");
				jteOutput.setContext("a", "href");
				jteOutput.writeUserContent(String.valueOf(req.get("id")));
				jteOutput.setContext("a", null);
				jteOutput.writeContent("\" target=\"_blank\" rel=\"noopener\">Open in new tab</a>\n\n              <form method=\"post\" action=\"/admin/verify/reject/");
				jteOutput.setContext("form", "action");
				jteOutput.writeUserContent(String.valueOf(req.get("id")));
				jteOutput.setContext("form", null);
				jteOutput.writeContent("\">\n                <input type=\"hidden\" name=\"decisionNote\" value=\"Rejected by admin\" />\n                <button class=\"btn btn-danger\" type=\"submit\">Reject</button>\n              </form>\n\n              <form method=\"post\" action=\"/admin/verify/approve/");
				jteOutput.setContext("form", "action");
				jteOutput.writeUserContent(String.valueOf(req.get("id")));
				jteOutput.setContext("form", null);
				jteOutput.writeContent("\">\n                <input type=\"hidden\" name=\"decisionNote\" value=\"Approved by admin\" />\n                <button class=\"btn btn-primary\" type=\"submit\">Approve</button>\n              </form>\n            </div>\n          </aside>\n        </section>\n      ");
			}
			jteOutput.writeContent("\n\n      <p class=\"muted\" style=\"margin-top:8px;\">\n        Tip: Approve/Reject will refresh this page and move to the next pending request automatically.\n      </p>\n    ");
		}
		jteOutput.writeContent("\n  </main>\n</body>\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		app.User me = (app.User)params.get("me");
		List<Map<String,Object>> reqs = (List<Map<String,Object>>)params.get("reqs");
		render(jteOutput, jteHtmlInterceptor, me, reqs);
	}
}
