// ===== FullCalendar 상태 =====
let fcCalendar = null;      // FullCalendar 인스턴스
let mySchedules = [];       // 서버에서 불러온 내 일정 목록
let selectedDateStr = '';   // 현재 선택된 날짜 (예: "2026-06-08")

// ===== 달력 모달 열기 =====
function openCalendar() {
    if (!currentUser) {
        alert('로그인이 필요합니다.');
        openModal('login-modal');
        return;
    }
    openModal('calendar-modal');

    // 모달이 화면에 완전히 그려진 뒤 FullCalendar 초기화
    // (브라우저가 display:flex 레이아웃을 계산할 시간을 줘야 함)
    setTimeout(() => {
        if (!fcCalendar) {
            initFullCalendar();
        } else {
            fcCalendar.updateSize(); // 모달 크기에 맞게 재조정
        }
        loadMySchedules();
    }, 150);
}

// ===== FullCalendar 초기화 (최초 1회만 실행) =====
function initFullCalendar() {
    const calEl = document.getElementById('fc-calendar');

    fcCalendar = new FullCalendar.Calendar(calEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',          // 한국어
        height: 'auto',
        headerToolbar: {
            left:   'prev,next today',
            center: 'title',
            right:  ''
        },
        buttonText: {
            today: '오늘'
        },
        eventColor:     '#5B6CEA', // 일정 막대 색상
        eventTextColor: '#ffffff',

        // 날짜 칸 클릭 → 해당 날짜 일정 보여주기
        dateClick: function(info) {
            selectCalDate(info.dateStr);
        },

        // 일정 이벤트 클릭 → 그 날짜 일정 보여주기
        eventClick: function(info) {
            selectCalDate(info.event.startStr);
        }
    });

    fcCalendar.render();
}

// ===== 서버에서 내 일정 불러오기 =====
function loadMySchedules() {
    fetch('/api/schedules')
        .then(r => r.json())
        .then(data => {
            mySchedules = data;
            refreshCalendarEvents();
            // 현재 선택된 날짜가 있으면 목록 갱신
            if (selectedDateStr) renderDayPanel(selectedDateStr);
        })
        .catch(() => {
            mySchedules = [];
        });
}

// ===== FullCalendar 이벤트 목록 갱신 =====
function refreshCalendarEvents() {
    if (!fcCalendar) return;

    // 기존 이벤트 모두 제거 후 다시 추가
    fcCalendar.removeAllEvents();
    mySchedules.forEach(s => {
        fcCalendar.addEvent({
            id:    String(s.id),
            title: s.title,
            start: s.travelDate, // "2026-06-08" 형식
            allDay: true
        });
    });
}

// ===== 날짜 선택 → 사이드 패널 갱신 =====
function selectCalDate(dateStr) {
    selectedDateStr = dateStr;
    renderDayPanel(dateStr);
}

// ===== 사이드 패널 내용 그리기 =====
function renderDayPanel(dateStr) {
    const panel   = document.getElementById('cal-panel');
    const [y, m, d] = dateStr.split('-');
    const dateLabel = `${y}년 ${parseInt(m)}월 ${parseInt(d)}일`;

    // 해당 날짜의 일정만 필터링
    const daySchedules = mySchedules.filter(s => s.travelDate === dateStr);

    // 일정 목록 HTML
    let schedulesHtml = '';
    if (daySchedules.length === 0) {
        schedulesHtml = '<p class="sch-no-places" style="margin-bottom:12px;">이 날 일정이 없습니다.</p>';
    } else {
        schedulesHtml = daySchedules.map(s => `
            <div class="sch-item">
                <div class="sch-item-header">
                    <span class="sch-item-title" onclick="refreshScheduleDetail(${s.id})">📋 ${s.title}</span>
                    <button class="sch-item-del" onclick="deleteSchedule('${dateStr}', ${s.id})">🗑️</button>
                </div>
                ${s.memo ? `<p class="sch-item-memo">${s.memo}</p>` : ''}
                <div id="sch-places-${s.id}">
                    ${renderPlacesHtml(s.items || [])}
                </div>
            </div>
        `).join('');
    }

    // 패널 전체 교체
    panel.innerHTML = `
        <p class="detail-section-title">${dateLabel}</p>

        <!-- 일정 목록 -->
        <div id="cal-schedule-list">${schedulesHtml}</div>

        <!-- 새 일정 추가 폼 -->
        <div class="cal-add-form">
            <div class="form-group">
                <label>일정 이름</label>
                <input type="text" id="sch-title" placeholder="예: 부산 여행">
            </div>
            <div class="form-group">
                <label>메모 (선택)</label>
                <input type="text" id="sch-memo" placeholder="간단한 메모">
            </div>
            <button class="form-submit" onclick="createSchedule()">+ 일정 추가</button>
            <p class="form-msg" id="sch-msg"></p>
        </div>
    `;
}

// ===== 장소 목록 HTML 생성 =====
function renderPlacesHtml(items) {
    if (items.length === 0) {
        return '<p class="sch-no-places">관광지 상세에서 장소를 추가하세요.</p>';
    }
    return items.map((item, idx) => `
        <div class="sch-place">
            <span class="sch-place-order">${idx + 1}</span>
            <div class="sch-place-info">
                <strong>${item.title}</strong>
                ${item.visitTime ? `<span class="sch-place-time">⏰ ${item.visitTime}</span>` : ''}
                ${item.addr     ? `<p class="sch-place-addr">📍 ${item.addr}</p>`           : ''}
                ${item.memo     ? `<p class="sch-place-memo">💬 ${item.memo}</p>`           : ''}
            </div>
            <button class="sch-place-del"
                    onclick="deleteScheduleItem(${item.scheduleId}, ${item.id})">✕</button>
        </div>
    `).join('');
}

