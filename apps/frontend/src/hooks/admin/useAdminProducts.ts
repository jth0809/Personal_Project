import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/apiClient";
import type { PageProductResponse, ProductResponse } from "@/types/backend";

export type AdminListParams = {
  keyword?: string;
  categoryId?: number;
  page?: number;
  size?: number;
  sortBy?: "id" | "price" | "name";
  sortOrder?: "ASC" | "DESC";
};

function buildQuery(params?: AdminListParams): string {
  const sp = new URLSearchParams();
  if (!params) return "";
  const set = (k: string, v: unknown) => {
    if (v !== undefined && v !== null && `${v}` !== "") sp.set(k, String(v));
  };
  set("keyword", params.keyword);
  set("categoryId", params.categoryId);
  set("page", params.page);
  set("size", params.size);
  set("sortBy", params.sortBy);
  set("sortOrder", params.sortOrder);
  const qs = sp.toString();
  return qs ? `?${qs}` : "";
}

/** 어드민: 원본 백엔드 타입 그대로 반환 (표/수정용) */
export function useAdminProducts(params?: AdminListParams) {
  const qs = buildQuery(params);
  return useQuery<PageProductResponse, Error>({
    queryKey: ["admin-products", params],
    queryFn: () => apiFetch<PageProductResponse>(`/products${qs}`),
    staleTime: 60_000,
  });
}

/** 어드민: 단일 상품 상세 (원본 타입) */
export function useAdminProduct(id: number) {
  return useQuery<ProductResponse, Error>({
    queryKey: ["admin-product", id],
    queryFn: () => apiFetch<ProductResponse>(`/products/${id}`),
    enabled: Number.isFinite(id) && id > 0,
  });
}
