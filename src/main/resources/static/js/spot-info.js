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
    fetch('/api/spots/common?contentId=' + spot.contentid + '&lang=' + currentLang)
        .then(function(r) { return r.json(); })
        .then(function(data) {
            var item = data.response && data.response.body && data.response.body.items && data.response.body.items.item;
            if (!item) return;
            var info = Array.isArray(item) ? item[0] : item;

            // 더 좋은 이미지가 있으면 교체
            if (info.firstimage) {
                document.getElementById('info-img').src = info.firstimage;
            }

            // 기본 정보 (주소·전화·홈페이지) 다시 그리기
            var metaHtml = '';
            if (info.addr1) {
                metaHtml += '<p class="info-row">📍 ' + info.addr1 + '</p>';
            }
            if (info.tel) {
                metaHtml += '<p class="info-row">📞 ' + info.tel + '</p>';
            }
            if (info.homepage) {
                var url = info.homepage.replace(/<[^>]+>/g, '').trim();  // HTML 태그 제거
                if (url) {
                    metaHtml += '<p class="info-row">🌐 <a href="' + url + '" target="_blank">' + url + '</a></p>';
                }
            }
            metaEl.innerHTML = metaHtml;

            // 소개글
            if (info.overview) {
                overviewEl.innerHTML =
                    '<p class="detail-section-title">📖 소개</p>' +
                    '<div class="info-overview-text">' + info.overview + '</div>';
                overviewEl.style.display = 'block';
            }
        })
        .catch(function() {
            // 상세 정보 불러오기 실패해도 모달은 그대로 열림
        });
}
