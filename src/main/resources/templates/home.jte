@param app.User me
@param app.HomeView vm

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Amaaneh - Ask Questions, Get Scholar Answers</title>
  <link rel="stylesheet" href="/styles.css">
</head>
<body>
  @template.header(me = me)

  <main class="container">
    <h1 class="page-title">Recent Questions</h1>

    @if(!vm.hasPosts())
      <div class="empty-state">
        <p>No questions yet. Be the first to ask!</p>
      </div>
    @else
      <ul class="post-list">
        @for(app.Post p : vm.getPostsNewestFirst())
          <li class="post-item">
            <div class="vote-box">
              <form method="post" action="/q/${p.getId()}/upvote">
                <button class="vote-btn up" type="submit" title="Upvote">▲</button>
              </form>

              <span class="vote-score">${p.getScore()}</span>

              <form method="post" action="/q/${p.getId()}/downvote">
                <button class="vote-btn down" type="submit" title="Downvote">▼</button>
              </form>
            </div>

            <div class="post-content">
              <a class="post-title" href="/q/${p.getId()}">${p.getTitle()}</a>

              @if(!p.getSnippet().isEmpty())
                <p class="post-snippet">${p.getSnippet()}</p>
              @endif

              <div class="post-meta">
                <span class="author-badge">Anonymous</span>
                • Posted ${p.getCreatedAtDisplay()}
                @if(!p.getComments().isEmpty())
                  • ${p.getComments().size()} comment${p.getComments().size() == 1 ? "" : "s"}
                @endif
              </div>
            </div>
          </li>
        @endfor
      </ul>
    @endif
  </main>
</body>
</html>