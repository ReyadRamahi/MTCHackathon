@param app.User me

<header class="header">
  <div class="header-content">
    <a class="brand" href="/">Amaaneh</a>

    <nav class="header-nav">
      <a class="header-link" href="/ask">+ Ask Question</a>

      <button
        id="profileBtn"
        class="profile-btn ${me != null ? "is-auth" : ""}"
        type="button"
        aria-haspopup="true"
        aria-expanded="false">
        <span class="status-dot ${me != null ? "online" : "offline"}"></span>
        <span class="profile-name">
          ${me == null || me.isAnon() ? "anon" : me.getDisplayName()}
        </span>
        <span class="badge role">
          ${me == null ? "USER" : me.getRole().name()}
        </span>
        <svg class="chev" viewBox="0 0 20 20" width="16" height="16" aria-hidden="true">
          <path d="M5 7l5 6 5-6" fill="none" stroke="currentColor" stroke-width="2"/>
        </svg>
      </button>

      <div id="profileMenu" class="menu" role="menu" aria-labelledby="profileBtn">
        @if(me != null)
          <a role="menuitem" class="menu-item" href="/verify">Get Verified</a>
          <form role="menuitem" class="menu-item as-form" method="post" action="/logout">
            <button type="submit">Log Out</button>
          </form>
        @else
          <a role="menuitem" class="menu-item" href="/login">Log In</a>
          <a role="menuitem" class="menu-item" href="/signup">Sign Up</a>
        @endif
        @if(me != null && me.getRole() == app.Role.ADMIN)
          <a class="menu-item" href="/admin/verify">Review Verifications</a>
        @endif
      </div>
    </nav>
  </div>
</header>

<script>
  (function () {
    const btn  = document.getElementById('profileBtn');
    const menu = document.getElementById('profileMenu');
    const open = () => { menu.classList.add('open');  btn.setAttribute('aria-expanded','true');  btn.classList.add('open'); };
    const shut = () => { menu.classList.remove('open'); btn.setAttribute('aria-expanded','false'); btn.classList.remove('open'); };

    btn.addEventListener('click', (e) => {
      e.stopPropagation();
      menu.classList.contains('open') ? shut() : open();
    });
    document.addEventListener('click', shut);
    menu.addEventListener('click', (e) => e.stopPropagation());
  })();
</script>