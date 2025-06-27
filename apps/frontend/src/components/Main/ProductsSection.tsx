"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { sampleProducts } from "@/contents/products";
import ProductCard from "./ProductCard";

export default function ProductsSection() {
  const handleAddToCart = (productId: string) => {
    console.log("장바구니에 추가:", productId);
    // 장바구니 추가 로직 구현
  };

  const handleAddToWishlist = (productId: string) => {
    console.log("찜 목록에 추가:", productId);
    // 찜 목록 추가 로직 구현
  };

  return (
    <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <div className="flex justify-between items-center mb-8">
        <h3 className="text-2xl font-bold text-gray-900">인기 상품</h3>
        <div className="flex space-x-2">
          <Button variant="outline" size="sm">
            전체
          </Button>
          <Button variant="outline" size="sm">
            베스트셀러
          </Button>
          <Button variant="outline" size="sm">
            신상품
          </Button>
          <Button variant="outline" size="sm">
            할인상품
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {sampleProducts.map((product) => (
          <Link key={product.id} href={`/product/${product.id}`}>
            <ProductCard
              product={product}
              onAddToCart={handleAddToCart}
              onAddToWishlist={handleAddToWishlist}
            />
          </Link>
        ))}
      </div>
    </section>
  );
}
