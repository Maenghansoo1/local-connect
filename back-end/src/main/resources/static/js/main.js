// 전국 17개 지역
const REGIONS = [
  '서울', '부산', '제주', '경기', '강원', '인천',
  '대구', '광주', '대전', '울산', '세종',
  '충북', '충남', '경북', '경남', '전북', '전남'
]

// 현재 상태
let currentPage    = 0
let currentRegion  = ''
let currentKeyword = ''
let totalPages     = 0
let currentTab     = 'login'
let currentUser    = null  // 로그인한 유저 정보

// 날짜 선택 모달 관련 상태
let pickerCalendar        = null   // FullCalendar 인스턴스
let currentFestivalData   = null   // 현재 일정 저장 중인 축제 정보
let selectedScheduleStart = null   // 드래그로 선택한 시작일
let selectedScheduleEnd   = null   // 드래그로 선택한 종료일

// 현재 언어 ('ko' = 한국어, 'en' = 영어)
let currentLang = 'ko'

// 페이지가 열리면 실행
document.addEventListener('DOMContentLoaded', function () {
  renderRegionButtons()
  checkLoginStatus()
  loadFestivals()
})

// ────────────────────────────────
// 로그인 상태 확인 (세션 쿠키로 자동)
// ────────────────────────────────
async function checkLoginStatus() {
  try {
    const res = await fetch('/api/auth/me')
    if (res.ok) {
      const data = await res.json()
      currentUser = data
    }
  } catch (e) {
    // 서버 오류 — 무시
  }
  // 로그인 여부와 관계없이 항상 UI 업데이트
  updateAuthUI()
}

// ────────────────────────────────
// 지역 필터 버튼 생성
// ────────────────────────────────
function renderRegionButtons() {
  const container = document.getElementById('region-buttons')

  // 전체 버튼
  let html = `<button class="btn btn-primary btn-sm me-1 mb-1" onclick="selectRegion('', this)">전체</button>`

  // 지역별 버튼
  REGIONS.forEach(r => {
    html += `<button class="btn btn-outline-secondary btn-sm me-1 mb-1" onclick="selectRegion('${r}', this)">${r}</button>`
  })

  container.innerHTML = html
}

// 지역 버튼 클릭
function selectRegion(region, btn) {
  currentRegion  = region
  currentKeyword = ''
  currentPage    = 0
  document.getElementById('search-input').value = ''

  // 버튼 활성화 표시
  document.querySelectorAll('#region-buttons button').forEach(b => {
    b.className = 'btn btn-outline-secondary btn-sm me-1 mb-1'
  })
  btn.className = 'btn btn-primary btn-sm me-1 mb-1'

  loadFestivals()
}

// ────────────────────────────────
// 검색
// ────────────────────────────────
function handleSearch(e) {
  e.preventDefault()
  currentKeyword = document.getElementById('search-input').value.trim()
  currentRegion  = ''
  currentPage    = 0
  loadFestivals()
}

// ────────────────────────────────
// 축제 목록 불러오기
// ────────────────────────────────
async function loadFestivals() {
  const grid = document.getElementById('festival-grid')
  grid.innerHTML = '<div class="col-12 text-center py-5"><div class="spinner-border text-primary"></div></div>'

  let url = `/api/events?page=${currentPage}&size=12&sort=startDate,asc&lang=${currentLang}`
  if (currentRegion)  url += `&region=${currentRegion}`
  if (currentKeyword) url += `&keyword=${currentKeyword}`

  try {
    const res  = await fetch(url)
    const data = await res.json()

    totalPages = data.totalPages
    document.getElementById('result-count').textContent = `총 ${data.totalElements}개의 축제`

    renderFestivals(data.content)
    renderPagination()
  } catch (e) {
    grid.innerHTML = '<div class="col-12"><div class="alert alert-danger">서버에 연결할 수 없습니다. 백엔드가 실행 중인지 확인해주세요.</div></div>'
  }
}

