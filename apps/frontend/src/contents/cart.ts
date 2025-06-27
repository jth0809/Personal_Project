import { CartItem } from "./products";

// 초기 장바구니 데이터
export const initialCartItems: CartItem[] = [
  {
    id: "1",
    productId: "1",
    name: "프리미엄 에코백",
    price: 25000,
    originalPrice: 30000,
    image: "/next.svg",
    color: "네이비",
    size: "원 사이즈",
    quantity: 2,
    selected: true,
    inStock: true,
  },
  {
    id: "2",
    productId: "2",
    name: "유니크 머그컵",
    price: 18000,
    originalPrice: 22000,
    image: "/vercel.svg",
    color: "화이트",
    size: "300ml",
    quantity: 1,
    selected: true,
    inStock: true,
  },
  {
    id: "3",
    productId: "3",
    name: "스타일 키링",
    price: 12000,
    originalPrice: 15000,
    image: "/globe.svg",
    color: "실버",
    size: "원 사이즈",
    quantity: 3,
    selected: false,
    inStock: true,
  },
];

// 장바구니 계산 유틸리티 함수들
export const calculateSubtotal = (items: CartItem[]): number => {
  return items.reduce((sum, item) => sum + item.price * item.quantity, 0);
};

export const calculateOriginalTotal = (items: CartItem[]): number => {
  return items.reduce(
    (sum, item) => sum + item.originalPrice * item.quantity,
    0
  );
};

export const calculateDiscount = (
  originalTotal: number,
  subtotal: number
): number => {
  return originalTotal - subtotal;
};

export const calculateCouponDiscount = (
  subtotal: number,
  couponCode?: string
): number => {
  if (!couponCode) return 0;
  // 간단한 쿠폰 로직: 10% 할인, 최대 10,000원
  return Math.min(subtotal * 0.1, 10000);
};

export const calculateDeliveryFee = (subtotal: number): number => {
  return subtotal >= 50000 ? 0 : 3000;
};

export const calculateFinalTotal = (
  subtotal: number,
  couponDiscount: number,
  deliveryFee: number
): number => {
  return subtotal - couponDiscount + deliveryFee;
};
