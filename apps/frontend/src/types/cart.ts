import { Product } from "./product";

export interface CartItem {
  id: string;
  product: Product;
  quantity: number;
  selectedColor?: string;
  selectedSize?: string;
  isSelected: boolean;
}

export interface CartState {
  items: CartItem[];
  isAllSelected: boolean;
  appliedCoupon: string | null;
  discountAmount: number;
}

export interface CartActions {
  addItem: (
    product: Product,
    quantity?: number,
    options?: { color?: string; size?: string }
  ) => void;
  removeItem: (itemId: string) => void;
  updateQuantity: (itemId: string, quantity: number) => void;
  toggleItemSelection: (itemId: string) => void;
  toggleAllSelection: () => void;
  applyCoupon: (couponCode: string) => void;
  removeCoupon: () => void;
  clearCart: () => void;
  getSelectedItems: () => CartItem[];
  getSubtotal: () => number;
  getShippingFee: () => number;
  getTotalAmount: () => number;
  getItemCount: () => number;
}

export type CartStore = CartState & CartActions;

export interface AddToCartData {
  productId: string;
  quantity: number;
  options?: {
    color?: string;
    size?: string;
  };
}

export interface UpdateCartItemData {
  itemId: string;
  quantity: number;
}

export interface CouponResponse {
  discount: number;
  message: string;
}
