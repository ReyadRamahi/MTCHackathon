@import java.util.List
@import java.util.Map
@param app.User me
@param List<Map<String,Object>> reqs

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Verification Queue – Amaanah</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="/styles.css">
</head>
<body>
  @template.header(me)

  <main class="container" style="max-width:1100px;">
    <div style="margin-bottom:24px;">
      <h1 class="page-title" style="font-size:24px;margin-bottom:8px;">Verification Queue</h1>
      <p style="color:#9a8b6f;font-size:14px;">Review pending scholar verification requests</p>
    </div>

    @if (reqs == null || reqs.isEmpty())
      <div class="card" style="padding:24px;text-align:center;">
        <h2 style="margin:0 0 6px;">✅ All caught up!</h2>
        <p class="muted">No pending verification requests at the moment.</p>
      </div>
    @else
      @for (Map<String,Object> req : reqs)
        <section class="card" style="display:grid;grid-template-columns:1.3fr 1fr;gap:16px;padding:16px;margin-bottom:16px;">
          <!-- Document preview -->
          <div style="background:#f9f6f0;border:1px dashed #e2d8c6;border-radius:10px;display:flex;align-items:center;justify-content:center;min-height:520px;">
            @if (req.get("file_name") != null && ((String)req.get("file_name")).toLowerCase().endsWith(".pdf"))
              <iframe
                class="doc-frame"
                src="/uploads/${(String)req.get("file_name")}#view=FitH"
                title="Uploaded document"
                style="width:100%;height:78vh;border:0;border-radius:8px;background:#fff;">
              </iframe>
            @else
              <img
                class="doc-img"
                src="/uploads/${(String)req.get("file_name")}"
                alt="Uploaded document"
                style="max-width:100%;max-height:78vh;border-radius:8px;">
            @endif
          </div>

          <!-- Meta + actions -->
          <aside style="display:flex;flex-direction:column;gap:10px;">
            <div style="display:flex;gap:8px;align-items:baseline;">
              <strong style="color:#3d7a52;min-width:86px;">Request ID</strong>
              <span>${String.valueOf(req.get("id"))}</span>
            </div>

            <div style="display:flex;gap:8px;align-items:baseline;">
              <strong style="color:#3d7a52;min-width:86px;">UID</strong>
              <code style="font-size:12px;background:#f3efe7;padding:2px 6px;border-radius:4px;">
                ${(String)req.get("uid")}
              </code>
            </div>

            <div style="display:flex;gap:8px;align-items:baseline;">
              <strong style="color:#3d7a52;min-width:86px;">File</strong>
              <span>${(String)req.get("file_name")}</span>
            </div>

            <div style="display:flex;gap:8px;align-items:baseline;">
              <strong style="color:#3d7a52;min-width:86px;">Submitted</strong>
              <span class="muted">${String.valueOf(req.get("created_at"))}</span>
            </div>

            @if (req.get("email") != null && !((String)req.get("email")).isBlank())
              <div style="display:flex;gap:8px;align-items:baseline;">
                <strong style="color:#3d7a52;min-width:86px;">Email</strong>
                <span>${(String)req.get("email")}</span>
              </div>
            @endif

            @if (req.get("note") != null && !((String)req.get("note")).isBlank())
              <div>
                <strong style="color:#3d7a52;">Notes</strong>
                <p style="margin:8px 0 0;padding:10px;background:#f9f6f0;border-radius:6px;line-height:1.45;">
                  ${(String)req.get("note")}
                </p>
              </div>
            @endif

            <div style="margin-top:auto;padding-top:14px;border-top:1px solid #eee8dd;display:flex;gap:10px;justify-content:flex-end;">
              <a class="btn btn-ghost" href="/uploads/${(String)req.get("file_name")}" target="_blank" rel="noopener" style="text-decoration:none;">
                Open in new tab
              </a>

              <form method="post" action="/admin/verify/reject/${String.valueOf(req.get("id"))}" style="margin:0;">
                <input type="hidden" name="decisionNote" value="Rejected by admin">
                <button class="btn btn-danger" type="submit">Reject</button>
              </form>

              <form method="post" action="/admin/verify/approve/${String.valueOf(req.get("id"))}" style="margin:0;">
                <input type="hidden" name="decisionNote" value="Approved by admin">
                <button class="btn btn-primary" type="submit">Approve</button>
              </form>
            </div>
          </aside>
        </section>
      @endfor

      <p class="muted" style="margin-top:8px;">Tip: Approve/Reject will refresh to the next pending request automatically.</p>
    @endif
  </main>
</body>
</html>