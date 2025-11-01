@param app.User me
@param app.Post post
@import header.jte

@header(me)
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>${post.getTitle()} · Amaanah</title>
  <link rel="stylesheet" href="/styles.css">
</head>
<body class="page">
  <header class="topbar">
    <a class="brand" href="/">Amaanah</a>
    <nav class="nav">
      <a class="link" href="/ask">Ask anonymously</a>
    </nav>
  </header>

  <main class="container">
    <div class="post-wrapper">
      <!-- Back button -->
      <a href="javascript:history.back()" class="back-btn" title="Back to feed">←</a>

      <!-- Post card -->
      <article class="post-card">
        <div class="vote-col">
          <form method="post" action="/q/${post.getId()}/upvote">
            <button class="vote up" title="Upvote">▲</button>
          </form>
          <div class="score">${post.getScore()}</div>
          <form method="post" action="/q/${post.getId()}/downvote">
            <button class="vote down" title="Downvote">▼</button>
          </form>
        </div>

        <div class="post-body">
          <h1 class="post-title">${post.getTitle()}</h1>
          @if (!post.getSnippet().isEmpty())
            <p class="post-text">${post.getSnippet()}</p>
          @endif
          <div class="meta">
            <span>Posted ${post.getCreatedAtDisplay()}</span>
          </div>
        </div>
      </article>

      <!-- Comments -->
      <section class="comments">
        <h2 class="h2">Comments</h2>

        @if (post.getComments().isEmpty())
          <p class="muted">No comments yet — be the first!</p>
        @endif

        <ul class="comment-list">
          @for (app.Post.Comment c : post.getCommentsNewestFirst())
            <li class="comment">
              <div class="comment-main">
                <div class="comment-text">${c.getBody()}</div>
                <div class="comment-meta">Posted ${c.getCreatedAtDisplay()}</div>
              </div>
            </li>
          @endfor
        </ul>

        <form class="comment-form" method="post" action="/q/${post.getId()}/comment">
          <textarea name="body" rows="4" placeholder="Add a comment…" required></textarea>
          <div class="actions">
            <button class="btn" type="submit">Comment</button>
          </div>
        </form>
      </section>
    </div>
  </main>
</body>
</html>