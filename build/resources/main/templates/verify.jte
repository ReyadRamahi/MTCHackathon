@param app.User me

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Request Verification - Amaanah</title>
  <link rel="stylesheet" href="/styles.css">
</head>
<body>
  @template.header(me = me)

  <main class="form-container">
    <div class="form-card">
      <h1 class="form-title">Scholar Verification</h1>

      <div style="background: #f6f7f8; padding: 12px; border-radius: 4px; margin-bottom: 20px;">
        <p style="font-size: 14px; margin-bottom: 8px;">
          <strong>Current Status:</strong>
          <span class="role-badge role-${me.getRole().name().toLowerCase()}">
            ${me.getRole().name()}
          </span>
        </p>
      </div>

      @if(me.getRole() == app.Role.SCHOLAR)
        <div style="background: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 16px; border-radius: 4px;">
          <p style="margin: 0; font-weight: 600;">✓ You're already verified as a scholar!</p>
        </div>
      @elseif(me.getRole() == app.Role.ADMIN)
        <div style="background: #fff3cd; border: 1px solid #ffeaa7; color: #856404; padding: 16px; border-radius: 4px;">
          <p style="margin: 0;">You have administrator privileges.</p>
        </div>
      @else
        <p style="color: #7c7c7c; font-size: 14px; margin-bottom: 20px;">
          To become a verified scholar and comment on questions, please submit a verification request with information about your qualifications.
        </p>

        <form method="post" action="/verify/request">
          <div class="form-group">
            <label class="form-label" for="note">Why should you be verified? *</label>
            <textarea
              class="form-textarea"
              id="note"
              name="note"
              rows="6"
              placeholder="Please provide details about your qualifications, credentials, areas of expertise, or links to verify your scholarly background..."
              required
            ></textarea>
          </div>

          <button class="form-submit" type="submit">Submit Verification Request</button>
        </form>
      @endif
    </div>
  </main>
</body>
</html>