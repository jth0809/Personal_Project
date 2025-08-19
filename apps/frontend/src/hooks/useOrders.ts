import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/apiClient";
import type {
  PageOrderHistoryResponse,
  OrderHistoryResponse,
} from "@/types/backend";

export const useOrders = (params?: { page?: number; size?: number }) => {
  const qs = new URLSearchParams();
  if (params?.page != null) qs.set("page", String(params.page));
  if (params?.size != null) qs.set("size", String(params.size));

  return useQuery<PageOrderHistoryResponse, Error>({
    queryKey: ["orders", params],
    queryFn: () =>
      apiFetch<PageOrderHistoryResponse>(
        `/orders/history${qs.toString() ? `?${qs.toString()}` : ""}`
      ),
    staleTime: 60_000,
  });
};

export const useOrder = (orderId: number) =>
  useQuery<OrderHistoryResponse, Error>({
    queryKey: ["order", orderId],
    queryFn: () => apiFetch<OrderHistoryResponse>(`/orders/${orderId}`),
    enabled: Number.isFinite(orderId) && orderId > 0,
    staleTime: 60_000,
  });
