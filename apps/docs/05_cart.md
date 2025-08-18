---

````md
# 05_cart.md

## 목적

장바구니 조회, 추가, 삭제, 수량변경 등 연동과 낙관적 업데이트 전략.

## 백엔드 API (컨트롤러 기준)

- Base: `/cart`
- 추가: `POST /cart/items`
- 삭제: `DELETE /cart/items/{itemId}`
- (조회/수정 엔드포인트도 존재 가능 — 일부 소스 출력 생략되어 있으나 일반적으로 `GET /cart`, `PUT /cart/items/{itemId}` 형태. 실제 컨트롤러 확정 후 맞춤)

## 프론트 구현 제안

- **조회**: `GET /cart` 로 사용자 장바구니 라인아이템 수신
- **추가**: 낙관적 업데이트 → 실패 시 롤백
- **수정/삭제**: 동일 패턴

```ts
// apps/frontend/src/hooks/useCart.ts (개념 예시)
const fetchCart = () => apiFetch<CartResponse>(`/cart`);

const addToCart = (data: { productId: number; quantity: number }) =>
  apiFetch<CartResponse>(`/cart/items`, {
    method: "POST",
    body: JSON.stringify(data),
  });

const removeFromCart = (itemId: number) =>
  apiFetch<CartResponse>(`/cart/items/${itemId}`, { method: "DELETE" });
```
````

상태 관리

서버 소스가 단일 진실. zustand는 UI 동기화/낙관적 업데이트에만 사용

완료 기준

로그인 사용자만 장바구니 접근 가능

네트워크/권한 에러 UX 반영
