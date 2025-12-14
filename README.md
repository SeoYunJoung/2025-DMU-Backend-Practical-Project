# Backend-Practical-Project
동양미래대학교 3학년 2학기 백엔드 실무 프로젝트 실습 파일 백업
<img width="1162" height="781" alt="image" src="https://github.com/user-attachments/assets/375bc0c3-cd79-4bc2-abc9-3ccb29434d2b" />
<img width="1048" height="673" alt="image" src="https://github.com/user-attachments/assets/af22ca7c-18ab-4db2-bdf9-3a4abad67b86" />
<img width="1015" height="663" alt="image" src="https://github.com/user-attachments/assets/07ad2a57-149a-4dfb-b0a0-1ac8a99696ce" />
<img width="1042" height="671" alt="image" src="https://github.com/user-attachments/assets/c4440d29-2932-4319-84c4-9dba21e01878" />
<img width="1045" height="675" alt="image" src="https://github.com/user-attachments/assets/b528e9d4-6020-4a87-bde9-bc1fc0c1f11f" />


## 💾 데이터베이스 스키마 구조

본 프로젝트는 사용자 관리, 인증, 댓글, 일정 관리를 위한 4개의 주요 테이블로 구성되어 있습니다.

### 1. `users` 테이블 (사용자 정보)

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `AI`, `PK` | 사용자 고유 식별자 (Primary Key) |
| `email` | `varchar(255)` | | 사용자 이메일 (로그인 ID) |
| `name` | `varchar(255)` | | 사용자 이름 |
| `password` | `varchar(255)` | | 해시된 사용자 비밀번호 |
| `role` | `enum('ROLE_ADMIN','ROLE_USER')` | | 사용자 권한 (관리자 또는 일반 사용자) |

---

### 2. `refresh_tokens` 테이블 (리프레시 토큰 관리)

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `AI`, `PK` | 토큰 고유 식별자 (Primary Key) |
| `email` | `varchar(255)` | | 해당 토큰을 소유한 사용자 이메일 |
| `refresh_token` | `varchar(255)` | | 실제 리프레시 토큰 값 |

---

### 3. `comment` 테이블 (댓글 관리)

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `AI`, `PK` | 댓글 고유 식별자 (Primary Key) |
| `content` | `text` | | 댓글 내용 |
| `created_at` | `datetime(6)` | | 댓글 작성 시간 |
| `parent_id` | `bigint` | | 부모 댓글 ID (대댓글 구현 시 사용) |
| `user_id` | `bigint` | `FK` | 댓글 작성자 (users.id 참조) |

---

### 4. `schedule` 테이블 (일정 관리)

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- |
| `id` | `bigint` | `AI`, `PK` | 일정 고유 식별자 (Primary Key) |
| `end` | `date` | | 일정 종료일 |
| `start` | `date` | | 일정 시작일 |
| `title` | `varchar(255)` | | 일정 제목 |
