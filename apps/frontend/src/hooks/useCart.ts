import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useCartStore } from "@/store/cartStore";
import {
  CartItem,
  Product,
  AddToCartData,
  UpdateCartItemData,
  CouponResponse,
} from "@/types";

// API 함수들
const fetchCartItems = async (): Promise<CartItem[]> => {
  await new Promise((resolve) => setTimeout(resolve, 800));

  // 실제 API 호출:
  // const response = await fetch('/api/cart');
  // if (!response.ok) throw new Error('Failed to fetch cart items');
  // return response.json();

  // 로컬 스토어에서 데이터 가져오기
  return [];
};

const addToCartApi = async (data: AddToCartData): Promise<CartItem> => {
  await new Promise((resolve) => setTimeout(resolve, 600));

  // 실제 API 호출:
  // const response = await fetch('/api/cart', {
  //   method: 'POST',
  //   headers: { 'Content-Type': 'application/json' },
  //   body: JSON.stringify(data)
  // });
  // if (!response.ok) throw new Error('Failed to add to cart');
  // return response.json();

  // 모의 응답 (실제로는 zustand store에서 처리)
  const mockProduct: Product = {
    id: data.productId,
    name: "샘플 상품",
    price: 25000,
    originalPrice: 30000,
    image: "/next.svg",
    category: "가방",
    rating: 4.8,
    reviews: 156,
    tags: ["베스트셀러"],
    description: "샘플 상품 설명",
  };

  return {
    id: `${data.productId}-${Date.now()}`,
    product: mockProduct,
    quantity: data.quantity,
    selectedColor: data.options?.color,
    selectedSize: data.options?.size,
    isSelected: true,
  };
};

const updateCartItemApi = async (_data: UpdateCartItemData): Promise<void> => {
  await new Promise((resolve) => setTimeout(resolve, 400));

  // 실제 API 호출:
  // const response = await fetch(`/api/cart/${data.itemId}`, {
  //   method: 'PUT',
  //   headers: { 'Content-Type': 'application/json' },
  //   body: JSON.stringify({ quantity: data.quantity })
  // });
  // if (!response.ok) throw new Error('Failed to update cart item');
};

const removeFromCartApi = async (_itemId: string): Promise<void> => {
  await new Promise((resolve) => setTimeout(resolve, 400));

  // 실제 API 호출:
  // const response = await fetch(`/api/cart/${itemId}`, {
  //   method: 'DELETE'
  // });
  // if (!response.ok) throw new Error('Failed to remove from cart');
};

const applyCouponApi = async (couponCode: string): Promise<CouponResponse> => {
  await new Promise((resolve) => setTimeout(resolve, 800));

  // 실제 API 호출:
  // const response = await fetch('/api/cart/coupon', {
  //   method: 'POST',
  //   headers: { 'Content-Type': 'application/json' },
  //   body: JSON.stringify({ couponCode })
  // });
  // if (!response.ok) throw new Error('Failed to apply coupon');
  // return response.json();

  // 모의 쿠폰 검증
  if (couponCode === "WELCOME10") {
    return {
      discount: 0.1,
      message: "10% 할인 쿠폰이 적용되었습니다!",
    };
  }

  throw new Error("유효하지 않은 쿠폰 코드입니다.");
};

// React Query hooks
export const useCartItems = () => {
  return useQuery({
    queryKey: ["cart"],
    queryFn: fetchCartItems,
    staleTime: 30 * 1000, // 30초
  });
};

export const useAddToCart = () => {
  const { addItem } = useCartStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: AddToCartData & { product: Product }) => {
      // 먼저 로컬 스토어에 추가
      addItem(data.product, data.quantity, data.options);

      // 그 다음 서버에 동기화
      await addToCartApi(data);

      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    },
    onError: (error) => {
      console.error("Failed to add to cart:", error);
      // 에러 발생 시 롤백 로직 추가 가능
    },
  });
};

export const useUpdateCartItem = () => {
  const { updateQuantity } = useCartStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: UpdateCartItemData) => {
      // 먼저 로컬 스토어 업데이트
      updateQuantity(data.itemId, data.quantity);

      // 서버에 동기화
      await updateCartItemApi(data);

      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    },
    onError: (error) => {
      console.error("Failed to update cart item:", error);
    },
  });
};

export const useRemoveFromCart = () => {
  const { removeItem } = useCartStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (itemId: string) => {
      // 먼저 로컬 스토어에서 제거
      removeItem(itemId);

      // 서버에 동기화
      await removeFromCartApi(itemId);

      return itemId;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    },
    onError: (error) => {
      console.error("Failed to remove from cart:", error);
    },
  });
};

export const useApplyCoupon = () => {
  const { applyCoupon } = useCartStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: applyCouponApi,
    onSuccess: (_, couponCode) => {
      applyCoupon(couponCode);
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    },
    onError: (error) => {
      console.error("Failed to apply coupon:", error);
    },
  });
};

export const useClearCart = () => {
  const { clearCart } = useCartStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      await new Promise((resolve) => setTimeout(resolve, 500));

      // 실제 API 호출:
      // const response = await fetch('/api/cart', {
      //   method: 'DELETE'
      // });
      // if (!response.ok) throw new Error('Failed to clear cart');
    },
    onSuccess: () => {
      clearCart();
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    },
  });
};