// ────────────────────────────────
// 축제 카드 그리기
// ────────────────────────────────
function renderFestivals(festivals) {
  const grid = document.getElementById('festival-grid')

  if (festivals.length === 0) {
    grid.innerHTML = '<div class="col-12 text-center text-muted py-5">검색 결과가 없습니다.</div>'
    return
  }

  grid.innerHTML = festivals.map(f => `
    <div class="col">
      <div class="card h-100 festival-card" onclick="openFestivalModal(${f.id})">
        ${f.imageUrl
          ? `<img src="${f.imageUrl}" class="card-img-top" style="height:200px; object-fit:cover" alt="${f.title}">`
          : `<div class="card-img-top d-flex align-items-center justify-content-center bg-light" style="height:200px">
               <span class="fs-1">🎪</span>
             </div>`
        }
        <div class="card-body">
          <span class="badge bg-primary mb-2">${f.region}</span>
          <h5 class="card-title">${f.title}</h5>
          <p class="card-text text-muted small">📅 ${formatDate(f.startDate)}${f.endDate && f.endDate !== f.startDate ? ' ~ ' + formatDate(f.endDate) : ''}</p>
          ${f.address ? `<p class="card-text text-muted small">📍 ${f.address}</p>` : ''}
        </div>
      </div>
    </div>
  `).join('')
}

// ────────────────────────────────
// 페이지네이션
// ────────────────────────────────
function renderPagination() {
  const container = document.getElementById('pagination')

  if (totalPages <= 1) {
    container.innerHTML = ''
    return
  }

  container.innerHTML = `
    <button class="btn btn-outline-secondary" onclick="changePage(${currentPage - 1})" ${currentPage === 0 ? 'disabled' : ''}>이전</button>
    <span class="px-3">${currentPage + 1} / ${totalPages}</span>
    <button class="btn btn-outline-secondary" onclick="changePage(${currentPage + 1})" ${currentPage === totalPages - 1 ? 'disabled' : ''}>다음</button>
  `
}

function changePage(page) {
  currentPage = page
  loadFestivals()
  window.scrollTo(0, 0)
}

// ────────────────────────────────
// 축제 상세 모달
// ────────────────────────────────
async function openFestivalModal(id) {
  const modal = new bootstrap.Modal(document.getElementById('festivalModal'))
  document.getElementById('modal-title').textContent = '불러오는 중...'
  document.getElementById('modal-body').innerHTML = '<div class="text-center py-4"><div class="spinner-border"></div></div>'
  modal.show()

  const res = await fetch(`/api/events/${id}?lang=${currentLang}`)
  const f   = await res.json()

  document.getElementById('modal-title').textContent = f.title
  document.getElementById('modal-body').innerHTML = `
    ${f.imageUrl ? `<img src="${f.imageUrl}" class="img-fluid rounded mb-3 w-100" style="max-height:300px; object-fit:cover" alt="${f.title}">` : ''}
    <p>📅 ${formatDate(f.startDate)}${f.endDate && f.endDate !== f.startDate ? ' ~ ' + formatDate(f.endDate) : ''}</p>
    ${f.address  ? `<p>📍 ${f.address}</p>` : ''}
    ${f.tel      ? `<p>📞 <a href="tel:${f.tel}">${f.tel}</a></p>` : ''}
    ${f.homepage ? `<p>🌐 <a href="${f.homepage}" target="_blank">${f.homepage}</a></p>` : ''}
    ${f.overview ? `<hr><p>${f.overview}</p>` : ''}
  `

  // 로그인한 경우에만 일정 저장 버튼 표시
  const saveBtnArea = document.getElementById('modal-schedule-btn')
  if (currentUser) {
    // 버튼 클릭 시 날짜 선택 달력 모달이 열림
    saveBtnArea.innerHTML = `<button class="btn btn-primary" onclick="openSchedulePicker(currentFestivalData)">📅 일정 저장</button>`
  } else {
    saveBtnArea.innerHTML = `<small class="text-muted">로그인하면 일정을 저장할 수 있어요</small>`
  }

  // 현재 축제 정보를 전역 변수에 저장 (날짜 선택 모달에서 사용)
  currentFestivalData = f
}

