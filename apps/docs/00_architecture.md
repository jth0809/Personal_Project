# 00_architecture.md

## 목적

프론트엔드(Next.js)와 백엔드(Spring Boot) 간 연동 전반의 구조·원칙을 정의한다.

## 현재 구조 개요

- Monorepo: TurboRepo
- Frontend: `apps/frontend` (Next.js 15 / React 19 / React Query / Zustand)
- Backend: `apps/backend` (Spring Boot 3.5 / JWT / Spring Security / springdoc-openapi)
- 공통: `packages/*` (eslint-config, tsconfig, ui 등)

## 통신 방식을 위한 선택지

1. **프록시(rewrite) 방식**
   - Next.js에서 `/api/*`를 백엔드로 프록시 → CORS 이슈 최소화
   - 장점: 브라우저에서의 CORS 제어 불필요, 단일 오리진처럼 동작
2. **직접 호출 방식**
   - `NEXT_PUBLIC_API_BASE_URL`로 백엔드 직접 호출
   - 장점: 배포 환경 분리 명확, 단점: 백엔드 CORS 설정 필요

본 프로젝트는 프론트에 이미 `/api/...` 주석이 다수 존재하므로 **① 프록시 우선**을 기본 원칙으로 한다. (단, 운영환경에서 API 게이트웨이 등 인프라 상황에 따라 ②로 전환 가능)

## 인증 정책

- 로그인 성공 시 **Access Token**(JWT)만 반환 (백엔드 `UserDto.TokenResponse.accessToken`)
- 보호 리소스 요청 시 `Authorization: Bearer <token>`
- 백엔드 보안 규칙(요약):
  - 공개: `/auth/**`, `GET /products/**`, `GET /categories/**`, `GET /qna/**`, `GET /reviews/**`
  - 그 외는 인증 필요

## 페이지네이션/정렬

- 백엔드는 Spring `Page<T>` 응답 사용
- 요청 파라미터: `page`, `size`, `sortBy`, `sortOrder`(ASC|DESC)
- 기본 정렬: `id DESC` (백엔드 `PageableDto` 참고)

## 결제

- 결제 확인: `POST /payments/confirm-toss`
- Request: `provider`, `paymentKey`, `orderId`, `amount`
- Response: `status`, `orderId`, `totalAmount`

## 용어

- **API 모델**: 백엔드 DTO 스키마
- **UI 모델**: 프론트에서 화면 표현용 가공 모델(어댑터로 변환)
