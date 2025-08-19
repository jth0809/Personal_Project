import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/apiClient";
import { ProductResponse } from "@/types/backend";

export const useProduct = (id: number) =>
  useQuery({
    queryKey: ["product", id],
    queryFn: () => apiFetch<ProductResponse>(`/products/${id}`),
  });
