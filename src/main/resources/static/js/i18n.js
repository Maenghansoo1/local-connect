const i18n = {
    ko: {
        title: '🗺️ 한국 관광 가이드',
        categoryLabel: '카테고리',
        regionLabel: '지역',
        categories: ['🏞️ 관광지', '🏛️ 문화시설', '🎉 축제', '🏄 레포츠', '🏨 숙박', '🛍️ 쇼핑', '🍽️ 음식점'],
        regions: {
            '1': '서울', '2': '인천', '31': '경기', '32': '강원', '33': '충북', '34': '충남',
            '3': '대전', '8': '세종', '37': '전북', '38': '전남', '5': '광주',
            '35': '경북', '36': '경남', '4': '대구', '6': '부산', '7': '울산', '39': '제주'
        },
        selectMsg: '카테고리와 지역을 선택하세요.',
        loading: '불러오는 중...',
        noData: '에 해당 정보가 없습니다.',
        noAddr: '주소 정보 없음',
        reviewBtn: '💬 리뷰 보기',
        naviBtn: '🚗 길찾기',
        login: '로그인', signup: '회원가입', logout: '로그아웃', mypage: '마이페이지',
    },
    en: {
        title: '🗺️ Korea Travel Guide',
        categoryLabel: 'Category',
        regionLabel: 'Region',
        categories: ['🏞️ Tourist Spots', '🏛️ Culture', '🎉 Festivals', '🏄 Leisure', '🏨 Stay', '🛍️ Shopping', '🍽️ Food'],
        regions: {
            '1': 'Seoul', '2': 'Incheon', '31': 'Gyeonggi', '32': 'Gangwon', '33': 'Chungbuk', '34': 'Chungnam',
            '3': 'Daejeon', '8': 'Sejong', '37': 'Jeonbuk', '38': 'Jeonnam', '5': 'Gwangju',
            '35': 'Gyeongbuk', '36': 'Gyeongnam', '4': 'Daegu', '6': 'Busan', '7': 'Ulsan', '39': 'Jeju'
        },
        selectMsg: 'Please select a category and region.',
        loading: 'Loading...',
        noData: ' has no results.',
        noAddr: 'No address info',
        reviewBtn: '💬 Reviews',
        naviBtn: '🚗 Directions',
        login: 'Login', signup: 'Sign Up', logout: 'Logout', mypage: 'My Page',
    }
};

function setLang(lang) {
    currentLang = lang;
    const t = i18n[lang];

    document.getElementById('nav-title').textContent = t.title;
    document.getElementById('section-category').textContent = t.categoryLabel;
    document.getElementById('section-region').textContent = t.regionLabel;
    document.getElementById('btn-ko').classList.toggle('active', lang === 'ko');
    document.getElementById('btn-en').classList.toggle('active', lang === 'en');

    document.querySelectorAll('.category-btn').forEach((btn, i) => {
        btn.textContent = t.categories[i];
    });
    document.querySelectorAll('.region-btn').forEach(btn => {
        const code = btn.dataset.code;
        if (t.regions[code]) btn.textContent = t.regions[code];
    });

    updateNavbar(currentUser);
    if (selectedRegion) loadSpots();
}
