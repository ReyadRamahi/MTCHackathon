@param app.User me
@param java.util.List<java.util.Map> reqs
@param String uid
@param String email
@param String note
@param String file
@param String when
@param String url
@param boolean isPdf

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Verification Queue – Amaanah</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="/styles.css" rel="stylesheet">
</head>
<body class="admin-body">
  <header class="admin-header">
    <div class="admin-header__inner">
      <a class="brand" href="/">Amaanah</a>
      <div class="admin-header__who">Admin · ${me.getDisplayName()}</div>
    </div>
  </header>

  <main class="admin-wrap">
    @if(reqs == null || reqs.isEmpty())
      <section class="card card--center">
        <h2 class="title">No requests in the queue 🎉</h2>
        <p class="muted">New submissions will appear here automatically.</p>
      </section>
    @else
      <div class="admin-head">
        <h1 class="title">Verification Queue</h1>
        <span class="muted">Showing 1 of ${reqs.size()} pending</span>
      </div>

      <section class="card review">
        <div class="doc">
          @if(isPdf)
            <iframe class="doc-frame" src="${url}#view=FitH" title="Uploaded document"></iframe>
          @else
            <img class="doc-img" src="${url}" alt="Uploaded document">
          @endif
        </div>

        <aside class="meta">
          <div class="kv"><div class="k">UID</div><code class="v v--mono">${uid}</code></div>
          <div class="kv"><div class="k">File</div><span class="v">${file}</span></div>
          <div class="kv"><div class="k">Submitted</div><span class="v muted">${when}</span></div>
          @if(email != null && !email.isBlank())
            <div class="kv"><div class="k">Email</div><span class="v">${email}</span></div>
          @endif
          @if(note != null && !note.isBlank())
            <div class="kv"><div class="k">Notes</div><span class="v">${note}</span></div>
          @endif
          <div class="kv"><div class="k">Status</div><span class="pill">pending</span></div>

          <div class="controls">
            <a class="btn btn-ghost" href="${url}" target="_blank" rel="noopener">Open in new tab</a>
            <form method="post" action="/admin/verify/reject/${uid}">
              <button class="btn btn-danger" type="submit">Reject</button>
            </form>
            <form method="post" action="/admin/verify/approve/${uid}">
              <button class="btn btn-primary" type="submit">Approve</button>
            </form>
          </div>
        </aside>
      </section>

      <p class="muted tip">Tip: Approve/Reject refreshes this page and advances to the next request.</p>
    @endif
  </main>
</body>
</html>