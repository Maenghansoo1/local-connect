// ===== 히어로 배너 캐러셀 =====
// 관광 앱 홍보 문구를 자동으로 넘겨가며 보여주는 큰 배너
// (이미지 대신 색 그라데이션을 써서 깨질 일이 없게 함)

// 슬라이드 데이터 (배경색 · 뱃지 · 제목 · 설명)
var heroSlides = [
    { bg: 'linear-gradient(120deg,#2B2417,#574325)', badge: '추천', title: '전국 관광지를 한눈에', sub: '지역을 골라 가까운 명소를 찾아보세요' },
    { bg: 'linear-gradient(120deg,#241F1A,#4A3A2C)', badge: '축제', title: '지금 열리는 축제, 날짜순으로', sub: '가까운 날짜의 축제를 바로 확인하세요' },
    { bg: 'linear-gradient(120deg,#1F1D18,#3D3A2A)', badge: '일정', title: '나만의 여행 일정 만들기', sub: '마음에 든 곳을 달력에 저장해요' }
];

var heroIndex = 0;     // 지금 보이는 슬라이드 번호
var heroTimer = null;  // 자동 넘김 타이머

// 현재 슬라이드를 화면에 그리기
function renderHero() {
    var slide = heroSlides[heroIndex];
    var slideEl = document.getElementById('hero-slide');
    slideEl.style.background = slide.bg;
    slideEl.innerHTML =
        '<span class="hero-badge">' + slide.badge + '</span>' +
        '<h2 class="hero-title">' + slide.title + '</h2>' +
        '<p class="hero-sub">' + slide.sub + '</p>';
    // 오른쪽 아래 "1 / 3" 표시
    document.getElementById('hero-indicator').textContent =
        (heroIndex + 1) + ' / ' + heroSlides.length;
}

// 다음 슬라이드로 (마지막이면 처음으로)
function heroNext() {
    heroIndex = (heroIndex + 1) % heroSlides.length;
    renderHero();
}

// 이전 슬라이드로 (처음이면 마지막으로)
function heroPrev() {
    heroIndex = (heroIndex - 1 + heroSlides.length) % heroSlides.length;
    renderHero();
}

// 5초마다 자동으로 다음 슬라이드
function startHeroAuto() {
    if (heroTimer) clearInterval(heroTimer);
    heroTimer = setInterval(heroNext, 5000);
}

// 페이지가 열리면 바로 시작
renderHero();
startHeroAuto();
