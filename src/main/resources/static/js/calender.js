document.addEventListener('DOMContentLoaded', async function () {
    const calendarEl = document.getElementById('calendar');
    const accessToken = localStorage.getItem("accessToken");
    let userRole = null;

    //캘린더 display를 위해 날짜 1 추가
    function addOneDay(dateStr) {
        const date = new Date(dateStr);
        date.setDate(date.getDate() + 1);
        return date.toISOString().split('T')[0];
    }

    //유저 정보 불러오기
    if (accessToken) {
        try {
            const res = await fetch("/api/user/info", {
                headers: {
                    "Authorization": `Bearer ${accessToken}`
                }
            });
            if (res.ok) {
                const user = await res.json();
                userRole = user.role;
            }
        } catch (e) {
            console.error("유저 정보 불러오기 실패", e);
        }
    }

    //캘린더 정보 설정
    const calendar = new FullCalendar.Calendar(calendarEl, {
        plugins: ['interaction', 'dayGrid', 'timeGrid', 'list'],
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek'
        },
        events: '/api/schedules'
    });

    //ADMIN만 일정 추가 버튼 노출
    if (userRole === "ROLE_ADMIN") {
        const btn = document.getElementById("add-schedule-btn");
        document.getElementById("calendar-top-bar").style.display = "block";

        btn.addEventListener("click", async () => {
            const title = prompt("일정 제목:");
            const start = prompt("시작일 (YYYY-MM-DD):");
            let end = prompt("종료일 (선택):");

            const rawEnd = end;  // 사용자가 입력한 값 저장
            const displayEnd = end ? addOneDay(end) : null;

            const res = await fetch('/api/schedules', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${accessToken}`
                },
                body: JSON.stringify({ title, start, end: rawEnd })
            });

            if (res.ok) {
                alert("일정이 추가되었습니다.");
                location.reload();
            } else {
                alert("추가 실패: 권한 없음 또는 서버 오류");
            }
        });
    }

    calendar.render();
});