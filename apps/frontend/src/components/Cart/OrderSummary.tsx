import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { CreditCard } from "lucide-react";
import { CartItem } from "@/contents/products";
import {
  calculateSubtotal,
  calculateOriginalTotal,
  calculateDiscount,
  calculateCouponDiscount,
  calculateDeliveryFee,
  calculateFinalTotal,
} from "@/contents/cart";

interface OrderSummaryProps {
  selectedItems: CartItem[];
  onCheckout: () => void;
}

export default function OrderSummary({
  selectedItems,
  onCheckout,
}: OrderSummaryProps) {
  const [couponCode, setCouponCode] = useState("");
  const [appliedCoupon, setAppliedCoupon] = useState<string | null>(null);

  const subtotal = calculateSubtotal(selectedItems);
  const originalTotal = calculateOriginalTotal(selectedItems);
  const discount = calculateDiscount(originalTotal, subtotal);
  const couponDiscount = calculateCouponDiscount(
    subtotal,
    appliedCoupon || undefined
  );
  const deliveryFee = calculateDeliveryFee(subtotal);
  const finalTotal = calculateFinalTotal(subtotal, couponDiscount, deliveryFee);

  const applyCoupon = () => {
    if (couponCode.trim()) {
      setAppliedCoupon(couponCode);
      setCouponCode("");
    }
  };

  return (
    <Card className="sticky top-4">
      <CardHeader>
        <CardTitle>주문 요약</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="space-y-2">
          <div className="flex justify-between text-sm">
            <span>상품 금액</span>
            <span>{subtotal.toLocaleString()}원</span>
          </div>
          {discount > 0 && (
            <div className="flex justify-between text-sm text-red-600">
              <span>상품 할인</span>
              <span>-{discount.toLocaleString()}원</span>
            </div>
          )}
          {couponDiscount > 0 && (
            <div className="flex justify-between text-sm text-blue-600">
              <span>쿠폰 할인</span>
              <span>-{couponDiscount.toLocaleString()}원</span>
            </div>
          )}
          <div className="flex justify-between text-sm">
            <span>배송비</span>
            <span>
              {deliveryFee === 0 ? "무료" : `${deliveryFee.toLocaleString()}원`}
            </span>
          </div>
        </div>

        <Separator />

        <div className="flex justify-between text-lg font-bold">
          <span>총 결제 금액</span>
          <span className="text-blue-600">{finalTotal.toLocaleString()}원</span>
        </div>

        {subtotal < 50000 && (
          <p className="text-xs text-gray-600 text-center">
            {(50000 - subtotal).toLocaleString()}원 더 구매하면 무료배송!
          </p>
        )}

        <div className="space-y-2">
          <div className="flex space-x-2">
            <Input
              placeholder="쿠폰 코드 입력"
              value={couponCode}
              onChange={(e) => setCouponCode(e.target.value)}
              className="flex-1"
            />
            <Button variant="outline" onClick={applyCoupon}>
              적용
            </Button>
          </div>
          {appliedCoupon && (
            <p className="text-xs text-green-600">
              &apos;{appliedCoupon}&apos; 쿠폰이 적용되었습니다
            </p>
          )}
        </div>

        <Button
          className="w-full h-12"
          size="lg"
          disabled={selectedItems.length === 0}
          onClick={onCheckout}
        >
          <CreditCard className="h-4 w-4 mr-2" />
          주문하기 ({selectedItems.length}개 상품)
        </Button>

        <div className="space-y-2 text-xs text-gray-600">
          <div className="flex items-center space-x-2">
            <div className="w-1 h-1 bg-gray-400 rounded-full"></div>
            <span>50,000원 이상 구매 시 무료배송</span>
          </div>
          <div className="flex items-center space-x-2">
            <div className="w-1 h-1 bg-gray-400 rounded-full"></div>
            <span>평일 오후 2시 이전 주문 시 당일 발송</span>
          </div>
          <div className="flex items-center space-x-2">
            <div className="w-1 h-1 bg-gray-400 rounded-full"></div>
            <span>7일 이내 교환/환불 가능</span>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
