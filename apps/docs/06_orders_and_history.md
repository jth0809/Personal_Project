---

````md
# 06_orders_and_history.md

## 목적

주문 생성/상세/이력/취소 플로우 정의.

## 백엔드 API (컨트롤러 기준)

- Base: `/orders`
- 이력: `GET /orders/history` (페이지형일 수 있음 — `PageableDto` 사용)
- 상세: `GET /orders/{orderId}`
- 취소: `DELETE /orders/{orderId}` + body: `{ reason }` (백엔드 `OrderDto.CancelRequest`)

> 주문 생성(`POST /orders`)은 서비스/DTO에 정의되어 있을 가능성이 큼(소스 부분 생략). 실제 API 시그니처 확인 후 적용.

## 프론트 구현

```ts
// 이력
export const useOrders = (params?: { page?: number; size?: number }) =>
  useQuery({
    queryKey: ["orders", params],
    queryFn: () => apiFetch<Page<OrderHistoryResponse>>(`/orders/history`),
  });

// 상세
export const useOrder = (orderId: number) =>
  useQuery({
    queryKey: ["order", orderId],
    queryFn: () => apiFetch<OrderDetailResponse>(`/orders/${orderId}`),
  });

// 취소
export const useCancelOrder = () =>
  useMutation({
    mutationFn: (args: { orderId: number; reason: string }) =>
      apiFetch<OrderHistoryResponse>(`/orders/${args.orderId}`, {
        method: "DELETE",
        body: JSON.stringify({ reason: args.reason }),
      }),
  });
```
````

완료 기준

주문 이력 페이징

취소 사유 전송 및 결과 반영