// ===== 일정 상세 (장소 포함) 새로 불러오기 =====
function refreshScheduleDetail(scheduleId) {
    fetch(`/api/schedules/${scheduleId}`)
        .then(r => r.json())
        .then(data => {
            // 로컬 목록 업데이트
            const idx = mySchedules.findIndex(s => s.id === scheduleId);
            if (idx !== -1) mySchedules[idx] = data;
            // 해당 일정 장소 목록만 갱신
            const placesEl = document.getElementById(`sch-places-${scheduleId}`);
            if (placesEl) placesEl.innerHTML = renderPlacesHtml(data.items || []);
        });
}

// ===== 새 일정 생성 =====
function createSchedule() {
    const titleInput = document.getElementById('sch-title');
    const memoInput  = document.getElementById('sch-memo');
    const msgEl      = document.getElementById('sch-msg');

    const title = titleInput ? titleInput.value.trim() : '';
    const memo  = memoInput  ? memoInput.value.trim()  : '';

    if (!title) {
        msgEl.className = 'form-msg error';
        msgEl.textContent = '일정 이름을 입력하세요.';
        return;
    }
    if (!selectedDateStr) {
        msgEl.className = 'form-msg error';
        msgEl.textContent = '달력에서 날짜를 선택하세요.';
        return;
    }

    fetch('/api/schedules', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title, travelDate: selectedDateStr, memo })
    })
    .then(r => r.json())
    .then(data => {
        data.items = [];
        mySchedules.push(data);
        refreshCalendarEvents();          // 달력에 이벤트 추가
        renderDayPanel(selectedDateStr);  // 사이드 패널 갱신
    })
    .catch(() => {
        const msg = document.getElementById('sch-msg');
        if (msg) { msg.className = 'form-msg error'; msg.textContent = '일정 추가에 실패했습니다.'; }
    });
}

// ===== 일정 삭제 =====
function deleteSchedule(dateStr, scheduleId) {
    if (!confirm('일정을 삭제하시겠습니까?')) return;

    fetch(`/api/schedules/${scheduleId}`, { method: 'DELETE' })
        .then(r => r.json())
        .then(() => {
            mySchedules = mySchedules.filter(s => s.id !== scheduleId);
            refreshCalendarEvents();     // 달력에서 이벤트 제거
            renderDayPanel(dateStr);     // 사이드 패널 갱신
        });
}

// ===== 장소 삭제 =====
function deleteScheduleItem(scheduleId, itemId) {
    fetch(`/api/schedules/${scheduleId}/items/${itemId}`, { method: 'DELETE' })
        .then(r => r.json())
        .then(() => {
            // 로컬 목록에서 장소 제거
            const sch = mySchedules.find(s => s.id === scheduleId);
            if (sch) sch.items = (sch.items || []).filter(i => i.id !== itemId);
            // 장소 목록 영역만 갱신
            const placesEl = document.getElementById(`sch-places-${scheduleId}`);
            if (placesEl) placesEl.innerHTML = renderPlacesHtml(sch ? sch.items : []);
        });
}

// ===== 관광지 상세 모달 → "일정에 추가" 버튼 =====
function openAddToSchedule() {
    if (!currentUser) {
        alert('로그인이 필요합니다.');
        return;
    }
    const panel = document.getElementById('add-to-sch-panel');
    // 토글: 열려있으면 닫기
    if (panel.style.display === 'block') {
        panel.style.display = 'none';
        return;
    }
    panel.style.display = 'block';

    // 최신 일정 목록 불러오기
    fetch('/api/schedules')
        .then(r => r.json())
        .then(data => {
            mySchedules = data;
            const listEl = document.getElementById('sch-select-list');
            if (mySchedules.length === 0) {
                listEl.innerHTML = '<p style="font-size:13px;color:var(--text-muted);padding:8px 0;">달력에서 먼저 일정을 만들어주세요.</p>';
                return;
            }
            listEl.innerHTML = mySchedules.map(s =>
                `<button class="sch-select-btn" onclick="addSpotToSchedule(${s.id})">
                    ${s.travelDate} · ${s.title}
                 </button>`
            ).join('');
        });
}

// ===== 현재 관광지를 선택한 일정에 추가 =====
function addSpotToSchedule(scheduleId) {
    if (!currentSpot) return;

    fetch(`/api/schedules/${scheduleId}/items`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            contentId: currentSpot.contentid,
            title:     currentSpot.title,
            addr:      currentSpot.addr1 || '',
            visitTime: '',
            memo:      ''
        })
    })
    .then(r => r.json())
    .then(() => fetch(`/api/schedules/${scheduleId}`).then(r => r.json()))
    .then(data => {
        // 로컬 목록 업데이트
        const idx = mySchedules.findIndex(s => s.id === scheduleId);
        if (idx !== -1) mySchedules[idx] = data;
        alert(`"${currentSpot.title}"이(가) 일정에 추가되었습니다!`);
        document.getElementById('add-to-sch-panel').style.display = 'none';
    })
    .catch(() => alert('추가에 실패했습니다.'));
}
