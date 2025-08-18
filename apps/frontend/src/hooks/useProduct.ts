import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/apiClient";
import { ProductResponse } from "@/types/apiModels";

export const useProduct = (id: number) =>
  useQuery({
    queryKey: ["product", id],
    queryFn: () => apiFetch<ProductResponse>(`/products/${id}`),
  });
