function openMypage() {
    openModal('mypage-modal');
    document.querySelectorAll('.tab-btn').forEach((b, i) => b.classList.toggle('active', i === 0));
    document.getElementById('fav-tab').style.display = 'block';
    document.getElementById('review-tab').style.display = 'none';
    document.getElementById('visit-tab').style.display = 'none';
    loadFavorites();
    loadMyReviews();
    loadVisitHistory();
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
            </div>`).join('');
    });
}
