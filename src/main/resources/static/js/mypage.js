function openMypage() {
    openModal('mypage-modal');
    document.querySelectorAll('.tab-btn').forEach((b, i) => b.classList.toggle('active', i === 0));
    document.getElementById('fav-tab').style.display = 'block';
    document.getElementById('review-tab').style.display = 'none';
    document.getElementById('visit-tab').style.display = 'none';
    loadProfile();
    loadFavorites();
    loadMyReviews();
    loadVisitHistory();
}

function loadProfile() {
    fetch('/api/auth/me').then(r => r.json()).then(d => {
        document.getElementById('mypage-nickname-text').textContent = d.nickname;
        document.getElementById('mypage-nickname-input').value = d.nickname;
    });
}

function editNickname() {
    document.getElementById('mypage-profile-view').style.display = 'none';
    document.getElementById('mypage-profile-edit').style.display = 'flex';
}

function cancelEditNickname() {
    document.getElementById('mypage-profile-view').style.display = 'flex';
    document.getElementById('mypage-profile-edit').style.display = 'none';
}

function saveNickname() {
    const nickname = document.getElementById('mypage-nickname-input').value.trim();
    if (!nickname) {
        alert('닉네임을 입력해주세요.');
        return;
    }
    fetch('/api/auth/nickname', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nickname })
    })
    .then(r => r.json())
    .then(d => {
        if (d.nickname) {
            document.getElementById('mypage-nickname-text').textContent = d.nickname;
            cancelEditNickname();
        } else {
            alert(d.message || '닉네임 변경에 실패했습니다.');
        }
    });
}

function switchTab(tabId, btnEl) {
    ['fav-tab', 'review-tab', 'visit-tab'].forEach(id => {
        document.getElementById(id).style.display = 'none';
    });
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    document.getElementById(tabId).style.display = 'block';
    btnEl.classList.add('active');
}

function loadMyReviews() {
    fetch('/api/reviews/my').then(r => r.json()).then(list => {
        const el = document.getElementById('review-tab');
        if (!list.length) {
            el.innerHTML = '<p class="empty-msg">작성한 리뷰가 없습니다.</p>';
            return;
        }
        el.innerHTML = list.map(r => `
            <div class="review-item">
                <div class="review-header">
                    <span class="review-author">${r.spotTitle}</span>
                    <span class="review-rating">${'★'.repeat(r.rating)}${'☆'.repeat(5 - r.rating)}</span>
                </div>
                <div class="review-content">${r.content}</div>
                <div style="display:flex;justify-content:space-between;">
                    <span class="review-date">${r.createdAt?.substring(0, 10)}</span>
                    <button class="review-delete" onclick="deleteMyReview(${r.id}, this.closest('.review-item'))">삭제</button>
                </div>
            </div>`).join('');
    });
}

function deleteMyReview(reviewId, el) {
    if (!confirm('리뷰를 삭제할까요?')) return;
    fetch(`/api/reviews/${reviewId}`, { method: 'DELETE' }).then(() => el.remove());
}

function loadVisitHistory() {
    fetch('/api/visits').then(r => r.json()).then(list => {
        const el = document.getElementById('visit-tab');
        if (!list.length) {
            el.innerHTML = '<p class="empty-msg">방문 기록이 없습니다.</p>';
            return;
        }
        el.innerHTML = list.map(v => `
            <div class="my-card">
                <img src="${v.image || 'https://via.placeholder.com/70x55?text=No'}" alt="${v.title}">
                <div class="my-card-info">
                    <h4>${v.title}</h4>
                    <p>${v.addr || '주소 없음'}</p>
                    <p style="color:#aaa;font-size:11px;">${v.visitedAt?.substring(0, 10)}</p>
                </div>
                <button class="my-card-del" onclick="deleteVisitHistory(${v.id}, this.closest('.my-card'))">×</button>
            </div>`).join('');
    });
}

function deleteVisitHistory(id, el) {
    if (!confirm('방문 기록을 삭제할까요?')) return;
    fetch(`/api/visits/${id}`, { method: 'DELETE' }).then(() => el.remove());
}
