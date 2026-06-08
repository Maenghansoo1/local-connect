// ===== 관광지 소개 정보 불러오기 =====
// spots.js의 openDetail() 이 호출될 때 함께 실행됨
// 소개글(overview)과 홈페이지 링크를 detail-modal 안에 표시

function loadDetailCommon(contentId, lang) {
    // 소개 정보 영역 초기화
    var infoEl = document.getElementById('detail-common-info');
    infoEl.style.display = 'none';
    infoEl.innerHTML = '';

    // 백엔드에 소개 정보 요청
    fetch('/api/spots/common?contentId=' + contentId + '&lang=' + lang)
        .then(function(r) { return r.json(); })
        .then(function(data) {
            var item = data.response && data.response.body && data.response.body.items && data.response.body.items.item;
            if (!item) return;

            // 배열로 올 수도 있고 객체로 올 수도 있어서 처리
            var info = Array.isArray(item) ? item[0] : item;

            var overview = info.overview || '';   // 소개글
            var homepage = info.homepage || '';   // 홈페이지 링크

            var html = '';

            // 소개글이 있으면 표시
            if (overview) {
                html += '<div style="font-size:13px;color:#444;line-height:1.8;margin-bottom:12px;">' + overview + '</div>';
            }

            // 홈페이지 링크가 있으면 표시 (API가 HTML 태그로 감싸서 주는 경우가 있어서 태그 제거)
            if (homepage) {
                var url = homepage.replace(/<[^>]+>/g, '').trim();
                if (url) {
                    html += '<p style="font-size:13px;margin-bottom:12px;">🔗 <a href="' + url + '" target="_blank" style="color:#4a90e2;">' + url + '</a></p>';
                }
            }

            // 내용이 있을 때만 영역 보여줌
            if (html) {
                infoEl.innerHTML = html;
                infoEl.style.display = 'block';
            }
        })
        .catch(function() {
            // 소개 정보 로드 실패해도 모달은 그냥 열림
        });
}
