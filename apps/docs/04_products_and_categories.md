---

````md
# 04_products_and_categories.md

## 목적

상품 목록/상세/배송정책, 카테고리 목록 연동.

## 백엔드 API

- `GET /products`
  - 쿼리: `keyword?`, `categoryId?`, `page?`, `size?`, `sortBy?`, `sortOrder?`
  - 응답: `Page<ProductDto.Response>`
- `GET /products/{productId}`
- `GET /products/{productId}/shipping` — `ShippingInfoDto.Response`
- `GET /categories` — `CategoryDto.Response[]`

## 프론트 구현

1. 목록

```ts
// apps/frontend/src/hooks/useProducts.ts
import { apiFetch } from "@/lib/apiClient";
import { Page } from "@/types/api";
import { ProductResponse } from "@/types/apiModels";
import { toUiProduct } from "@/lib/adapters";

export const useProducts = (params?: {
  keyword?: string;
  categoryId?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortOrder?: "ASC" | "DESC";
}) => {
  const qs = new URLSearchParams(
    Object.entries(params || {}).filter(([, v]) => v !== undefined) as any
  ).toString();

  return useQuery({
    queryKey: ["products", params],
    queryFn: async () => {
      const data = await apiFetch<Page<ProductResponse>>(
        `/products${qs ? `?${qs}` : ""}`
      );
      return {
        ...data,
        content: data.content.map(toUiProduct),
      };
    },
    staleTime: 60_000,
  });
};
```
````

2. 상세/배송 정책

export const useProduct = (id: number) =>
useQuery({
queryKey: ["product", id],
queryFn: () => apiFetch<ProductResponse>(`/products/${id}`),
});

export const useShipping = (id: number) =>
useQuery({
queryKey: ["shipping", id],
queryFn: () => apiFetch<ShippingResponse>(`/products/${id}/shipping`),
});

```

```

3. 카테고리

export const useCategories = () =>
useQuery({
queryKey: ["categories"],
queryFn: () => apiFetch<CategoryResponse[]>(`/categories`),
staleTime: 5 \* 60_000,
});
