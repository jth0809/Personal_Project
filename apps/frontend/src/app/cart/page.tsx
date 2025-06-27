"use client";

import { useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { ShoppingCart, ArrowLeft } from "lucide-react";

import CartItem from "@/components/Cart/CartItem";
import OrderSummary from "@/components/Cart/OrderSummary";
import RecommendedProducts from "@/components/Cart/RecommendedProducts";
import { initialCartItems } from "@/contents/cart";
import { CartItem as CartItemType } from "@/contents/products";

export default function Cart() {
  const [cartItems, setCartItems] = useState<CartItemType[]>(initialCartItems);

  // 전체 선택/해제
  const handleSelectAll = (checked: boolean) => {
    setCartItems((items) =>
      items.map((item) => ({ ...item, selected: checked }))
    );
  };

  // 개별 상품 선택/해제
  const handleSelectItem = (id: string, checked: boolean) => {
    setCartItems((items) =>
      items.map((item) =>
        item.id === id ? { ...item, selected: checked } : item
      )
    );
  };

  // 수량 변경
  const updateQuantity = (id: string, newQuantity: number) => {
    if (newQuantity < 1) return;
    setCartItems((items) =>
      items.map((item) =>
        item.id === id ? { ...item, quantity: newQuantity } : item
      )
    );
  };

  // 상품 삭제
  const removeItem = (id: string) => {
    setCartItems((items) => items.filter((item) => item.id !== id));
  };

  // 장바구니에 상품 추가
  const handleAddToCart = (productId: string) => {
    console.log("장바구니에 추가:", productId);
    // 장바구니 추가 로직 구현
  };

  // 결제
  const handleCheckout = () => {
    console.log("결제 진행");
    // 결제 로직 구현
  };

  const selectedItems = cartItems.filter((item) => item.selected);
  const allSelected =
    cartItems.length > 0 && cartItems.every((item) => item.selected);

  return (
    <div>
      <div className="bg-gray-50 min-h-screen">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* Breadcrumb */}
          <nav className="text-sm text-gray-600 mb-6">
            <Link href="/" className="hover:text-gray-900 flex items-center">
              <ArrowLeft className="h-4 w-4 mr-1" />
              홈으로 돌아가기
            </Link>
          </nav>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Cart Items */}
            <div className="lg:col-span-2 space-y-6">
              <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold text-gray-900">
                  장바구니 ({cartItems.length})
                </h2>
                <div className="flex items-center space-x-2">
                  <Checkbox
                    id="select-all"
                    checked={allSelected}
                    onCheckedChange={handleSelectAll}
                  />
                  <label htmlFor="select-all" className="text-sm text-gray-700">
                    전체 선택 ({selectedItems.length}/{cartItems.length})
                  </label>
                </div>
              </div>

              {cartItems.length === 0 ? (
                <Card className="p-12 text-center">
                  <ShoppingCart className="h-16 w-16 mx-auto text-gray-400 mb-4" />
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    장바구니가 비어있습니다
                  </h3>
                  <p className="text-gray-600 mb-6">
                    마음에 드는 상품을 장바구니에 담아보세요
                  </p>
                  <Link href="/">
                    <Button>쇼핑 계속하기</Button>
                  </Link>
                </Card>
              ) : (
                <div className="space-y-4">
                  {cartItems.map((item) => (
                    <Card key={item.id}>
                      <CardContent className="p-0">
                        <CartItem
                          item={item}
                          onSelectItem={handleSelectItem}
                          onUpdateQuantity={updateQuantity}
                          onRemoveItem={removeItem}
                        />
                      </CardContent>
                    </Card>
                  ))}

                  {selectedItems.length > 0 && (
                    <div className="flex justify-end space-x-2">
                      <Button variant="outline" size="sm">
                        선택 상품 삭제
                      </Button>
                      <Button variant="outline" size="sm">
                        선택 상품 찜하기
                      </Button>
                    </div>
                  )}
                </div>
              )}

              <RecommendedProducts onAddToCart={handleAddToCart} />
            </div>

            {/* Order Summary */}
            <div className="space-y-6">
              <OrderSummary
                selectedItems={selectedItems}
                onCheckout={handleCheckout}
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
