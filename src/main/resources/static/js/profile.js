document.addEventListener("DOMContentLoaded", async () => {
    const nickname = localStorage.getItem("nickname");
    const loginTest = document.getElementById("loginTest");
    const accessToken = localStorage.getItem("accessToken");

    if (!nickname || !accessToken) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }

    // 유저 정보 요청
    const res = await fetch("/api/user/info", {
        headers: {
            "Authorization": `Bearer ${accessToken}`
        }
    });

    if (!res.ok) {
        alert("유저 정보 불러오기 실패");
        return;
    }

    const user = await res.json();
    const role = user.role;

    // 일반 사용자
    if (role !== "ROLE_ADMIN") {
        loginTest.textContent = `환영합니다, ${nickname}님!`;
        return;
    }

    // 관리자 전용 영역 활성화
    document.getElementById("admin-section").style.display = "block";
    if (loginTest) loginTest.style.display = "none";

    // 회원 목록 불러오기
    const userRes = await fetch("/api/auth/admin/users", {
        headers: {
            "Authorization": `Bearer ${accessToken}`
        }
    });

    if (!userRes.ok) {
        alert("회원 목록 불러오기 실패");
        return;
    }

    const users = await userRes.json();
    const tbody = document.querySelector("#user-table tbody");

    users.forEach(user => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${user.id}</td>
            <td>${user.email}</td>
            <td>${user.name}</td>
            <td>${user.role}</td>
            <td><button class="btn btn-sm btn-warning" onclick="editUser(${user.id})">수정</button></td>
            <td><button class="btn btn-sm btn-danger" onclick="deleteUser(${user.id})">삭제</button></td>
        `;
        tbody.appendChild(tr);
    });
});

// 회원 삭제 (관리자용)
async function deleteUser(id) {
    if (!confirm("정말로 삭제하시겠습니까?")) return;

    const accessToken = localStorage.getItem("accessToken");

    const res = await fetch(`/api/auth/admin/users/${id}`, {
        method: "DELETE",
        headers: {
            "Authorization": `Bearer ${accessToken}`
        }
    });

    if (res.ok) {
        alert("삭제되었습니다.");
        location.reload();
    } else {
        alert("삭제 실패");
    }
}

// 회원 수정
async function editUser(id) {
    const newEmail = prompt("새 이메일을 입력하세요:");
    const newName = prompt("새 이름을 입력하세요:");
    const newRole = prompt("새 역할을 입력하세요 (예: ROLE_USER 또는 ROLE_ADMIN):");

    if (!newEmail || !newName || !newRole) {
        alert("모든 값을 입력해야 합니다.");
        return;
    }

    const accessToken = localStorage.getItem("accessToken");

    try {
        const res = await fetch(`/api/auth/admin/users/${id}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${accessToken}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: newEmail,
                name: newName,
                role: newRole
            })
        });

        const msg = await res.text();

        if (res.ok) {
            alert("회원 정보 수정 완료");
            location.reload();
        } else {
            // 이메일 중복 메시지 처리
            if (msg.includes("이미 사용 중인 이메일")) {
                alert("이미 사용 중인 이메일입니다. 다른 이메일을 입력해주세요.");
            } else {
                alert(`수정 실패: ${msg}`);
            }
        }
    } catch (error) {
        console.error("요청 중 오류 발생:", error);
        alert("서버 요청 중 문제가 발생했습니다.");
    }
}

// 회원 탈퇴
async function deleteAccount() {
    const confirmed = confirm("정말로 회원 탈퇴하시겠습니까?");
    if (!confirmed) return;

    const accessToken = localStorage.getItem("accessToken");

    const res = await fetch("/api/auth/withdraw", {
        method: "DELETE",
        headers: {
            "Authorization": `Bearer ${accessToken}`
        }
    });

    if (res.ok) {
        alert("회원 탈퇴가 완료되었습니다.");
        localStorage.clear();
        location.href = "/";
    } else {
        alert("회원 탈퇴 실패");
    }
}