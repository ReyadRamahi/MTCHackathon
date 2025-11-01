@param app.User me

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Ask a Question - Amaanah</title>
  <link rel="stylesheet" href="/styles.css">
</head>
<body>
  @template.header(me = me)

  <main class="form-container">
    <div class="form-card">
      <h1 class="form-title">Ask a Question</h1>
      <p style="color: #7c7c7c; font-size: 14px; margin-bottom: 20px;">
        Your question will be answered by verified scholars in the community.
      </p>

      <form method="post" action="/ask">
        <div class="form-group">
          <label class="form-label" for="title">Title *</label>
          <input
            class="form-input"
            type="text"
            id="title"
            name="title"
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
          ></textarea>
        </div>

        <button class="form-submit" type="submit">Post Question</button>
      </form>
    </div>
  </main>
</body>
</html>