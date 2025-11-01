@param app.User me
@param app.Post post

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${post.getTitle()} - Amaaneh</title>
  <link rel="stylesheet" href="/styles.css">
</head>
<body>
  @template.header(me = me)

  <main class="container">
    <div class="post-wrapper">
      <article class="post-card">
        <div class="vote-box">
          <form method="post" action="/q/${post.getId()}/upvote">
            <button class="vote-btn up" type="submit" title="Upvote">▲</button>
          </form>

          <span class="vote-score">${post.getScore()}</span>

          <form method="post" action="/q/${post.getId()}/downvote">
            <button class="vote-btn down" type="submit" title="Downvote">▼</button>
          </form>
        </div>

        <div class="post-body">
          <h1 class="post-heading">${post.getTitle()}</h1>

          @if(!post.getBody().isEmpty())
            <p class="post-text">${post.getBody()}</p>
          @endif

          <div class="post-meta">
            <span class="author-badge">Anonymous</span>
            • Posted ${post.getCreatedAtDisplay()}
            @if(me != null && post.getOwnerUid() != null && post.getOwnerUid().equals(me.getId()))
              • <a href="/q/${post.getId()}/edit" style="color: #3d7a52; font-weight: 600;">Edit</a>
            @endif
            @if(me != null && me.getRole() == app.Role.ADMIN)
              <form method="post" action="/admin/posts/${post.getId()}/delete" style="display:inline">
                <button class="btn btn-danger btn-xs">Delete Post</button>
              </form>
            @endif
          </div>
        </div>
      </article>

      <section class="comments-section">
        <h2 class="section-title">
          ${post.getComments().size()} Comment${post.getComments().size() == 1 ? "" : "s"}
        </h2>

        @if(post.getComments().isEmpty())
          <p style="color: #9a8b6f; font-size: 14px; margin-bottom: 20px;">
            No comments yet. Be the first to respond!
          </p>
        @else
          <ul class="comment-list">
            @for(app.Post.Comment c : post.getCommentsNewestFirst())
              <li class="comment-item">
                <div class="comment-body">${c.getBody()}</div>
                <div class="comment-meta">
                  <span class="role-badge role-${c.getAuthorRole().name().toLowerCase()}" style="display: inline-block; margin-right: 8px;">
                    ${c.getAuthorRole().name()}
                  </span>
                  <strong style="color: #3d7a52;">${c.getAuthorName()}</strong>
                  • ${c.getCreatedAtDisplay()}
                </div>
              </li>
            @endfor
          </ul>
        @endif

        @if(me != null && (me.getRole() == app.Role.SCHOLAR || me.getRole() == app.Role.ADMIN))
          <form class="comment-form" method="post" action="/q/${post.getId()}/comment">
            <textarea
              name="body"
              rows="4"
              placeholder="What are your thoughts?"
              required
            ></textarea>
            <div class="form-actions">
              <button class="btn btn-primary" type="submit">Comment</button>
            </div>
          </form>
        @elseif(me != null && me.getRole() == app.Role.USER)
          <div style="background: #f9f6f0; padding: 16px; border-radius: 4px; text-align: center; color: #9a8b6f;">
            <p>Only verified scholars can comment.</p>
            <a href="/verify" style="color: #3d7a52; font-weight: 600;">Request verification</a>
          </div>
        @else
          <div style="background: #f9f6f0; padding: 16px; border-radius: 4px; text-align: center; color: #9a8b6f;">
            <p><a href="/login" style="color: #3d7a52; font-weight: 600;">Log in</a> to comment</p>
          </div>
        @endif
      </section>
    </div>
  </main>
</body>
</html>