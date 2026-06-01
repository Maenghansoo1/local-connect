function updateNavbar(username) {
    currentUser = username;
    const nav = document.getElementById('nav-buttons');
    const t = i18n[currentLang];
    nav.innerHTML = username
        ? `<span class="user-info">👤 ${username}</span>
           <button class="nav-btn btn-mypage" onclick="openMypage()">${t.mypage}</button>
           <button class="nav-btn btn-logout" onclick="logout()">${t.logout}</button>`
        : `<button class="nav-btn btn-login" onclick="openModal('login-modal')">${t.login}</button>
           <button class="nav-btn btn-signup" onclick="openModal('signup-modal')">${t.signup}</button>`;
}

function submitLogin() {
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value;
    const msgEl = document.getElementById('login-msg');
    if (!username || !password) {
        msgEl.className = 'form-msg error';
        msgEl.textContent = '아이디와 비밀번호를 입력하세요.';
        return;
    }
    const form = new FormData();
    form.append('username', username);
    form.append('password', password);
    fetch('/api/auth/login', { method: 'POST', body: form })
        .then(r => r.json())
        .then(d => {
            if (d.username) {
                closeModal('login-modal');
                updateNavbar(d.username);
                if (selectedRegion) loadSpots();
            } else {
                msgEl.className = 'form-msg error';
                msgEl.textContent = d.message || '로그인 실패';
            }
        });
}

function submitSignup() {
    const username = document.getElementById('signup-username').value.trim();
    const password = document.getElementById('signup-password').value;
    const nickname = document.getElementById('signup-nickname').value.trim();
    const email    = document.getElementById('signup-email').value.trim();
    const msgEl    = document.getElementById('signup-msg');
    if (!username || !password || !nickname || !email) {
        msgEl.className = 'form-msg error';
        msgEl.textContent = '모든 항목을 입력하세요.';
        return;
    }
    fetch('/api/auth/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password, nickname, email })
    })
    .then(r => r.json())
    .then(d => {
        if (d.message === '회원가입이 완료되었습니다.') {
            msgEl.className = 'form-msg success';
            msgEl.textContent = '가입 완료! 로그인해주세요.';
            setTimeout(() => switchModal('signup-modal', 'login-modal'), 1500);
        } else {
            msgEl.className = 'form-msg error';
            msgEl.textContent = d.message;
        }
    });
}

function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => { currentUser = null; updateNavbar(null); });
}
