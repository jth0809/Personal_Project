import { useMutation, useQueryClient } from "@tanstack/react-query";
import { apiFetch } from "@/lib/apiClient";
import { ProductCreateRequest, ProductUpdateRequest } from "@/types/backend";

export function useCreateProduct() {
  const qc = useQueryClient();
  return useMutation<void, Error, ProductCreateRequest>({
    mutationFn: (payload) =>
      apiFetch<void>(`/products`, {
        method: "POST",
        body: JSON.stringify(payload),
      }),
    onSettled: () => {
      qc.invalidateQueries({ queryKey: ["admin-products"] });
      qc.invalidateQueries({ queryKey: ["products"] });
    },
  });
}

export function useUpdateProduct(id: number) {
  const qc = useQueryClient();
  return useMutation<void, Error, ProductUpdateRequest>({
    mutationFn: (payload) =>
      apiFetch<void>(`/products/${id}`, {
        method: "PUT",
        body: JSON.stringify(payload),
      }),
    onSettled: () => {
      qc.invalidateQueries({ queryKey: ["admin-products"] });
      qc.invalidateQueries({ queryKey: ["products"] });
      qc.invalidateQueries({ queryKey: ["admin-product", id] });
      qc.invalidateQueries({ queryKey: ["product", id] });
    },
  });
}

export function useDeleteProduct() {
  const qc = useQueryClient();
  return useMutation<void, Error, number>({
    mutationFn: (productId) =>
      apiFetch<void>(`/products/${productId}`, { method: "DELETE" }),
    onSettled: () => {
      qc.invalidateQueries({ queryKey: ["admin-products"] });
      qc.invalidateQueries({ queryKey: ["products"] });
    },
  });
}
