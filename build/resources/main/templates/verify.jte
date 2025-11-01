@param app.User me
@template("base.jte")
<main class="container">
  <h1>Verification</h1>
  <p>Your status: <strong>${me.getRole().name()}</strong></p>
  @if(me.getRole() == app.Role.SCHOLAR)
    <p>You're already verified. 🎉</p>
  @elseif(me.getRole() == app.Role.ADMIN)
    <p>You're an admin.</p>
  @else
    <form method="post" action="/verify/request" class="card">
      <label>Why should you be verified?
        <textarea name="note" rows="4" placeholder="Links, evidence, context…"></textarea>
      </label>
      <button type="submit">Request verification</button>
    </form>
  @endif
</main>