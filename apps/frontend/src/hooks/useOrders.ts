import { useMutation, useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/apiClient";

export const useOrders = (params?: { page?: number; size?: number }) =>
  useQuery({
    queryKey: ["orders", params],
    queryFn: () => apiFetch(`/orders/history`),
  });

export const useOrder = (orderId: number) =>
  useQuery({
    queryKey: ["order", orderId],
    queryFn: () => apiFetch(`/orders/${orderId}`),
  });

export const useCancelOrder = () =>
  useMutation({
    mutationFn: (args: { orderId: number; reason: string }) =>
      apiFetch(`/orders/${args.orderId}`, {
        method: "DELETE",
        body: JSON.stringify({ reason: args.reason }),
      }),
  });
