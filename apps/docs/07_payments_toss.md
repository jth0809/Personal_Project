---

````md
# 07_payments_toss.md

## 목적

토스페이먼츠 승인 확인 API 연동.

## 백엔드 API

- `POST /payments/confirm-toss`
- Request (`PaymentDto.VerificationRequest`):
  - `provider`: 결제사 식별자 (e.g., "toss")
  - `paymentKey`, `orderId`, `amount`
- Response (`PaymentDto.ConfirmationResponse`): `status`, `orderId`, `totalAmount`

## 프론트 결제 플로우(요약)

1. 프론트에서 토스 SDK로 결제 진행 → `paymentKey`, `orderId`, `amount` 확보
2. 백엔드로 승인 확인 요청

```ts
const confirmPayment = (payload: {
  provider: string;
  paymentKey: string;
  orderId: string;
  amount: number;
}) =>
  apiFetch<PaymentConfirmResponse>(`/payments/confirm-toss`, {
    method: "POST",
    body: JSON.stringify(payload),
  });
```
````

오류 처리

금액 불일치, 이미 처리된 결제키, 네트워크 오류 시 사용자 안내 및 재시도/문의 경로 제공

완료 기준

정상 승인/오류 케이스 시나리오별 UX 반영
