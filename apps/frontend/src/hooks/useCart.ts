import { useEffect } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { apiFetch } from "@/lib/apiClient";
import type { CartResponse } from "@/types/apiModels";
import { useCartBadge } from "@/store/cartStore";

const sumQty = (data?: CartResponse | null) =>
  (data?.items ?? []).reduce((s, it) => s + (it.quantity ?? 0), 0);

type AddVars = { productId: number; quantity: number };
type Ctx = { prev: number };

export function useCart() {
  const qc = useQueryClient();
  const setBadge = useCartBadge((s) => s.set);

  const cart = useQuery<CartResponse, Error>({
    queryKey: ["cart"],
    queryFn: () => apiFetch<CartResponse>("/cart"),
    staleTime: 30_000,
  });

  // ▶ 쿼리 결과를 배지와 동기화 (값이 바뀔 때만)
  useEffect(() => {
    if (!cart.data) return;
    const next = sumQty(cart.data);
    if (useCartBadge.getState().count !== next) setBadge(next);
    // setBadge는 안정적, cart.data가 바뀔 때만 실행
  }, [cart.data, setBadge]);

  // ▶ 에러 시 배지를 0으로 (이미 0이면 no-op)
  useEffect(() => {
    if (!cart.isError) return;
    if (useCartBadge.getState().count !== 0) setBadge(0);
  }, [cart.isError, setBadge]);

  // 담기(optimistic) → 실패 롤백 → 최종 재검증
  const add = useMutation<void, Error, AddVars, Ctx>({
    mutationFn: (vars) =>
      apiFetch<void>("/cart/items", {
        method: "POST",
        body: JSON.stringify(vars),
      }),
    onMutate: async (vars) => {
      const prev = useCartBadge.getState().count;
      setBadge(prev + Math.max(1, vars.quantity));
      return { prev };
    },
    onError: (_e, _v, ctx) => {
      if (ctx) setBadge(ctx.prev);
    },
    onSettled: async () => {
      await qc.invalidateQueries({ queryKey: ["cart"] });
    },
  });

  // 삭제(optimistic) → 실패 롤백 → 최종 재검증
  const remove = useMutation<void, Error, number, Ctx>({
    mutationFn: (itemId) =>
      apiFetch<void>(`/cart/items/${itemId}`, { method: "DELETE" }),
    onMutate: async (itemId) => {
      const prev = useCartBadge.getState().count;
      const cached = qc.getQueryData<CartResponse>(["cart"]);
      const targetQty =
        cached?.items.find((i) => i.id === itemId)?.quantity ?? 0;
      if (targetQty > 0) setBadge(prev - targetQty);
      return { prev };
    },
    onError: (_e, _id, ctx) => {
      if (ctx) setBadge(ctx.prev);
    },
    onSettled: async () => {
      await qc.invalidateQueries({ queryKey: ["cart"] });
    },
  });

  return { cart, add, remove };
}
