"use client";
import RequireAuth from "@/components/Account/RequireAuth";
import { useOrder } from "@/hooks/useOrders";
import { useParams } from "next/navigation";

export default function OrderDetailPage() {
  const params = useParams();
  const id = Number(params?.id);
  const { data, isLoading, error } = useOrder(id);

  return (
    <RequireAuth>
      <div className="space-y-3">
        <h1 className="text-xl font-extrabold">주문 상세</h1>
        {isLoading && (
          <div className="py-10 text-center text-gray-500">불러오는 중…</div>
        )}
        {error && (
          <div className="py-10 text-center text-red-600">
            불러오지 못했습니다.
          </div>
        )}
        {data && (data as any).id && (
          <div className="space-y-4 rounded-2xl border bg-white p-4">
            <div className="flex items-center justify-between">
              <p className="font-semibold">주문번호 #{(data as any).id}</p>
              <p className="text-sm text-gray-500">{(data as any).status}</p>
            </div>
            <ul className="space-y-2 text-sm">
              {(data as any).items?.map((it: any) => (
                <li key={it.id} className="flex items-center justify-between">
                  <span>
                    {it.name} × {it.quantity}
                  </span>
                  <span className="font-medium">
                    {(it.price * it.quantity).toLocaleString()}원
                  </span>
                </li>
              ))}
            </ul>
            <div className="flex items-center justify-between border-t pt-3 text-sm">
              <span className="text-gray-500">총액</span>
              <span className="text-base font-semibold">
                {(data as any).totalPrice?.toLocaleString()}원
              </span>
            </div>
          </div>
        )}
      </div>
    </RequireAuth>
  );
}
