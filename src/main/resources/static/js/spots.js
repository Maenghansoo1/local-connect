function selectCategory(contentTypeId, btnEl) {
    document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));
    btnEl.classList.add('active');
    selectedCategory = contentTypeId;
    if (selectedRegion) loadSpots(1);
}

function selectRegion(areaCode, lat, lng, btnEl) {
    document.querySelectorAll('.region-btn').forEach(b => b.classList.remove('active'));
    btnEl.classList.add('active');
    selectedRegion = {
        areaCode,
        regionName: i18n[currentLang].regions[areaCode] || areaCode,
        lat,
        lng
    };
    map.setCenter(new kakao.maps.LatLng(lat, lng));
    map.setLevel(10);
    loadSpots(1);
}

function loadSpots(page = 1) {
    currentPage = page;
    const listEl = document.getElementById('spot-list');
    listEl.innerHTML = '<p class="empty-msg">불러오는 중...</p>';
    document.getElementById('pagination').innerHTML = '';

    fetch(`/api/spots?areaCode=${selectedRegion.areaCode}&contentTypeId=${selectedCategory}&lang=${currentLang}&pageNo=${page}&numOfRows=${PAGE_SIZE}`)
        .then(res => res.json())
        .then(data => {
            markers.forEach(m => m.setMap(null));
            markers = [];
            infoWindows = [];
            listEl.innerHTML = '';

            const body = data.response?.body;
            const items = body?.items?.item;
            const totalCount = body?.totalCount || 0;
            const totalPages = Math.ceil(totalCount / PAGE_SIZE);

            if (!items || items.length === 0) {
                listEl.innerHTML = `<p class="empty-msg">${selectedRegion.regionName}${i18n[currentLang].noData}</p>`;
                return;
            }

            const itemArr = Array.isArray(items) ? items : [items];
            itemArr.forEach(spot => {
                const card = createSpotCard(spot);
                listEl.appendChild(card);

                if (currentUser) {
                    fetch(`/api/favorites/check?contentId=${spot.contentid}`)
                        .then(r => r.json())
                        .then(d => {
                            const btn = document.getElementById(`fav-${spot.contentid}`);
                            if (btn) btn.textContent = d.saved ? '❤️' : '🤍';
                        });
                }

                if (spot.mapx && spot.mapy) addMapMarker(spot, card);
            });

            renderPagination(page, totalPages, totalCount);
        })
        .catch(() => {
            listEl.innerHTML = '<p class="empty-msg">데이터를 불러오지 못했습니다.</p>';
        });
}

function renderPagination(page, totalPages, totalCount) {
    const el = document.getElementById('pagination');
    if (totalPages <= 1) { el.innerHTML = ''; return; }

    const WINDOW = 5;
    let start = Math.max(1, page - Math.floor(WINDOW / 2));
    let end   = Math.min(totalPages, start + WINDOW - 1);
    if (end - start < WINDOW - 1) start = Math.max(1, end - WINDOW + 1);

    let html = `<div class="pagination-info">${totalCount}개 중 ${(page - 1) * PAGE_SIZE + 1}–${Math.min(page * PAGE_SIZE, totalCount)}개</div>`;
    html += '<div class="pagination-btns">';
    html += `<button class="page-btn page-arrow" onclick="loadSpots(${page - 1})" ${page === 1 ? 'disabled' : ''}>‹</button>`;

    if (start > 1) {
        html += `<button class="page-btn" onclick="loadSpots(1)">1</button>`;
        if (start > 2) html += `<span class="page-ellipsis">…</span>`;
    }
    for (let i = start; i <= end; i++) {
        html += `<button class="page-btn ${i === page ? 'active' : ''}" onclick="loadSpots(${i})">${i}</button>`;
    }
    if (end < totalPages) {
        if (end < totalPages - 1) html += `<span class="page-ellipsis">…</span>`;
        html += `<button class="page-btn" onclick="loadSpots(${totalPages})">${totalPages}</button>`;
    }

    html += `<button class="page-btn page-arrow" onclick="loadSpots(${page + 1})" ${page === totalPages ? 'disabled' : ''}>›</button>`;
    html += '</div>';

    el.innerHTML = html;
}

