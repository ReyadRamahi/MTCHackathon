@template("base.jte")
<main class="container">
  <h1>Log in</h1>
  <form method="post" action="/login" class="card">
    <label>Email <input type="email" name="email" required></label>
    <label>Password <input type="password" name="password" required></label>
    <button type="submit">Log in</button>
  </form>
</main>