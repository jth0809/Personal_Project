import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/apiClient";
import { toUiProduct } from "@/lib/adapters";
import type { PageProductResponse, ProductResponse } from "@/types/backend";

/** UI 카드에서 쓰는 제품 타입 (toUiProduct 결과 타입) */
export type UiProduct = ReturnType<typeof toUiProduct>;
/** 페이지 응답을 UI용으로 매핑한 타입 */
export type PageUiProduct = Omit<PageProductResponse, "content"> & {
  content: UiProduct[];
};

export type UseProductsParams = {
  keyword?: string;
  categoryId?: number;
  page?: number;
  size?: number;
  sortBy?: "id" | "price" | "name";
  sortOrder?: "ASC" | "DESC";
};

function buildQuery(params?: UseProductsParams): string {
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

/** 공개 상품 리스트 (UI용으로 매핑해서 반환) */
export function useProducts(params?: UseProductsParams) {
  const qs = buildQuery(params);
  return useQuery<PageUiProduct, Error>({
    queryKey: ["products", params],
    queryFn: async () => {
      const raw = await apiFetch<PageProductResponse>(`/products${qs}`);
      return {
        ...raw,
        content: raw.content.map<UiProduct>((p: ProductResponse) =>
          toUiProduct(p)
        ),
      };
    },
    staleTime: 60_000,
  });
}
