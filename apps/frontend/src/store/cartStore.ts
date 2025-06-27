import { create } from "zustand";
import { persist } from "zustand/middleware";
import { CartStore, CartItem } from "@/types";

export const useCartStore = create<CartStore>()(
  persist(
    (set, get) => ({
      // State
      items: [],
      isAllSelected: true,
      appliedCoupon: null,
      discountAmount: 0,

      // Actions
      addItem: (product, quantity = 1, options) => {
        const state = get();
        const existingItemIndex = state.items.findIndex(
          (item) =>
            item.product.id === product.id &&
            item.selectedColor === options?.color &&
            item.selectedSize === options?.size
        );

        if (existingItemIndex >= 0) {
          // 기존 아이템 수량 업데이트
          const updatedItems = [...state.items];
          updatedItems[existingItemIndex].quantity += quantity;
          set({ items: updatedItems });
        } else {
          // 새 아이템 추가
          const newItem: CartItem = {
            id: `${product.id}-${Date.now()}`,
            product,
            quantity,
            selectedColor: options?.color,
            selectedSize: options?.size,
            isSelected: true,
          };
          set({ items: [...state.items, newItem] });
        }
      },

      removeItem: (itemId) => {
        const state = get();
        const updatedItems = state.items.filter((item) => item.id !== itemId);
        set({ items: updatedItems });
      },

      updateQuantity: (itemId, quantity) => {
        const state = get();
        if (quantity <= 0) {
          get().removeItem(itemId);
          return;
        }

        const updatedItems = state.items.map((item) =>
          item.id === itemId ? { ...item, quantity } : item
        );
        set({ items: updatedItems });
      },

      toggleItemSelection: (itemId) => {
        const state = get();
        const updatedItems = state.items.map((item) =>
          item.id === itemId ? { ...item, isSelected: !item.isSelected } : item
        );
        const isAllSelected =
          updatedItems.length > 0 &&
          updatedItems.every((item) => item.isSelected);
        set({ items: updatedItems, isAllSelected });
      },

      toggleAllSelection: () => {
        const state = get();
        const newSelectionState = !state.isAllSelected;
        const updatedItems = state.items.map((item) => ({
          ...item,
          isSelected: newSelectionState,
        }));
        set({ items: updatedItems, isAllSelected: newSelectionState });
      },

      applyCoupon: (couponCode) => {
        // 간단한 쿠폰 로직
        let discount = 0;
        if (couponCode === "WELCOME10") {
          discount = get().getSubtotal() * 0.1;
          if (discount > 10000) discount = 10000; // 최대 10,000원
        }
        set({ appliedCoupon: couponCode, discountAmount: discount });
      },

      removeCoupon: () => {
        set({ appliedCoupon: null, discountAmount: 0 });
      },

      clearCart: () => {
        set({
          items: [],
          isAllSelected: true,
          appliedCoupon: null,
          discountAmount: 0,
        });
      },

      getSelectedItems: () => {
        return get().items.filter((item) => item.isSelected);
      },

      getSubtotal: () => {
        const selectedItems = get().getSelectedItems();
        return selectedItems.reduce((total, item) => {
          const itemPrice = item.product.price;
          return total + itemPrice * item.quantity;
        }, 0);
      },

      getShippingFee: () => {
        const subtotal = get().getSubtotal();
        return subtotal >= 50000 ? 0 : 3000;
      },

      getTotalAmount: () => {
        const subtotal = get().getSubtotal();
        const shippingFee = get().getShippingFee();
        const discountAmount = get().discountAmount;
        return subtotal + shippingFee - discountAmount;
      },

      getItemCount: () => {
        return get().items.reduce((total, item) => total + item.quantity, 0);
      },
    }),
    {
      name: "cart-storage",
      partialize: (state) => ({
        items: state.items,
        appliedCoupon: state.appliedCoupon,
        discountAmount: state.discountAmount,
      }),
    }
  )
);
