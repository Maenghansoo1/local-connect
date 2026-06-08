// ===== 전역 상태 =====
let map, markers = [], infoWindows = [];
let selectedCategory = '12';
let selectedRegion = null;
let currentUser = null;
let currentSpot = null;
let selectedRating = 0;
let currentLang = 'ko';
let currentPage = 1;
const PAGE_SIZE = 6;
let festivalSortMode = false;   // 축제 임박순 정렬 토글 켜짐 여부

// ===== 카카오맵 초기화 =====
map = new kakao.maps.Map(document.getElementById('map'), {
    center: new kakao.maps.LatLng(36.5, 127.5),
    level: 13
});

// ===== 초기 로그인 상태 확인 =====
fetch('/api/auth/me')
    .then(r => r.ok ? r.json() : null)
    .then(d => updateNavbar(d ? d.username : null))
    .catch(() => updateNavbar(null));

// ===== 첫 화면: 전체 지역이 선택된 상태로 목록 표시 =====
const allRegionBtn = document.querySelector('.region-btn[data-code="all"]');
if (allRegionBtn) selectRegion('', 36.5, 127.5, allRegionBtn);
