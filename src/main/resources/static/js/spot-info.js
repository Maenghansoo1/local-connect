// ===== 관광지 상세정보 모달 =====
// 카드 클릭(이미지/제목 부분) 시 열리는 창
// 주소, 전화, 홈페이지, 소개글, 길찾기 버튼 표시

var infoSpot = null;  // 현재 보고 있는 관광지

function openSpotInfo(spot) {
    infoSpot = spot;

    // 1) 카드에 있는 기본 정보로 먼저 채우기 (바로 보이게)
    document.getElementById('info-title').textContent = spot.title;
    document.getElementById('info-img').src = spot.firstimage || 'https://placehold.co/500x220?text=No+Image';

    var metaEl = document.getElementById('info-meta');
    metaEl.innerHTML = spot.addr1 ? '<p class="info-row">📍 ' + spot.addr1 + '</p>' : '';

    var overviewEl = document.getElementById('info-overview');
    overviewEl.style.display = 'none';
    overviewEl.innerHTML = '';

    openModal('spot-info-modal');

    // 2) 백엔드에서 상세 정보(소개글·전화·홈페이지) 불러와서 채우기
    // ★ API 변경 지점 — 카드 클릭 요약창은 detailCommon2 API 사용 (모든 관광지 공통)
    fetch('/api/spots/common?contentId=' + spot.contentid + '&lang=' + currentLang)  // → detailCommon2 API
        .then(function(r) { return r.json(); })
        .then(function(data) {
            var item = data.response && data.response.body && data.response.body.items && data.response.body.items.item;
            if (!item) return;
            var info = Array.isArray(item) ? item[0] : item;

            // 더 좋은 이미지가 있으면 교체
            if (info.firstimage) {
                document.getElementById('info-img').src = info.firstimage;
            }

            // 기본 정보 (주소·전화)만 간단히 — 홈페이지·소개글 전문은 '더보기'에서
            var metaHtml = '';
            if (info.addr1) {
                metaHtml += '<p class="info-row">📍 ' + info.addr1 + '</p>';
            }
            if (info.tel) {
                metaHtml += '<p class="info-row">📞 ' + info.tel + '</p>';
            }
            metaEl.innerHTML = metaHtml;

            // 소개글은 '요약'만 보여줌 (HTML 태그 제거 후 앞부분 120자)
            var summaryHtml = '';
            if (info.overview) {
                var plain = info.overview.replace(/<[^>]+>/g, '').trim(); // 태그 제거해서 순수 글자만
                var summary = plain.length > 120 ? plain.slice(0, 120) + ' …' : plain;
                summaryHtml =
                    '<p class="detail-section-title">📖 요약</p>' +
                    '<div class="info-overview-text">' + summary + '</div>';
            }

            // 요약 + '더보기' 버튼 (더보기를 누르면 상세 페이지로 이동)
            overviewEl.innerHTML =
                summaryHtml +
                '<button class="info-more-btn" onclick="openFullDetail()">더보기 (상세정보) →</button>';
            overviewEl.style.display = 'block';
        })
        .catch(function() {
            // 상세 정보 불러오기 실패해도 모달은 그대로 열림
        });
}

// ===== '더보기' → 상세정보 페이지(detail-modal)로 이동 =====
// detail-modal에는 소개글 전문·홈페이지·길찾기·일정추가·리뷰가 모두 들어있음
function openFullDetail() {
    if (!infoSpot) return;
    closeModal('spot-info-modal');               // 요약 모달 닫기
    // openDetail은 (event, spot) 형태라서 가짜 event 객체를 만들어 넘김
    openDetail({ stopPropagation: function() {} }, infoSpot);
}
