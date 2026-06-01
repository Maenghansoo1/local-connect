function toggleFavorite(e, spot) {
    e.stopPropagation();
    if (!currentUser) {
        alert('로그인이 필요합니다.');
        openModal('login-modal');
        return;
    }
    fetch('/api/favorites/toggle', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            contentId: spot.contentid,
            title: spot.title,
            addr: spot.addr1 || '',
            image: spot.firstimage || '',
            contentTypeId: selectedCategory,
            mapx: spot.mapx || '',
            mapy: spot.mapy || ''
        })
    })
    .then(r => r.json())
    .then(d => {
        const btn = document.getElementById(`fav-${spot.contentid}`);
        if (btn) btn.textContent = d.saved ? '❤️' : '🤍';
    });
}

function loadFavorites() {
    fetch('/api/favorites').then(r => r.json()).then(list => {
        const el = document.getElementById('fav-tab');
        if (!list.length) {
            el.innerHTML = '<p class="empty-msg">즐겨찾기가 없습니다.</p>';
            return;
        }
        el.innerHTML = list.map(f => `
            <div class="my-card">
                <img src="${f.image || 'https://via.placeholder.com/70x55?text=No'}" alt="${f.title}">
                <div class="my-card-info"><h4>${f.title}</h4><p>${f.addr || '주소 없음'}</p></div>
                <button class="my-card-del" onclick="removeFavorite('${f.contentId}', this.closest('.my-card'))">×</button>
            </div>`).join('');
    });
}

function removeFavorite(contentId, el) {
    fetch('/api/favorites/toggle', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ contentId })
    }).then(() => el.remove());
}