// ────────────────────────────────
// 날짜 선택 달력 모달 열기
// ────────────────────────────────
function openSchedulePicker(festival) {
  if (!festival) return

  // 기본 날짜 = 축제 전체 기간
  selectedScheduleStart = festival.startDate
  selectedScheduleEnd   = festival.endDate || festival.startDate

  // 모달 제목 설정
  document.getElementById('picker-title').textContent = festival.title

  // 축제 모달 닫고 날짜 선택 모달 열기
  bootstrap.Modal.getInstance(document.getElementById('festivalModal')).hide()

  const pickerModal = new bootstrap.Modal(document.getElementById('schedulePickerModal'))
  pickerModal.show()

  // 모달이 완전히 열린 다음 달력 초기화 (크기 계산 때문에 필요)
  document.getElementById('schedulePickerModal').addEventListener('shown.bs.modal', function () {
    initPickerCalendar(festival)
  }, { once: true })
}

// ────────────────────────────────
// 날짜 선택 달력 초기화
// ────────────────────────────────
function initPickerCalendar(festival) {
  const calEl = document.getElementById('schedule-picker-calendar')

  // 이미 달력이 있으면 제거
  if (pickerCalendar) {
    pickerCalendar.destroy()
    pickerCalendar = null
  }

  const festivalStartStr = toCalendarDate(festival.startDate)
  const festivalEndStr   = addOneDay(festival.endDate || festival.startDate)

  // 날짜 비교를 위해 Date 객체로 변환
  const festivalStartDate = new Date(festivalStartStr)
  const festivalEndDate   = new Date(festivalEndStr)   // exclusive (마지막날 + 1)

  pickerCalendar = new FullCalendar.Calendar(calEl, {
    initialView: 'dayGridMonth',
    initialDate: festivalStartStr,  // 축제 시작월로 이동
    locale: 'ko',
    selectable: true,               // 드래그로 날짜 선택 가능
    unselectAuto: false,            // 다른 곳 클릭해도 선택 유지
    headerToolbar: {
      left: 'prev,next',
      center: 'title',
      right: ''
    },

    // 축제 기간 안에서만 선택 허용
    selectAllow: function (selectInfo) {
      return selectInfo.start >= festivalStartDate && selectInfo.end <= festivalEndDate
    },

    // 날짜 셀마다 클래스를 붙여서 색상 조절
    // 축제 기간 = 흰색(기본), 그 외 = 회색(.day-unavailable)
    dayCellClassNames: function (arg) {
      if (arg.date < festivalStartDate || arg.date >= festivalEndDate) {
        return ['day-unavailable']
      }
      return []
    },

    // 드래그로 날짜를 선택하면 실행
    select: function (info) {
      selectedScheduleStart = info.startStr.replace(/-/g, '')

      // FullCalendar의 end는 "마지막날 + 1일"이므로 하루 빼기
      const endDate = new Date(info.end)
      endDate.setDate(endDate.getDate() - 1)
      selectedScheduleEnd =
        endDate.getFullYear() +
        String(endDate.getMonth() + 1).padStart(2, '0') +
        String(endDate.getDate()).padStart(2, '0')

      // 선택한 날짜를 연한 녹색으로 표시 (CSS .fc-highlight 적용됨)
      document.getElementById('selected-date-info').innerHTML =
        `선택한 날짜: <strong class="text-success">${formatDate(selectedScheduleStart)} ~ ${formatDate(selectedScheduleEnd)}</strong>`
    }
  })

  pickerCalendar.render()

  // 기본값 안내 문구
  document.getElementById('selected-date-info').innerHTML =
    `기본 날짜 (축제 전체 기간): <strong>${formatDate(festival.startDate)} ~ ${formatDate(festival.endDate || festival.startDate)}</strong>
     <br><small class="text-muted">흰색 날짜를 드래그해서 방문 날짜를 선택하세요</small>`
}

// ────────────────────────────────
// 일정 저장 확인 버튼
// ────────────────────────────────
async function confirmSaveSchedule() {
  if (!currentFestivalData || !selectedScheduleStart) {
    alert('저장할 축제 정보가 없습니다.')
    return
  }

  try {
    const res = await fetch('/api/schedules', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        eventId: currentFestivalData.id,
        startDate: selectedScheduleStart,
        endDate: selectedScheduleEnd
      })
    })

    if (res.ok) {
      bootstrap.Modal.getInstance(document.getElementById('schedulePickerModal')).hide()
      alert('일정이 저장되었습니다! 📅 내 일정 페이지에서 확인하세요.')
    } else {
      const err = await res.json().catch(() => ({}))
      alert(err.message || '저장에 실패했습니다.')
    }
  } catch (e) {
    alert('서버에 연결할 수 없습니다.')
  }
}

