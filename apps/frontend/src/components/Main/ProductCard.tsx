"use client";

import Image from "next/image";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { ShoppingCart, Heart, Star } from "lucide-react";
import { Product } from "@/types";
import { calculateDiscount } from "@/contents/products";

interface ProductCardProps {
  product: Product;
  onAddToCart?: (productId: string) => void;
  onAddToWishlist?: (productId: string) => void;
}

export default function ProductCard({
  product,
  onAddToCart,
  onAddToWishlist,
}: ProductCardProps) {
  const discountPercentage = calculateDiscount(
    product.originalPrice,
    product.price
  );

  const handleCardClick = (e: React.MouseEvent) => {
    // 버튼 클릭 시에는 카드 클릭 이벤트를 막음
    if ((e.target as HTMLElement).closest("button")) {
      return;
    }
    // 상품 상세 페이지로 이동 (부모 컴포넌트에서 Link로 감싸져 있음)
  };

  return (
    <Card
      className="group hover:shadow-lg transition-shadow duration-300 cursor-pointer"
      onClick={handleCardClick}
    >
      <CardHeader className="p-0">
        <div className="relative overflow-hidden rounded-t-lg">
          <Image
            src={product.image}
            alt={product.name}
            width={400}
            height={300}
            className="w-full h-48 object-cover group-hover:scale-105 transition-transform duration-300"
          />
          <div className="absolute top-2 left-2 flex flex-wrap gap-1">
            {product.tags.map((tag: string) => (
              <Badge key={tag} variant="secondary" className="text-xs">
                {tag}
              </Badge>
            ))}
          </div>
          <Button
            size="icon"
            variant="ghost"
            className="absolute top-2 right-2 bg-white/80 hover:bg-white"
            onClick={(e) => {
              e.stopPropagation();
              onAddToWishlist?.(product.id);
            }}
          >
            <Heart className="h-4 w-4" />
          </Button>
        </div>
      </CardHeader>
      <CardContent className="p-4">
        <div className="flex items-center justify-between mb-2">
          <Badge variant="outline" className="text-xs">
            {product.category}
          </Badge>
          <div className="flex items-center space-x-1">
            <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
            <span className="text-sm text-gray-600">
              {product.rating} ({product.reviews})
            </span>
          </div>
        </div>
        <CardTitle className="text-lg mb-2">{product.name}</CardTitle>
        <p className="text-sm text-gray-600 mb-3">{product.description}</p>
        <div className="flex items-center space-x-2">
          <span className="text-lg font-bold text-gray-900">
            {product.price.toLocaleString()}원
          </span>
          <span className="text-sm text-gray-500 line-through">
            {product.originalPrice.toLocaleString()}원
          </span>
          <Badge variant="destructive" className="text-xs">
            {discountPercentage}% 할인
          </Badge>
        </div>
      </CardContent>
      <CardFooter className="p-4 pt-0">
        <Button
          className="w-full"
          size="sm"
          onClick={(e) => {
            e.stopPropagation();
            onAddToCart?.(product.id);
          }}
        >
          <ShoppingCart className="h-4 w-4 mr-2" />
          장바구니 담기
        </Button>
      </CardFooter>
    </Card>
  );
}
