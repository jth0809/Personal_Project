import { ProductResponse } from "@/types/apiModels";
import { Product } from "@/types/product"; // 기존 UI 모델 유지

export function toUiProduct(p: ProductResponse): Product {
  return {
    id: String(p.id),
    name: p.name,
    price: p.price,
    originalPrice: p.price,
    image: Array.isArray(p.imageUrl)
      ? p.imageUrl[0]
      : (p.imageUrl ?? "/placeholder.png"),
    category: p.categoryName ?? "기타",
    description: p.detailContent ?? p.description ?? "",
    inStock: (p.stockQuantity ?? 0) > 0,
    stockCount: p.stockQuantity ?? 0,
    rating: 0,
    reviews: 0,
    tags: [],
  };
}
