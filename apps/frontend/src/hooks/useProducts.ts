import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/apiClient";
import { Page } from "@/types/api";
import { ProductResponse } from "@/types/apiModels";
import { toUiProduct } from "@/lib/adapters";

export function useProducts(params?: {
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

  return useQuery({
    queryKey: ["products", params],
    queryFn: async () => {
      const data = await apiFetch<Page<ProductResponse>>(
        `/products${qs ? `?${qs}` : ""}`
      );
      return { ...data, content: data.content.map(toUiProduct) };
    },
    staleTime: 60_000,
  });
}
