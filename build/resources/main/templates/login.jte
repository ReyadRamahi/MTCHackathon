<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Log In - Amaanah</title>
  <link rel="stylesheet" href="/styles.css">
</head>
<body>
  @template.header(me = null)

  <main class="form-container">
    <div class="form-card">
      <h1 class="form-title">Log In</h1>

      <form method="post" action="/login">
        <div class="form-group">
          <label class="form-label" for="email">Email</label>
          <input
            class="form-input"
            type="email"
            id="email"
            name="email"
            required
          />
        </div>

        <div class="form-group">
          <label class="form-label" for="password">Password</label>
          <input
            class="form-input"
            type="password"
            id="password"
            name="password"
            required
          />
        </div>

        <button class="form-submit" type="submit">Log In</button>
      </form>

      <div class="form-footer">
        New to Amaanah? <a href="/signup">Sign up</a>
      </div>
    </div>
  </main>
</body>
</html>