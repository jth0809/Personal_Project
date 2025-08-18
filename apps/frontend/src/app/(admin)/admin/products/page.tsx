"use client";
import Link from "next/link";
import { useState } from "react";
import { useAdminProducts } from "@/hooks/admin/useAdminProducts";
import { useDeleteProduct } from "@/hooks/admin/useAdminProductMutations";
import { Button } from "@/components/ui/button";

export default function AdminProductsHome() {
  const [page, setPage] = useState(0);
  const { data, isLoading, error } = useAdminProducts({
    page,
    size: 20,
    sortBy: "id",
    sortOrder: "DESC",
  });
  const del = useDeleteProduct();

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-bold">상품 관리</h1>
        <Button asChild>
          <Link href="/admin/products/new">새 상품</Link>
        </Button>
      </div>

      {isLoading && <p className="text-sm text-gray-500">불러오는 중…</p>}
      {error && (
        <p className="text-sm text-red-600">목록을 불러오지 못했습니다.</p>
      )}

      {data && (
        <div className="overflow-hidden rounded-xl border bg-white">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-left">
              <tr>
                <th className="px-4 py-2">ID</th>
                <th className="px-4 py-2">이름</th>
                <th className="px-4 py-2">가격</th>
                <th className="px-4 py-2">재고</th>
                <th className="px-4 py-2">카테고리</th>
                <th className="px-4 py-2 text-right">액션</th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((p) => (
                <tr key={p.id} className="border-t">
                  <td className="px-4 py-2">{p.id}</td>
                  <td className="px-4 py-2">{p.name}</td>
                  <td className="px-4 py-2">{p.price.toLocaleString()}원</td>
                  <td className="px-4 py-2">{p.stockQuantity}</td>
                  <td className="px-4 py-2">{p.categoryName || "-"}</td>
                  <td className="px-4 py-2 text-right">
                    <div className="flex justify-end gap-2">
                      <Button variant="secondary" size="sm" asChild>
                        <Link href={`/admin/products/${p.id}/edit`}>수정</Link>
                      </Button>
                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => del.mutate(p.id)}
                        disabled={del.isPending}
                      >
                        삭제
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div className="flex items-center justify-center gap-2">
        <Button
          variant="outline"
          size="sm"
          onClick={() => setPage((p) => Math.max(0, p - 1))}
          disabled={page <= 0}
        >
          이전
        </Button>
        <span className="text-sm text-gray-600">
          {page + 1} / {Math.max(1, data?.totalPages ?? 1)}
        </span>
        <Button
          variant="outline"
          size="sm"
          onClick={() =>
            setPage((p) =>
              data ? Math.min(data.totalPages - 1, p + 1) : p + 1
            )
          }
          disabled={!data || page >= data.totalPages - 1}
        >
          다음
        </Button>
      </div>
    </div>
  );
}
