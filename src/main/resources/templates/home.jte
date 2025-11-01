@param app.User me
@param app.HomeView vm
@import header.jte

@header(me)
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Amaanah • Newest</title>
  <link rel="stylesheet" href="/styles.css">
</head>
<body class="site">
<header class="site-header">
  <a class="home-link" href="/">Amaanah</a> ·
  <a class="ask-link" href="/ask">Ask anonymously</a>
</header>

<main class="feed">
  <h1 class="page-title">Newest questions</h1>

  @if(!vm.hasPosts())
    <p class="empty">No questions yet.</p>
  @endif

  <ul class="post-list">
    @for(app.Post p : vm.getPostsNewestFirst())
      <li class="post-row">
        <div class="vote-col">
          <form method="post" action="/q/${p.getId()}/upvote">
            <button class="vote up" title="Upvote" aria-label="Upvote">▲</button>
          </form>

          <div class="score">${p.getScore()}</div>

          <form method="post" action="/q/${p.getId()}/downvote">
            <button class="vote down" title="Downvote" aria-label="Downvote">▼</button>
          </form>
        </div>

        <div class="content-col">
          <a class="title" href="/q/${p.getId()}">${p.getTitle()}</a>

          @if(!p.getSnippet().isEmpty())
            <p class="snippet">${p.getSnippet()}</p>
          @endif

          <div class="meta">
            Posted ${p.getCreatedAtDisplay()}
          </div>
        </div>
      </li>
    @endfor
  </ul>
</main>
</body>
</html>