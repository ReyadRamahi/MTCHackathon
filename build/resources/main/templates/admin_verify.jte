@param app.User me
@param java.util.List<java.util.Map<String,Object>> reqs

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Verification Queue – Amaanah</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="/styles.css" rel="stylesheet">
</head>
<body>
  <header class="header-bar">
    <div class="header-inner">
      <a class="brand" href="/">Amaanah</a>
      <div class="muted">Admin · ${me.getDisplayName()}</div>
    </div>
  </header>

  <main class="admin-wrap">
    @if (reqs == null || reqs.isEmpty())
      <div class="card queue-empty">
        <h2>No requests in the queue 🎉</h2>
        <p class="muted">When users submit a diploma/transcript, new requests will appear here.</p>
      </div>
    @else
      @{
        java.util.Map<String,Object> row = reqs.get(0);
        int    requestId = ((Number) row.get("id")).intValue();
        String uid       = (String) row.get("uid");
        String email     = (String) row.get("email");
        String note      = (String) row.get("note");
        String file      = (String) row.get("file_name");
        String when      = String.valueOf(row.get("created_at"));
        String url       = "/uploads/" + file;
        boolean isPdf    = (file != null && file.toLowerCase().endsWith(".pdf"));
      @}

      <div class="admin-head">
        <h1>Verification Queue</h1>
        <span class="muted">Showing 1 of ${reqs.size()} pending</span>
      </div>

      <section class="card review">
        <div class="doc">
          @if (isPdf)
            <iframe class="doc-frame" src="${url}#view=FitH" title="Uploaded Document"></iframe>
          @else
            <img class="doc-img" src="${url}" alt="Uploaded Document">
          @endif
        </div>

        <aside class="meta">
          <div class="kv"><div class="k">UID</div><code>${uid}</code></div>
          <div class="kv"><div class="k">File</div><span>${file}</span></div>
          <div class="kv"><div class="k">Submitted</div><span class="muted">${when}</span></div>
          @if (email != null && !email.isBlank())
            <div class="kv"><div class="k">Email</div><span>${email}</span></div>
          @endif
          @if (note != null && !note.isBlank())
            <div class="kv"><div class="k">Notes</div><span>${note}</span></div>
          @endif
          <div class="kv"><div class="k">Status</div><span class="pill">pending</span></div>

          <div class="controls">
            <a class="btn btn-ghost" href="${url}" target="_blank" rel="noopener">Open in new tab</a>
            <form method="post" action="/admin/verify/reject/${requestId}">
              <button class="btn btn-danger" type="submit">Reject</button>
            </form>
            <form method="post" action="/admin/verify/approve/${requestId}">
              <button class="btn btn-primary" type="submit">Approve</button>
            </form>
          </div>
        </aside>
      </section>

      <div class="muted" style="margin-top:8px;">
        Tip: Approve/Reject refreshes automatically to show the next pending request.
      </div>
    @endif
  </main>
</body>
</html>