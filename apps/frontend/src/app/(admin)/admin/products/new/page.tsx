"use client";
import { useRouter } from "next/navigation";
import ProductForm from "@/components/admin/ProductForm";
import { useCreateProduct } from "@/hooks/admin/useAdminProductMutations";
import { ProductCreateRequest } from "@/types/backend";

export default function ProductCreatePage() {
  const router = useRouter();
  const create = useCreateProduct();

  return (
    <div className="space-y-6">
      <h1 className="text-xl font-bold">상품 생성</h1>
      <ProductForm
        mode="create"
        onSubmit={async (payload) => {
          await create.mutateAsync(payload as ProductCreateRequest);
          router.push("/admin/products");
        }}
      />
    </div>
  );
}
