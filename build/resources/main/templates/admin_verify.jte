@import java.util.List
@param List<app.Db.VerificationRequest> pending
@template("base.jte")
<main class="container">
  <h1>Pending verification</h1>
  @if(pending.isEmpty())
    <p>No pending requests.</p>
  @else
    <ul>
      @for(var r : pending)
        <li class="card">
          <div><b>uid:</b> ${r.uid()} • <b>created:</b> ${r.createdAt()}</div>
          <p>${r.note()}</p>
          <form method="post" action="/admin/verify/${r.id()}/approve" style="display:inline"><button>Approve</button></form>
          <form method="post" action="/admin/verify/${r.id()}/reject"  style="display:inline"><button>Reject</button></form>
        </li>
      @endfor
    </ul>
  @endif
</main>