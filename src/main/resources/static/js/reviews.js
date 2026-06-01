function setRating(val) {
    selectedRating = val;
    updateStars(val);
}

function updateStars(val) {
    document.querySelectorAll('#star-select span').forEach((s, i) => {
        s.classList.toggle('on', i < val);
    });
}

function loadReviews(contentId) {
    const listEl = document.getElementById('review-list');
    listEl.innerHTML = '<p style="color:#aaa;font-size:13px;">불러오는 중...</p>';
    fetch(`/api/reviews/spot/${contentId}`)
        .then(r => r.json())
        .then(reviews => {
            if (reviews.length === 0) {
                listEl.innerHTML = '<p style="color:#aaa;font-size:13px;text-align:center;">아직 리뷰가 없습니다.</p>';
                return;
            }
            listEl.innerHTML = reviews.map(r => `
                <div class="review-item">
                    <div class="review-header">
                        <span class="review-author">${r.user.nickname || r.user.username}</span>
                        <span class="review-rating">${'★'.repeat(r.rating)}${'☆'.repeat(5 - r.rating)}</span>
                    </div>
                    <div class="review-content">${r.content}</div>
                    <div style="display:flex;justify-content:space-between;align-items:center;">
                        <span class="review-date">${r.createdAt?.substring(0, 10)}</span>
                        ${currentUser && currentUser === r.user.username
                            ? `<button class="review-delete" onclick="deleteReview(${r.id})">삭제</button>` : ''}
                    </div>
                </div>`).join('');
        });
}

function submitReview() {
    const content = document.getElementById('review-content').value.trim();
    const msgEl = document.getElementById('review-msg');
    if (!selectedRating) {
        msgEl.className = 'form-msg error';
        msgEl.textContent = '별점을 선택해주세요.';
        return;
    }
    if (!content) {
        msgEl.className = 'form-msg error';
        msgEl.textContent = '리뷰 내용을 입력해주세요.';
        return;
    }
    fetch('/api/reviews', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            contentId: currentSpot.contentid,
            spotTitle: currentSpot.title,
            content,
            rating: String(selectedRating)
        })
    })
    .then(r => r.json())
    .then(d => {
        msgEl.className = 'form-msg success';
        msgEl.textContent = d.message;
        document.getElementById('review-content').value = '';
        selectedRating = 0;
        updateStars(0);
        loadReviews(currentSpot.contentid);
    });
}

function deleteReview(reviewId) {
    if (!confirm('리뷰를 삭제할까요?')) return;
    fetch(`/api/reviews/${reviewId}`, { method: 'DELETE' })
        .then(() => loadReviews(currentSpot.contentid));
}
