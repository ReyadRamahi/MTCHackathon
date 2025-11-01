<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Sign Up - Amaanah</title>
  <link rel="stylesheet" href="/styles.css">
</head>
<body>
  @template.header(me = null)

  <main class="form-container">
    <div class="form-card">
      <h1 class="form-title">Create Account</h1>

      <form method="post" action="/signup">
        <div class="form-group">
          <label class="form-label" for="email">Email *</label>
          <input
            class="form-input"
            type="email"
            id="email"
            name="email"
            required
          />
        </div>

        <div class="form-group">
          <label class="form-label" for="display">Display Name</label>
          <input
            class="form-input"
            type="text"
            id="display"
            name="display"
            placeholder="Optional"
          />
        </div>

        <div class="form-group">
          <label class="form-label" for="password">Password *</label>
          <input
            class="form-input"
            type="password"
            id="password"
            name="password"
            required
          />
        </div>

        <button class="form-submit" type="submit">Sign Up</button>
      </form>

      <div class="form-footer">
        Already have an account? <a href="/login">Log in</a>
      </div>
    </div>
  </main>
</body>
</html>