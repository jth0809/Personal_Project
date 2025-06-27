import Image from "next/image";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { recommendedProducts } from "@/contents/products";

interface RecommendedProductsProps {
  onAddToCart: (productId: string) => void;
}

export default function RecommendedProducts({
  onAddToCart,
}: RecommendedProductsProps) {
  return (
    <div className="mt-12">
      <h3 className="text-xl font-bold text-gray-900 mb-6">
        이런 상품은 어떠세요?
      </h3>
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {recommendedProducts.map((product) => (
          <Card
            key={product.id}
            className="group hover:shadow-lg transition-shadow"
          >
            <CardContent className="p-4">
              <div className="flex space-x-4">
                <Image
                  src={product.image}
                  alt={product.name}
                  width={80}
                  height={80}
                  className="rounded-lg object-cover"
                />
                <div className="flex-1">
                  <h4 className="font-semibold text-gray-900 mb-1">
                    {product.name}
                  </h4>
                  <div className="flex items-center space-x-2 mb-2">
                    <span className="text-lg font-bold text-gray-900">
                      {product.price.toLocaleString()}원
                    </span>
                    <span className="text-sm text-gray-500 line-through">
                      {product.originalPrice.toLocaleString()}원
                    </span>
                  </div>
                  <Button
                    size="sm"
                    variant="outline"
                    className="w-full"
                    onClick={() => onAddToCart(product.id)}
                  >
                    장바구니 담기
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
