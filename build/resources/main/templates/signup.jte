@import app.User
@template("base.jte")
<main class="container">
  <h1>Create account</h1>
  <form method="post" action="/signup" class="card">
    <label>Email <input type="email" name="email" required></label>
    <label>Display name (optional) <input type="text" name="display"></label>
    <label>Password <input type="password" name="password" required></label>
    <button type="submit">Sign up</button>
  </form>
  <p>Already have an account? <a href="/login">Log in</a></p>
</main>