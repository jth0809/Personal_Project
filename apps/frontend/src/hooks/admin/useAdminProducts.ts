import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/apiClient";
import { Page } from "@/types/api";
import { ProductResponse } from "@/types/apiModels";

export function useAdminProducts(params?: {
  keyword?: string;
  categoryId?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortOrder?: "ASC" | "DESC";
}) {
  const qs = new URLSearchParams(
    Object.entries(params || {}).filter(([, v]) => v !== undefined) as any
  ).toString();
  return useQuery<Page<ProductResponse>, Error>({
    queryKey: ["admin-products", params],
    queryFn: () =>
      apiFetch<Page<ProductResponse>>(`/products${qs ? `?${qs}` : ""}`),
    staleTime: 60_000,
  });
}

export function useAdminProduct(id: number) {
  return useQuery<ProductResponse, Error>({
    queryKey: ["admin-product", id],
    queryFn: () => apiFetch<ProductResponse>(`/products/${id}`),
    enabled: Number.isFinite(id) && id > 0,
  });
}
