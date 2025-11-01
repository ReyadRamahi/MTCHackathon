@template("base.jte")

<h1>Ask anonymously</h1>
<form method="post" action="/ask" style="display:flex;flex-direction:column;gap:10px;max-width:720px">
  <input name="title" placeholder="Title" required
         style="padding:10px;border:1px solid #ddd;border-radius:10px;background:#fff;">
  <textarea name="body" rows="6" placeholder="Details (optional)"
         style="padding:10px;border:1px solid #ddd;border-radius:10px;background:#fff;"></textarea>
  <button type="submit" style="width:max-content;padding:8px 14px;border:1px solid #ddd;border-radius:10px;background:#fff;cursor:pointer">
    Post
  </button>
</form>