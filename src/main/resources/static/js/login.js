document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("form");
    if (!form) return;

    form.addEventListener("submit", async e => {
        e.preventDefault();

        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        const res = await fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: email, password: password })
        });

        if (res.ok) {
            const data = await res.json();
            localStorage.setItem("accessToken", data.accessToken);
            localStorage.setItem("refreshToken", data.refreshToken);
            localStorage.setItem("nickname", data.nickname);  // 이 부분 꼭 필요
            location.href = "/";
        } else {
            const errorMessage = await res.text();  // 서버에서 전달된 에러 메시지 받기
            alert(errorMessage);  // 예: "존재하지 않는 사용자입니다."
        }
    });
});