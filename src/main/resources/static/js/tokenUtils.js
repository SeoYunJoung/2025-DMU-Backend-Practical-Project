export async function fetchWithReissue(url, options = {}) {
    const accessToken = localStorage.getItem("accessToken");

    if (!options.headers) {
        options.headers = {};
    }
    if (accessToken) {
        options.headers['Authorization'] = `Bearer ${accessToken}`;
    }

    let res = await fetch(url, options);
    if (res.status === 401) {
        const success = await reissueTokens();
        if (success) {
            const newAccessToken = localStorage.getItem("accessToken");
            options.headers['Authorization'] = `Bearer ${newAccessToken}`;
            res = await fetch(url, options);
        } else {
            alert("세션이 만료되어 로그아웃됩니다.");
            localStorage.clear();
            location.href = "/login";
        }
    }
    return res;
}

async function reissueTokens() {
    const refreshToken = localStorage.getItem("refreshToken");
    if (!refreshToken) return false;

    const res = await fetch("/api/auth/reissue", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken: refreshToken })
    });

    if (res.ok) {
        const data = await res.json();
        localStorage.setItem("accessToken", data.accessToken);
        localStorage.setItem("refreshToken", data.refreshToken);
        return true;
    }
    return false;
}