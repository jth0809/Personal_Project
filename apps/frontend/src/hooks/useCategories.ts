import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/apiClient";
import { CategoryResponse } from "@/types/apiModels";

export const useCategories = () =>
  useQuery({
    queryKey: ["categories"],
    queryFn: () => apiFetch<CategoryResponse[]>(`/categories`),
    staleTime: 300_000,
  });
