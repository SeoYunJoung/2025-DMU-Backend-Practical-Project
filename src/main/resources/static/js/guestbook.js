document.addEventListener("DOMContentLoaded", async () => {
    const container = document.getElementById("comments-container");
    const commentForm = document.getElementById("main-comment-form");
    const accessToken = localStorage.getItem("accessToken");
    const nickname = localStorage.getItem("nickname");

    let role = null;

    // 유저 정보 조회 (로그인한 경우)
    if (accessToken) {
        try {
            const res = await fetch("/api/user/info", {
                headers: { "Authorization": `Bearer ${accessToken}` }
            });
            if (res.ok) {
                const user = await res.json();
                role = user.role;
            }
        } catch (e) {
            console.error("유저 정보 조회 실패", e);
        }
    }

    // 비로그인 상태면 댓글 작성 폼 숨김
    if (!accessToken && commentForm) {
        commentForm.style.display = "none";
    }

    // 댓글 불러오기
    async function loadComments() {
        try {
            const headers = accessToken
                ? { "Authorization": `Bearer ${accessToken}` }
                : {};

            const res = await fetch("/api/comments", {
                method: "GET",
                headers
            });

            if (!res.ok) {
                console.error("댓글 불러오기 실패", await res.text());
                return;
            }

            const comments = await res.json();
            container.innerHTML = "";
            comments.forEach(comment => {
                const element = renderComment(comment);
                container.appendChild(element);
            });
        } catch (e) {
            console.error("댓글 불러오기 중 에러", e);
        }
    }

    // 댓글 작성
    const submitBtn = document.getElementById("main-comment-submit");
    if (submitBtn) {
        submitBtn.addEventListener("click", async () => {
            const content = document.getElementById("main-comment-input").value.trim();
            if (!content) return;

            const res = await fetch("/api/comments", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${accessToken}`
                },
                body: JSON.stringify({ content })
            });

            if (!res.ok) {
                console.error("댓글 등록 실패", await res.text());
                return;
            }

            document.getElementById("main-comment-input").value = "";
            loadComments();
        });
    }

    // 댓글 렌더링
    function renderComment(comment, isReply = false) {
        const box = document.createElement("div");
        box.className = "comment-box";
        if (isReply) box.classList.add("ms-5");

        const alreadyReplied = comment.replies && comment.replies.length > 0;

        box.innerHTML = `
            <div class="d-flex gap-3">
                <img src="https://img.icons8.com/ios-filled/50/000000/user.png" class="user-avatar">
                <div class="flex-grow-1">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <h6 class="mb-0">${comment.userName}</h6>
                        <span class="comment-time">${comment.createdAt}</span>
                    </div>
                    <p class="mb-2">${comment.content}</p>
                    <div class="comment-actions">
                        ${
            role === "ROLE_ADMIN" && !isReply && !alreadyReplied
                ? `<a href="#" class="reply-btn" data-id="${comment.id}"><i class="bi bi-reply"></i> Reply</a>`
                : ""
        }
                    </div>
                    <div class="reply-form mt-2"></div>
                </div>
            </div>
        `;

        // 대댓글 렌더링
        if (comment.replies && comment.replies.length > 0) {
            const replyContainer = document.createElement("div");
            replyContainer.classList.add("reply-section", "mt-3");
            comment.replies.forEach(reply => {
                replyContainer.appendChild(renderComment(reply, true));
            });
            box.appendChild(replyContainer);
        }

        // apply (reply) 버튼 동작
        const replyBtn = box.querySelector(".reply-btn");
        if (replyBtn) {
            replyBtn.addEventListener("click", e => {
                e.preventDefault();
                const replyForm = box.querySelector(".reply-form");
                if (replyForm.hasChildNodes()) return;

                replyForm.innerHTML = `
                    <textarea class="form-control reply-input" rows="2" placeholder="Write a reply..."></textarea>
                    <div class="text-end mt-2">
                        <button class="btn btn-sm btn-primary submit-reply">Post Reply</button>
                    </div>
                `;

                replyForm.querySelector(".submit-reply").addEventListener("click", async () => {
                    const replyContent = replyForm.querySelector(".reply-input").value.trim();
                    if (!replyContent) return;

                    if (role === "ROLE_USER") {
                        alert("일반 사용자는 대댓글을 작성할 수 없습니다.");
                        return;
                    }

                    const res = await fetch("/api/comments", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${accessToken}`
                        },
                        body: JSON.stringify({
                            content: replyContent,
                            parentId: comment.id
                        })
                    });

                    if (!res.ok) {
                        console.error("대댓글 등록 실패", await res.text());
                        return;
                    }

                    loadComments();
                });
            });
        }

        return box;
    }

    loadComments();
});