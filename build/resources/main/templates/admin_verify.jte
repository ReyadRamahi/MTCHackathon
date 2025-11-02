@import java.util.List
@import java.util.Map
@param app.User me
@param List<Map<String,Object>> reqs

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Verification Queue – Amaanah</title>
  <link rel="stylesheet" href="/styles.css" />
  <style>
    .admin-wrap { max-width: 1100px; margin: 24px auto 56px; padding: 0 16px; }
    .admin-head { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
    .muted { color:#777; font-size: 13px; }
    .card { background:#fff; border:1px solid #e6e0d6; border-radius:12px; box-shadow:0 2px 10px rgba(0,0,0,.03); }
    .review { display:grid; grid-template-columns: 1.3fr 1fr; gap:16px; padding:16px; }
    .doc { background:#f9f6f0; border:1px dashed #e0d6c6; border-radius:10px; display:flex; align-items:center; justify-content:center; min-height:540px; }
    .doc-frame { width:100%; height:78vh; border:0; border-radius:10px; background:#fff; }
    .doc-img { max-width:100%; max-height:78vh; border-radius:10px; }
    .meta { padding:12px; display:grid; gap:10px; }
    .kv { display:flex; gap:8px; align-items:baseline; }
    .kv .k { font-weight:600; min-width:84px; color:#3d7a52; }
    .controls { display:flex; gap:10px; padding:14px; border-top:1px solid #eee8dd; justify-content:flex-end; }
    .btn { cursor:pointer; border-radius:8px; padding:10px 14px; font-weight:600; border:1px solid transparent; }
    .btn-primary { background:#3d7a52; color:#fff; }
    .btn-ghost { background:#f4efe6; color:#3d7a52; border-color:#d8d1c6; text-decoration:none; }
    .btn-danger { background:#fee9e6; color:#b33a2f; border-color:#f5c6c0; }
    .pill { display:inline-block; padding:2px 8px; border-radius:999px; background:#f4efe6; color:#7a6f5e; font-size:12px; }
    .empty { padding:40px; text-align:center; }
    .header-bar { background:#f3efe7; border-bottom:1px solid #e6e0d6; }
    .header-inner { max-width:1100px; margin:0 auto; padding:12px 16px; display:flex; justify-content:space-between; align-items:center; }
    .brand { font-weight:800; color:#2d623f; text-decoration:none; font-size:20px; }
  </style>
</head>
<body>
  @template.header(me = me)

  <main class="admin-wrap">
    @if (reqs == null || reqs.isEmpty())
      <div class="card empty">
        <h2>No requests in the queue 🎉</h2>
        <p class="muted">When users submit a diploma/transcript, the next request will appear here.</p>
      </div>
    @else
      <div class="admin-head">
        <h1>Verification Queue</h1>
        <span class="muted">Showing ${String.valueOf(reqs.size())} pending</span>
      </div>

      @for (Map<String,Object> req : reqs)
        <section class="card review" style="margin-bottom:16px;">
          <!-- Document Preview -->
          <div class="doc">
            @if (req.get("file_name") != null && ((String)req.get("file_name")).toLowerCase().endsWith(".pdf"))
              <iframe
                class="doc-frame"
                src="/admin/verify/file/${String.valueOf(req.get("id"))}#view=FitH"
                title="Uploaded document">
              </iframe>
            @else
              <img
                class="doc-img"
                src="/admin/verify/file/${String.valueOf(req.get("id"))}"
                alt="Uploaded document" />
            @endif
          </div>

          <!-- Request Details + Actions -->
          <aside class="meta">
            <div class="kv"><div class="k">Request ID</div><span>${String.valueOf(req.get("id"))}</span></div>
            <div class="kv"><div class="k">UID</div><code>${String.valueOf(req.get("uid"))}</code></div>
            <div class="kv"><div class="k">File</div><span>${(String)req.get("file_name")}</span></div>
            <div class="kv"><div class="k">Submitted</div><span class="muted">${String.valueOf(req.get("created_at"))}</span></div>

            @if (req.get("email") != null && !((String)req.get("email")).isBlank())
              <div class="kv"><div class="k">Email</div><span>${(String)req.get("email")}</span></div>
            @endif

            @if (req.get("note") != null && !((String)req.get("note")).isBlank())
              <div class="kv"><div class="k">Notes</div><span>${(String)req.get("note")}</span></div>
            @endif

            <div class="kv"><div class="k">Status</div><span class="pill">pending</span></div>

            <div class="controls">
              <a class="btn btn-ghost" href="/admin/verify/file/${String.valueOf(req.get("id"))}" target="_blank" rel="noopener">Open in new tab</a>

              <form method="post" action="/admin/verify/reject/${String.valueOf(req.get("id"))}">
                <input type="hidden" name="decisionNote" value="Rejected by admin" />
                <button class="btn btn-danger" type="submit">Reject</button>
              </form>

              <form method="post" action="/admin/verify/approve/${String.valueOf(req.get("id"))}">
                <input type="hidden" name="decisionNote" value="Approved by admin" />
                <button class="btn btn-primary" type="submit">Approve</button>
              </form>
            </div>
          </aside>
        </section>
      @endfor

      <p class="muted" style="margin-top:8px;">
        Tip: Approve/Reject will refresh this page and move to the next pending request automatically.
      </p>
    @endif
  </main>
</body>
</html>