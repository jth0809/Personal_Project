"use client";
import RequireAuth from "@/components/Account/RequireAuth";
import { useOrders } from "@/hooks/useOrders";
import Link from "next/link";

export default function OrdersPage() {
  const { data, isLoading, error } = useOrders({ page: 0, size: 20 });
  return (
    <RequireAuth>
      <div className="space-y-3">
        <h1 className="text-xl font-extrabold">주문 내역</h1>
        {isLoading && (
          <div className="py-10 text-center text-gray-500">불러오는 중…</div>
        )}
        {error && (
          <div className="py-10 text-center text-red-600">
            불러오지 못했습니다.
          </div>
        )}
        {data && data.content && (
          <ul className="space-y-2">
            {data.content.map((o) => (
              <li
                key={o.orderId}
                className="flex items-center justify-between rounded-2xl border bg-white p-4"
              >
                <div>
                  <p className="font-medium">주문번호 #{o.orderId}</p>
                  <p className="text-sm text-gray-500">
                    {o.orderStatus} · {new Date(o.orderDate).toLocaleString()}
                  </p>
                </div>
                <Link
                  href={`/orders/${o.orderId}`}
                  className="text-sm underline"
                >
                  상세보기
                </Link>
              </li>
            ))}
          </ul>
        )}
      </div>
    </RequireAuth>
  );
}
