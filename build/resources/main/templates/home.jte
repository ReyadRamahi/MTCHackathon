@param app.HomeView vm
@template("base.jte")

<h1>Newest questions</h1>
<p><a href="/ask">Ask anonymously</a></p>

@if(!vm.hasPosts())
  <p>No questions yet.</p>
@endif

<ul class="feed">
  @for(app.Post p : vm.getPostsNewestFirst())
    <li class="post">
      <div class="votes">
        <form method="post" action="/upvote/${p.id}">
          <button class="vote-btn" title="Upvote">&#9650;</button>
        </form>
        <div class="count">0</div>
        <form method="post" action="/downvote/${p.id}">
          <button class="vote-btn" title="Downvote">&#9660;</button>
        </form>
      </div>
      <div class="body">
        <a class="title" href="/q/${p.id}">${p.title}</a>
        <div class="meta">Posted ${p.getCreatedAtDisplay()}</div>
        @if(!p.getSnippet().isEmpty())
          <div class="snippet">${p.getSnippet()}</div>
        @endif
      </div>
    </li>
  @endfor
</ul>