// ===== 전역 상태 =====
let map, markers = [], infoWindows = [];
let selectedCategory = '12';
let selectedRegion = null;
let currentUser = null;
let currentSpot = null;
let selectedRating = 0;
let currentLang = 'ko';

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
