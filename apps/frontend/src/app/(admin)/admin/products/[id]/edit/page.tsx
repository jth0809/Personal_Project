"use client";
import { useParams, useRouter } from "next/navigation";
import { useAdminProduct } from "@/hooks/admin/useAdminProducts";
import { useUpdateProduct } from "@/hooks/admin/useAdminProductMutations";
import ProductForm from "@/components/admin/ProductForm";
import type { ProductUpdateRequest } from "@/types/backend";

export default function ProductEditPage() {
  const params = useParams();
  const router = useRouter();
  const id = Number(params?.id);
  const { data, isLoading, error } = useAdminProduct(id);
  const update = useUpdateProduct(id);

  if (isLoading) return <p className="text-sm text-gray-500">불러오는 중…</p>;
  if (error || !data)
    return <p className="text-sm text-red-600">상품을 불러오지 못했습니다.</p>;

  return (
    <div className="space-y-6">
      <h1 className="text-xl font-bold">상품 수정</h1>
      <ProductForm
        mode="edit"
        initial={data}
        onSubmit={async (payload: ProductUpdateRequest) => {
          await update.mutateAsync(payload);
          router.push("/admin/products");
        }}
      />
    </div>
  );
}
