document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("signupForm");
    form.addEventListener("submit", async e => {
        e.preventDefault();

        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        const passwordRepeat = document.getElementById("passwordRepeat").value;
        const name = document.getElementById("name").value;
        const termChecked = document.getElementById("term").checked;

        // 1. 비밀번호 확인
        if (password !== passwordRepeat) {
            alert("비밀번호가 일치하지 않습니다.");
            return;
        }

        // 2. 약관 동의 확인
        if (!termChecked) {
            alert("이용약관에 동의해야 회원가입이 가능합니다.");
            return;
        }

        const res = await fetch("/api/auth/signup", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password, name })
        });

        if (res.ok) {
            alert("회원가입 성공");
            location.href = "/login";
        } else {
            const errorMsg = await res.text();
            alert("회원가입 실패: " + errorMsg);
        }
    });
});