function createSpotCard(spot) {
    const card = document.createElement('div');
    card.className = 'spot-card';
    card.innerHTML = `
        <div class="spot-card-img-wrap">
            <img src="${spot.firstimage || 'https://via.placeholder.com/250x120?text=No+Image'}" alt="${spot.title}">
            <button class="fav-btn" id="fav-${spot.contentid}" onclick="toggleFavorite(event, ${JSON.stringify(spot).replace(/"/g, '&quot;')})">🤍</button>
        </div>
        <div class="spot-card-body">
            <h3>${spot.title}</h3>
            <p>${spot.addr1 || i18n[currentLang].noAddr}</p>
            <div class="card-actions">
                <button class="card-btn" onclick="openDetail(event, ${JSON.stringify(spot).replace(/"/g, '&quot;')})">${i18n[currentLang].reviewBtn}</button>
                <button class="card-btn btn-navi" onclick="openNavi(event, '${spot.title}', ${spot.mapy}, ${spot.mapx})">${i18n[currentLang].naviBtn}</button>
            </div>
        </div>`;
    return card;
}

function addMapMarker(spot, card) {
    const position = new kakao.maps.LatLng(spot.mapy, spot.mapx);
    const marker = new kakao.maps.Marker({ position, map, title: spot.title });
    markers.push(marker);

    const infoWindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:8px;font-size:13px;font-weight:600;">${spot.title}</div>`
    });
    infoWindows.push(infoWindow);

    kakao.maps.event.addListener(marker, 'click', () => {
        infoWindows.forEach(iw => iw.close());
        infoWindow.open(map, marker);
    });

    card.addEventListener('click', e => {
        if (e.target.closest('.fav-btn') || e.target.closest('.card-actions')) return;
        map.setCenter(position);
        map.setLevel(5);
        infoWindows.forEach(iw => iw.close());
        infoWindow.open(map, marker);
        if (currentUser) {
            fetch('/api/visits', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    contentId: spot.contentid,
                    title: spot.title,
                    addr: spot.addr1 || '',
                    image: spot.firstimage || ''
                })
            });
        }
    });
}

function openNavi(e, title, lat, lng) {
    e.stopPropagation();
    if (!lat || !lng) { alert('해당 장소의 좌표 정보가 없습니다.'); return; }
    window.open(`https://map.kakao.com/link/to/${encodeURIComponent(title)},${lat},${lng}`, '_blank');
}

function openNaviFromDetail() {
    if (!currentSpot?.mapy || !currentSpot?.mapx) { alert('해당 장소의 좌표 정보가 없습니다.'); return; }
    window.open(`https://map.kakao.com/link/to/${encodeURIComponent(currentSpot.title)},${currentSpot.mapy},${currentSpot.mapx}`, '_blank');
}

function openDetail(e, spot) {
    e.stopPropagation();
    currentSpot = spot;

    document.getElementById('detail-title').textContent = spot.title;
    document.getElementById('detail-img').src = spot.firstimage || 'https://via.placeholder.com/500x180?text=No+Image';
    document.getElementById('detail-addr').textContent = spot.addr1 || i18n[currentLang].noAddr;
    document.getElementById('review-content').value = '';
    document.getElementById('review-msg').textContent = '';
    selectedRating = 0;
    updateStars(0);

    document.getElementById('review-form-wrap').style.display = currentUser ? 'block' : 'none';

    const eventInfoEl = document.getElementById('detail-event-info');
    eventInfoEl.style.display = 'none';
    eventInfoEl.innerHTML = '';

    if (selectedCategory === '15') {
        fetch(`/api/spots/detail?contentId=${spot.contentid}&contentTypeId=15&lang=${currentLang}`)
            .then(r => r.json())
            .then(data => {
                const item = data.response?.body?.items?.item;
                if (!item) return;
                const info = Array.isArray(item) ? item[0] : item;

                const startRaw = info.eventstartdate || '';
                const endRaw   = info.eventenddate   || '';
                const place    = info.eventplace      || '';
                const homepage = info.eventhomepage   || '';

                const fmt = d => d.length === 8
                    ? `${d.slice(0,4)}.${d.slice(4,6)}.${d.slice(6,8)}`
                    : d;

                let html = '<div class="event-info-box">';
                if (startRaw || endRaw) html += `<p>📅 <strong>기간</strong>: ${fmt(startRaw)} ~ ${fmt(endRaw)}</p>`;
                if (place)    html += `<p>📍 <strong>행사 장소</strong>: ${place}</p>`;
                if (homepage) {
                    const url = homepage.replace(/<[^>]+>/g, '').trim();
                    if (url) html += `<p>🔗 <strong>홈페이지</strong>: <a href="${url}" target="_blank" style="color:var(--primary);">${url}</a></p>`;
                }
                html += '</div>';

                eventInfoEl.innerHTML = html;
                eventInfoEl.style.display = 'block';
            })
            .catch(() => {});
    }

    loadDetailCommon(spot.contentid, currentLang); // 소개글·홈페이지 불러오기 (detail-info.js)
    loadReviews(spot.contentid);
    openModal('detail-modal');
}
