@param app.User me
<nav class="sitebar">
  <div class="sitebar__left">
    <a class="brand" href="/">Amaanah</a>
  </div>

  <div class="sitebar__right">
    @if (me == null) {
      <a class="link" href="/login">Log in</a>
      <a class="btn btn--primary" href="/signup">Sign up</a>
    } else {
      <span class="me">
        <span class="me__name">${me.getDisplayName()}</span>
        <span class="me__role me__role--${me.getRole().name().toLowerCase()}">
          ${me.getRole().name()}
        </span>
      </span>

      @if (me.getRole() == app.Role.USER) {
        <a class="link" href="/verify">Request verification</a>
      }

      @if (me.getRole() == app.Role.ADMIN) {
        <a class="link" href="/admin/verify">Verify queue</a>
      }

      <form method="post" action="/logout" style="display:inline">
        <button class="btn btn--ghost" type="submit">Log out</button>
      </form>
    }
  </div>
</nav>