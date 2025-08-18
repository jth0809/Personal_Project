---

```md
# 08_qna_and_reviews.md

## 목적

상품 Q&A/리뷰 목록·작성·수정·삭제 플로우 정의.

## 백엔드 API (컨트롤러 기준)

- QnA(Base `/qna`)
  - 목록(상품별): `GET /qna/product/{productId}`
  - 질문 작성: `POST /qna/questions` (인증 필요)
  - 답변 등록: `PUT /qna/{qnaId}/answers` (권한 필요)
  - 삭제: `DELETE /qna/{qnaId}`
- 리뷰(Base `/reviews`)
  - 목록(상품별): `GET /reviews/product/{productId}`
  - 수정: `PUT /reviews/{reviewId}`
  - 삭제: `DELETE /reviews/{reviewId}`
  - (작성 엔드포인트는 소스 생략 구간 — 존재 시 동일 패턴 적용)

## 프론트 구현 포인트

- 상품 상세에서 탭으로 Q&A/리뷰 노출
- 작성/수정/삭제는 인증 필요 → 토큰 체크
- 본인 글만 수정/삭제 버튼 노출

## 완료 기준

- 권한/유효성 에러 처리
- 목록 새로고침/캐시 무효화
```
