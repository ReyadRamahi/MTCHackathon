@param app.User me

<header class="header">
  <div class="header-content">
    <a class="brand" href="/">Amaanah</a>

    <nav class="header-nav">
      <a class="header-link" href="/ask">+ Ask Question</a>

      @if (me == null)
        <a class="btn btn-ghost" href="/login">Log In</a>
        <a class="btn btn-primary" href="/signup">Sign Up</a>
      @else
        <div class="user-info">
          <span class="user-name">${me.getDisplayName()}</span>
          <span class="role-badge role-${me.getRole().name().toLowerCase()}">
            ${me.getRole().name()}
          </span>
        </div>

        @if (me.getRole() == app.Role.USER)
          <a class="header-link" href="/verify">Get Verified</a>
        @endif

        @if (me.getRole() == app.Role.ADMIN)
          <a class="header-link" href="/admin/verify">Verify Queue</a>
        @endif

        <form method="post" action="/logout" style="display:inline;">
          <button class="btn btn-ghost" type="submit">Log Out</button>
        </form>
      @endif
    </nav>
  </div>
</header>