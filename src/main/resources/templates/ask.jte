@import app.User
@import app.Post
@param User me
@param String mode = "create"
@param Post post = null

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${mode.equals("edit") ? "Edit Question" : "Ask a Question"} - Amaaneh</title>
  <link rel="stylesheet" href="/styles.css">
</head>
<body>
  @template.header(me = me)

  <main class="form-container">
    <div class="form-card">
      <h1 class="form-title">
        ${mode.equals("edit") ? "Edit Your Question" : "Ask a Question"}
      </h1>

      <p style="color: #9a8b6f; font-size: 14px; margin-bottom: 20px;">
        Your question will be answered by verified scholars in the community.
      </p>

      <form method="post" action="${mode.equals("edit") ? ("/q/" + post.getId() + "/edit") : "/ask"}">
        <div class="form-group">
          <label class="form-label" for="title">Title *</label>
          <input
            class="form-input"
            type="text"
            id="title"
            name="title"
            value="${mode.equals("edit") && post != null ? post.getTitle() : ""}"
            placeholder="What's your question?"
            required
          />
        </div>

        <div class="form-group">
          <label class="form-label" for="body">Details (optional)</label>
          <textarea
            class="form-textarea"
            id="body"
            name="body"
            rows="8"
            placeholder="Provide additional context or details about your question..."
          >${mode.equals("edit") && post != null ? post.getBody() : ""}</textarea>
        </div>

        <button class="form-submit" type="submit">
          ${mode.equals("edit") ? "Save Changes" : "Post Question"}
        </button>
      </form>
    </div>
  </main>
</body>
</html>