// ────────────────────────────────
// 언어 전환 버튼
// ────────────────────────────────
function toggleLanguage() {
  const btn = document.getElementById('lang-btn')

  if (currentLang === 'ko') {
    // 영어로 전환
    currentLang = 'en'
    btn.textContent = '한국어'
    btn.classList.remove('btn-outline-light')
    btn.classList.add('btn-light')
  } else {
    // 한국어로 전환
    currentLang = 'ko'
    btn.textContent = 'English'
    btn.classList.remove('btn-light')
    btn.classList.add('btn-outline-light')
  }

  // 언어 바꾸면 첫 페이지부터 목록 새로고침
  currentPage = 0
  loadFestivals()
}

// ────────────────────────────────
// 로그인/회원가입 모달
// ────────────────────────────────
function openLoginModal() {
  new bootstrap.Modal(document.getElementById('authModal')).show()
}

// 로그인 ↔ 회원가입 탭 전환
function switchTab(tab, btn) {
  currentTab = tab

  document.querySelectorAll('#auth-tabs .nav-link').forEach(el => el.classList.remove('active'))
  btn.classList.add('active')

  document.getElementById('nickname-field').style.display = tab === 'signup' ? 'block' : 'none'
  document.getElementById('auth-submit').textContent = tab === 'login' ? '로그인' : '회원가입'
  document.getElementById('auth-error').textContent  = ''
}

// 로그인/회원가입 폼 제출
async function handleAuth(e) {
  e.preventDefault()

  const email    = document.getElementById('auth-email').value
  const password = document.getElementById('auth-password').value
  const nickname = document.getElementById('auth-nickname').value

  const url  = currentTab === 'login' ? '/api/auth/login' : '/api/auth/signup'
  const body = currentTab === 'login'
    ? { email, password }
    : { email, password, nickname }

  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    })

    if (!res.ok) {
      const err = await res.json().catch(() => ({}))
      document.getElementById('auth-error').textContent = err.message || '오류가 발생했습니다.'
      return
    }

    const data = await res.json()
    currentUser = data
    updateAuthUI()

    bootstrap.Modal.getInstance(document.getElementById('authModal')).hide()
  } catch (e) {
    document.getElementById('auth-error').textContent = '서버에 연결할 수 없습니다.'
  }
}

// 로그아웃
async function handleLogout() {
  await fetch('/api/auth/logout', { method: 'POST' })
  currentUser = null
  updateAuthUI()
}

// 헤더 로그인 상태 UI 업데이트
function updateAuthUI() {
  const authArea = document.getElementById('auth-area')
  if (currentUser) {
    authArea.innerHTML = `
      <span class="text-light me-2">${currentUser.nickname}님</span>
      <button class="btn btn-outline-light btn-sm" onclick="handleLogout()">로그아웃</button>
    `
  } else {
    authArea.innerHTML = `
      <button class="btn btn-outline-light btn-sm" onclick="openLoginModal()">로그인</button>
    `
  }
}

// ────────────────────────────────
// 날짜 유틸 함수들
// ────────────────────────────────

// 화면 표시용: "20250501" → "2025년 5월 1일"
function formatDate(str) {
  if (!str || str.length !== 8) return ''
  return str.slice(0, 4) + '년 ' + parseInt(str.slice(4, 6)) + '월 ' + parseInt(str.slice(6, 8)) + '일'
}

// FullCalendar용: "20250501" → "2025-05-01"
function toCalendarDate(str) {
  if (!str || str.length !== 8) return null
  return str.slice(0, 4) + '-' + str.slice(4, 6) + '-' + str.slice(6, 8)
}

// FullCalendar 종료일 계산: 하루 더하기 (종료일은 exclusive)
// "20250503" → "2025-05-04"
function addOneDay(str) {
  if (!str || str.length !== 8) return null
  const d = new Date(
    parseInt(str.slice(0, 4)),
    parseInt(str.slice(4, 6)) - 1,
    parseInt(str.slice(6, 8))
  )
  d.setDate(d.getDate() + 1)
  return d.getFullYear() + '-' +
    String(d.getMonth() + 1).padStart(2, '0') + '-' +
    String(d.getDate()).padStart(2, '0')
}
