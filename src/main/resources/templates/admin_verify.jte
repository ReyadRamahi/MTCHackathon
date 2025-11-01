@import java.util.List
@import app.Db.VerificationRequest
@import app.User
@param List<VerificationRequest> pending
@param User me

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Verification Queue - Amaanah Admin</title>
  <link rel="stylesheet" href="/styles.css">
</head>
<body>
  @template.header(me = me)

  <main class="container">
    <h1 class="page-title">Pending Verification Requests</h1>

    @if(pending.isEmpty())
      <div class="empty-state">
        <p>No pending verification requests.</p>
      </div>
    @else
      <ul class="admin-list">
        @for(var r : pending)
          <li class="admin-card">
            <div class="admin-card-header">
              <strong>User ID:</strong> ${r.uid()} •
              <strong>Submitted:</strong> ${r.createdAt()}
            </div>

            <div class="admin-card-body">
              ${r.note()}
            </div>

            <div class="admin-card-actions">
              <form method="post" action="/admin/verify/${r.id()}/approve" style="display:inline;">
                <button class="btn btn-success" type="submit">✓ Approve</button>
              </form>

              <form method="post" action="/admin/verify/${r.id()}/reject" style="display:inline;">
                <button class="btn btn-danger" type="submit">✗ Reject</button>
              </form>
            </div>
          </li>
        @endfor
      </ul>
    @endif
  </main>
</body>
</html>