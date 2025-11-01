@param app.User me

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Scholar Verification – Amaaneh</title>
  <link rel="stylesheet" href="/styles.css" />
</head>
<body>
  @template.header(me = me)

  <main class="container">
    <section class="card verify-card">
      <header class="verify-header">
        <h1 class="verify-title">Scholar Verification</h1>
        <span class="status-pill">
          Current Status:
          <span class="role-badge">${me == null ? "GUEST" : me.getRole().name()}</span>
        </span>
      </header>

      <p class="verify-subtext">
        To become a verified scholar and comment on questions, please submit a verification request
        with information about your qualifications.
      </p>

      <form method="post" action="/verify" enctype="multipart/form-data" id="verifyForm" class="verify-form">
        <div class="form-row">
          <label for="email">Contact email <span class="hint">(optional)</span></label>
          <input id="email" name="email" type="email" placeholder="you@example.com" />
        </div>

        <div class="form-row">
          <label for="file">Diploma / Transcript <span class="required">*</span>
            <span class="hint">(PDF or image, max 8 MB)</span>
          </label>

          <!-- Drag & Drop Upload -->
          <div id="dropzone" class="dropzone" tabindex="0" role="button" aria-label="Upload diploma or transcript">
            <input id="file" name="file" type="file" accept=".pdf,image/*" hidden />
            <div class="dropzone-inner">
              <svg viewBox="0 0 24 24" aria-hidden="true" class="dz-icon">
                <path d="M14,14H10V3H14M19,13V17H5V13H3V17A2,2 0 0,0 5,19H19A2,2 0 0,0 21,17V13" />
                <path d="M12,7L8,11H11V21H13V11H16L12,7Z" />
              </svg>
              <div class="dz-text">
                <strong>Drag & drop</strong> your file here, or
                <button type="button" class="link-btn" id="pickBtn">browse</button>
                <div class="dz-note">PDF or image only. Max size 8 MB.</div>
              </div>
            </div>
          </div>

          <div class="file-meta" id="fileMeta" hidden>
            <div><span class="file-name" id="fileName"></span></div>
            <div class="file-size" id="fileSize"></div>
          </div>

          <div class="form-error" id="fileError" hidden></div>
        </div>

        <div class="form-row">
          <label for="note">Notes <span class="hint">(e.g., degree, year, institution)</span></label>
          <textarea id="note" name="note" rows="4" placeholder="B.S. in … from … (2022). Any extra notes."></textarea>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" id="submitBtn" disabled>
            Submit Verification Request
          </button>
        </div>
      </form>
    </section>
  </main>

  <script>
    (function () {
      const drop = document.getElementById('dropzone');
      const fileInput = document.getElementById('file');
      const pickBtn = document.getElementById('pickBtn');
      const fileMeta = document.getElementById('fileMeta');
      const fileName = document.getElementById('fileName');
      const fileSize = document.getElementById('fileSize');
      const fileError = document.getElementById('fileError');
      const submitBtn = document.getElementById('submitBtn');

      const MAX = 8 * 1024 * 1024; // 8 MB
      const ok = (f) => ['application/pdf', 'image/png', 'image/jpeg', 'image/webp', 'image/gif'].includes(f.type) || f.name.toLowerCase().endsWith('.pdf');

      function showMeta(f) {
        fileMeta.hidden = false;
        fileName.textContent = f.name;
        fileSize.textContent = (f.size / 1024 / 1024).toFixed(2) + ' MB';
      }
      function error(msg) {
        fileError.hidden = false;
        fileError.textContent = msg;
        submitBtn.disabled = true;
      }
      function clearError() {
        fileError.hidden = true;
        fileError.textContent = '';
      }
      function handleFile(f) {
        clearError();
        if (!f) return;
        if (!ok(f)) return error('Please upload a PDF or image.');
        if (f.size > MAX) return error('File too large (max 8 MB).');
        showMeta(f);
        submitBtn.disabled = false;
      }

      pickBtn.addEventListener('click', () => fileInput.click());
      fileInput.addEventListener('change', (e) => handleFile(e.target.files[0]));

      ['dragenter','dragover'].forEach(ev =>
        drop.addEventListener(ev, e => { e.preventDefault(); drop.classList.add('is-over'); })
      );
      ['dragleave','drop'].forEach(ev =>
        drop.addEventListener(ev, e => { e.preventDefault(); drop.classList.remove('is-over'); })
      );
      drop.addEventListener('drop', (e) => {
        const f = e.dataTransfer.files && e.dataTransfer.files[0];
        if (f) { fileInput.files = e.dataTransfer.files; handleFile(f); }
      });
      drop.addEventListener('click', () => fileInput.click());
      drop.addEventListener('keypress', (e) => { if (e.key === 'Enter' || e.key === ' ') fileInput.click(); });
    })();
  </script>
</body>
</html>