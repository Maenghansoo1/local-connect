// 현재 로그인한 유저 정보
let currentUser = null

// 페이지가 열리면 실행
document.addEventListener('DOMContentLoaded', function () {
  checkLoginStatus()
})

// ────────────────────────────────
// 로그인 상태 확인
// ────────────────────────────────
async function checkLoginStatus() {
  try {
    const res = await fetch('/api/auth/me')
    if (res.ok) {
      const data = await res.json()
      currentUser = data
      updateAuthUI()
      loadSchedules() // 로그인 됐을 때만 일정 불러오기
    } else {
      // 로그인 안 된 경우
      document.getElementById('login-required').style.display = 'block'
      document.getElementById('calendar-wrapper').style.display = 'none'
      document.getElementById('schedule-list').textContent = ''
      updateAuthUI()
    }
  } catch (e) {
    document.getElementById('login-required').style.display = 'block'
  }
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
      <a href="/" class="btn btn-outline-light btn-sm">로그인하러 가기</a>
    `
  }
}

// 로그아웃
async function handleLogout() {
  await fetch('/api/auth/logout', { method: 'POST' })
  currentUser = null
  window.location.href = '/'
}

// ────────────────────────────────
// 일정 불러오기
// ────────────────────────────────
async function loadSchedules() {
  try {
    const res = await fetch('/api/schedules')
    const schedules = await res.json()

    renderCalendar(schedules)
    renderScheduleList(schedules)
  } catch (e) {
    document.getElementById('schedule-list').innerHTML =
      '<p class="text-danger">일정을 불러올 수 없습니다.</p>'
  }
}

// ────────────────────────────────
// FullCalendar로 달력 그리기
// ────────────────────────────────
function renderCalendar(schedules) {
  // 일정을 FullCalendar가 이해하는 형식으로 변환
  const calendarEvents = schedules.map(function (s) {
    return {
      id: s.id,
      title: s.eventTitle,
      start: toCalendarDate(s.startDate),
      // FullCalendar는 종료일을 "마지막 날 + 1일"로 계산하므로 하루 더함
      end: addOneDay(s.endDate || s.startDate),
      color: '#0d6efd'
    }
  })

  const calendarEl = document.getElementById('calendar')
  const calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: 'dayGridMonth',
    locale: 'ko',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,listMonth'
    },
    events: calendarEvents,
    // 달력에서 일정 클릭하면 목록으로 스크롤
    eventClick: function (info) {
      const targetId = 'schedule-item-' + info.event.id
      const target = document.getElementById(targetId)
      if (target) {
        target.scrollIntoView({ behavior: 'smooth' })
      }
    }
  })

  calendar.render()
}

// ────────────────────────────────
// 저장된 일정 목록 카드 그리기
// ────────────────────────────────
function renderScheduleList(schedules) {
  const container = document.getElementById('schedule-list')

  if (schedules.length === 0) {
    container.innerHTML = '<p class="text-muted">저장된 일정이 없습니다. 축제 목록에서 일정을 추가해보세요!</p>'
    return
  }

  let html = ''
  for (let i = 0; i < schedules.length; i++) {
    const s = schedules[i]
    html += `
      <div class="card mb-3" id="schedule-item-${s.id}">
        <div class="row g-0">
          ${s.imageUrl
            ? `<div class="col-md-2">
                 <img src="${s.imageUrl}" class="img-fluid rounded-start h-100" style="object-fit:cover; max-height:120px;" alt="${s.eventTitle}">
               </div>`
            : ''
          }
          <div class="${s.imageUrl ? 'col-md-10' : 'col-12'}">
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-start">
                <div>
                  <span class="badge bg-primary mb-1">${s.region}</span>
                  <h5 class="card-title mb-1">${s.eventTitle}</h5>
                  <p class="card-text text-muted mb-0">
                    📅 ${formatDate(s.startDate)}${s.endDate && s.endDate !== s.startDate ? ' ~ ' + formatDate(s.endDate) : ''}
                  </p>
                </div>
                <button class="btn btn-outline-danger btn-sm ms-2" onclick="deleteSchedule(${s.id})">삭제</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    `
  }
  container.innerHTML = html
}

// ────────────────────────────────
// 일정 삭제
// ────────────────────────────────
async function deleteSchedule(scheduleId) {
  if (!confirm('이 일정을 삭제하시겠습니까?')) return

  try {
    const res = await fetch('/api/schedules/' + scheduleId, { method: 'DELETE' })
    if (res.ok) {
      alert('일정이 삭제되었습니다.')
      loadSchedules() // 목록 새로고침
    } else {
      alert('삭제에 실패했습니다.')
    }
  } catch (e) {
    alert('서버에 연결할 수 없습니다.')
  }
}

// ────────────────────────────────
// 날짜 유틸 함수들
// ────────────────────────────────

// FullCalendar용: "20250501" → "2025-05-01"
function toCalendarDate(str) {
  if (!str || str.length !== 8) return null
  return str.slice(0, 4) + '-' + str.slice(4, 6) + '-' + str.slice(6, 8)
}

// FullCalendar 종료일용: 하루 더하기 (종료일은 exclusive)
// "20250503" → "2025-05-04"
function addOneDay(str) {
  if (!str || str.length !== 8) return null
  const year  = parseInt(str.slice(0, 4))
  const month = parseInt(str.slice(4, 6)) - 1 // JS Date는 월이 0부터 시작
  const day   = parseInt(str.slice(6, 8))
  const d = new Date(year, month, day)
  d.setDate(d.getDate() + 1)
  const y  = d.getFullYear()
  const m  = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return y + '-' + m + '-' + dd
}

// 화면 표시용: "20250501" → "2025년 5월 1일"
function formatDate(str) {
  if (!str || str.length !== 8) return ''
  return str.slice(0, 4) + '년 ' + parseInt(str.slice(4, 6)) + '월 ' + parseInt(str.slice(6, 8)) + '일'
}
