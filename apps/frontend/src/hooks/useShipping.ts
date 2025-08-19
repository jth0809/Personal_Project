import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/apiClient";
import { ShippingResponse } from "@/types/backend";

export const useShipping = (productId: number) =>
  useQuery({
    queryKey: ["shipping", productId],
    queryFn: () =>
      apiFetch<ShippingResponse>(`/products/${productId}/shipping`),
  });
