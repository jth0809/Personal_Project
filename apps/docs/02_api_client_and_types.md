---

````md
# 02_api_client_and_types.md

## 목적

프론트 공통 API 클라이언트와 타입 체계를 정의한다.

## 공통 fetch 클라이언트

- 기능: 기본 URL/프록시 사용, JWT 헤더 주입, 에러 표준화

```ts
// apps/frontend/src/lib/apiClient.ts
export async function apiFetch<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const token =
    typeof window !== "undefined"
      ? JSON.parse(localStorage.getItem("auth-storage") || "{}")?.state?.token
      : null;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };

  const res = await fetch(path.startsWith("/api") ? path : `/api${path}`, {
    ...options,
    headers,
  });

  if (!res.ok) {
    const msg = await res.text();
    throw new Error(msg || `HTTP ${res.status}`);
  }
  return res.json() as Promise<T>;
}
```
````

## Spring Page 응답 타입

// apps/frontend/src/types/api.ts
export interface Page<T> {
content: T[];
totalElements: number;
totalPages: number;
size: number;
number: number; // current page
first: boolean;
last: boolean;
sort?: unknown;
pageable?: unknown;
}

## API 모델 타입

// apps/frontend/src/types/apiModels.ts

export interface ProductResponse {
id: number;
name: string;
description: string;
price: number;
stockQuantity: number;
imageUrl: string | null;
categoryName: string | null;
detailContent: string | null;
}

export interface ShippingResponse {
shippingMethod: string | null;
shippingFee: number | null;
freeShippingThreshold: number | null;
estimatedDeliveryDays: string | null;
shippingProvider: string | null;
}

export interface CategoryResponse {
id: number;
name: string;
}

export interface TokenResponse {
accessToken: string;
}

## UI 모델 및 어댑터

// apps/frontend/src/types/product.ts (UI 모델 그대로 유지 가능)
export interface Product {
id: string;
name: string;
price: number;
originalPrice?: number;
image: string;
category: string;
rating?: number;
reviews?: number;
tags?: string[];
description: string;
inStock?: boolean;
stockCount?: number;
}

// apps/frontend/src/lib/adapters.ts
import { ProductResponse } from "@/types/apiModels";
import { Product } from "@/types/product";

export function toUiProduct(p: ProductResponse): Product {
return {
id: String(p.id),
name: p.name,
price: p.price,
originalPrice: p.price, // 필요 시 할인 로직 추가
image: p.imageUrl ?? "/placeholder.png",
category: p.categoryName ?? "기타",
description: p.detailContent ?? p.description ?? "",
inStock: (p.stockQuantity ?? 0) > 0,
stockCount: p.stockQuantity ?? 0,
};
}
