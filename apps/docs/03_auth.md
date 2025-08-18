---

````md
# 03_auth.md

## 목적

로그인/회원가입 연동 및 보호 라우트 접근 제어 설계.

## 백엔드 API

- `POST /auth/signup` — `email`, `password`, `name` 등 (정확한 필드: `UserDto.SignupRequest`)
- `POST /auth/login` — `email`, `password` → `{ accessToken }`

## 프론트 구현

1. 훅/스토어 연결
   - `useAuthStore`의 모의 로직 제거, 실제 API 호출로 교체
2. 로그인

```ts
// apps/frontend/src/hooks/useAuth.ts (발췌)
import { apiFetch } from "@/lib/apiClient";
import { TokenResponse } from "@/types/apiModels";

const loginApi = async (data: { email: string; password: string }) =>
  apiFetch<TokenResponse>("/auth/login", {
    method: "POST",
    body: JSON.stringify(data),
  });

// zustand store에서 token/user 세팅
```
````

3. 회원가입

const signupApi = async (data: { email: string; password: string; name: string }) =>
apiFetch<void>("/auth/signup", {
method: "POST",
body: JSON.stringify(data),
});

4. 보호 라우트 가드

토큰 없으면 로그인 페이지로 리다이렉트

401 수신 시 토큰 파기 + 로그인 유도

완료 기준

성공/실패 플로우 토스트/에러 처리

새로고침 후에도 토큰 유지(zustand persist)
