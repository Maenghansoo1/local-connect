// 상단 검색창 — 지금 보이는 관광지 카드를 이름으로 걸러줌
function searchSpots(keyword) {
    const q = keyword.trim().toLowerCase();
    document.querySelectorAll('#spot-list .spot-card').forEach(card => {
        const titleEl = card.querySelector('h3');
        const title = titleEl ? titleEl.textContent.toLowerCase() : '';
        // 검색어가 제목에 들어있으면 보이고, 아니면 숨김
        card.style.display = title.includes(q) ? '' : 'none';
    });
}

function selectCategory(contentTypeId, btnEl) {
    document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));
    btnEl.classList.add('active');
    selectedCategory = contentTypeId;

    // 축제(15)가 아닌 카테고리를 고르면 임박순 정렬 토글 끄기
    if (festivalSortMode && contentTypeId !== '15') {
        festivalSortMode = false;
        const t = document.getElementById('festival-sort-toggle');
        if (t) t.checked = false;
    }

    // 축제는 지역 선택 없이도 바로 불러옴 (전국 기준 날짜순)
    if (contentTypeId === '15') {
        loadSpots(1);
    } else if (selectedRegion) {
        loadSpots(1);
    }
}

// ===== 축제 임박순 정렬 토글 =====
// 켜면 축제 카테고리로 전환 + 시작일이 가까운 순으로 정렬
function toggleFestivalSort() {
    festivalSortMode = document.getElementById('festival-sort-toggle').checked;

    if (festivalSortMode) {
        // 정렬은 축제에만 의미가 있으므로 축제 카테고리로 자동 전환
        selectedCategory = '15';
        document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));
        const festBtn = document.getElementById('cat-festival');
        if (festBtn) festBtn.classList.add('active');
    }

    loadSpots(1);
}

function selectRegion(areaCode, lat, lng, btnEl) {
    document.querySelectorAll('.region-btn').forEach(b => b.classList.remove('active'));
    btnEl.classList.add('active');
    selectedRegion = {
        areaCode,
        // 전체(areaCode 빈 값)면 '전체', 아니면 지역 이름
        regionName: areaCode ? (i18n[currentLang].regions[areaCode] || areaCode) : i18n[currentLang].regions.all,
        lat,
        lng
    };
    map.setCenter(new kakao.maps.LatLng(lat, lng));
    map.setLevel(areaCode ? 10 : 13);  // 전체는 전국이 보이게 넓게
    loadSpots(1);
}

function loadSpots(page = 1) {
    currentPage = page;
    const listEl = document.getElementById('spot-list');
    listEl.innerHTML = '<p class="empty-msg">불러오는 중...</p>';
    document.getElementById('pagination').innerHTML = '';

    // ★ API 변경 지점 1 — 목록 API가 카테고리에 따라 바뀜
    let fetchUrl;
    if (selectedCategory === '15') {
        // [축제] searchFestival2 API → 시작일/종료일 줌 (지역 없어도 전국 조회 가능)
        const areaCode = selectedRegion ? selectedRegion.areaCode : '';
        fetchUrl = `/api/spots/festivals?areaCode=${areaCode}&lang=${currentLang}&pageNo=${page}&numOfRows=${PAGE_SIZE}`;
    } else {
        // [그 외] areaBasedList2 API → 지역·카테고리별 기본 목록
        if (!selectedRegion) return;
        fetchUrl = `/api/spots?areaCode=${selectedRegion.areaCode}&contentTypeId=${selectedCategory}&lang=${currentLang}&pageNo=${page}&numOfRows=${PAGE_SIZE}`;
    }

    fetch(fetchUrl)
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

            // 임박축제순 토글 ON: 시작일(eventstartdate)이 가까운 순으로 정렬
            if (festivalSortMode) {
                itemArr.sort((a, b) =>
                    (a.eventstartdate || '99999999').localeCompare(b.eventstartdate || '99999999')
                );
            }

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

// 축제 날짜 배지 생성 ("진행중" / "D-N일")
function getFestivalBadge(startStr, endStr) {
    if (!startStr) return '';
    const today = new Date(); today.setHours(0, 0, 0, 0);
    const s = new Date(startStr.slice(0,4), parseInt(startStr.slice(4,6))-1, parseInt(startStr.slice(6,8)));
    const e = new Date((endStr||startStr).slice(0,4), parseInt((endStr||startStr).slice(4,6))-1, parseInt((endStr||startStr).slice(6,8)));
    const msDay = 86400000;
    if (today >= s && today <= e) {
        return `<span class="festival-badge ongoing">🟢 진행중</span>`;
    } else if (today < s) {
        const dDay = Math.ceil((s - today) / msDay);
        return `<span class="festival-badge upcoming">📅 D-${dDay}</span>`;
    }
    return `<span class="festival-badge ended">종료</span>`;
}

// "20260601" → "2026.06.01" 형식 변환
function fmtDate(d) {
    if (!d || d.length !== 8) return '';
    return `${d.slice(0,4)}.${d.slice(4,6)}.${d.slice(6,8)}`;
}

function createSpotCard(spot) {
    const card = document.createElement('div');
    card.className = 'spot-card';

    // 축제인 경우 날짜 정보 표시
    const isFestival = spot.eventstartdate;
    const festivalHtml = isFestival ? `
        <div class="festival-info">
            ${getFestivalBadge(spot.eventstartdate, spot.eventenddate)}
            <span class="festival-period">${fmtDate(spot.eventstartdate)} ~ ${fmtDate(spot.eventenddate)}</span>
        </div>` : '';

    card.innerHTML = `
        <div class="spot-card-img-wrap">
            <img src="${spot.firstimage || 'https://via.placeholder.com/250x120?text=No+Image'}" alt="${spot.title}">
            <button class="fav-btn" id="fav-${spot.contentid}" onclick="toggleFavorite(event, ${JSON.stringify(spot).replace(/"/g, '&quot;')})">🤍</button>
        </div>
        <div class="spot-card-body">
            <h3>${spot.title}</h3>
            ${festivalHtml}
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
        openSpotInfo(spot); // 카드 클릭 시 관광지 상세정보 모달 열기 (spot-info.js)
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

    // ★ API 변경 지점 2 — 축제일 때만 detailIntro2 API를 추가로 호출 (기간·행사장소)
    if (selectedCategory === '15') {
        fetch(`/api/spots/detail?contentId=${spot.contentid}&contentTypeId=15&lang=${currentLang}`)  // → detailIntro2 API
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

    // ★ API 변경 지점 3 — 모든 상세 모달은 detailCommon2 API로 소개글·홈페이지를 부름 (detail-info.js 안에서 호출)
    loadDetailCommon(spot.contentid, currentLang); // 소개글·홈페이지 불러오기 (detail-info.js)
    loadReviews(spot.contentid);
    openModal('detail-modal');
}
