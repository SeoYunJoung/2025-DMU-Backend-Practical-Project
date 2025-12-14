// 헤더의 profile 메뉴를 유동적으로 숨기고 보이게 하는 로직
document.addEventListener("DOMContentLoaded", () => {
    const nickname = localStorage.getItem("nickname");

    const profileMenu = document.getElementById("profile");

    if (profileMenu) {
        if (nickname) {
            profileMenu.style.display = "inline";
        } else {
            profileMenu.style.display = "none";
        }
    }

    const logoutButton = document.getElementById("logoutBtn");
    if (logoutButton) {
        logoutButton.addEventListener("click", () => {
            localStorage.clear();
            location.href = "/";
        });
    }
});

//로그아웃 로직 구현
document.addEventListener("DOMContentLoaded", () => {
    const nickname = localStorage.getItem("nickname");
    const profileMenu = document.getElementById("profile");
    const loginBtn = document.getElementById("loginBtn");

    // 프로필 메뉴 보이기/숨기기
    if (profileMenu) {
        profileMenu.style.display = nickname ? "inline" : "none";
    }

    // 로그인/로그아웃 버튼 동작 전환
    if (loginBtn) {
        if (nickname) {
            loginBtn.textContent = "로그아웃";
            loginBtn.href = "#";

            loginBtn.addEventListener("click", async (e) => {
                e.preventDefault();

                const confirmed = confirm("로그아웃 하시겠습니까?");
                if (!confirmed) return;

                const refreshToken = localStorage.getItem("refreshToken");
                if (refreshToken) {
                    try {
                        const res = await fetch("/api/auth/logout", {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                                "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
                            },
                            body: JSON.stringify({
                                refreshToken: localStorage.getItem("refreshToken")
                            })
                        });

                        if (res.ok) {
                            alert("정상적으로 로그아웃되었습니다.");
                            localStorage.clear();
                            location.href = "/";
                        } else {
                            const msg = await res.text();
                            alert("로그아웃 중 오류가 발생했습니다. 다시 시도해주세요.");
                        }
                    } catch (err) {
                        alert("로그아웃 요청 중 오류 발생");
                    }
                } else {
                    // 토큰이 없으면 그냥 클라이언트만 초기화
                    localStorage.clear();
                    location.href = "/";
                }
            });
        } else {
            // 로그인 상태가 아닐 때는 기존처럼 로그인 이동
            loginBtn.textContent = "로그인";
            loginBtn.href = "/login";
        }
    }
});