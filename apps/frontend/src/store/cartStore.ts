import { create } from "zustand";
import { persist } from "zustand/middleware";

type CartBadgeState = {
  count: number;
  set: (n: number) => void;
  inc: (delta?: number) => void;
  dec: (delta?: number) => void;
  reset: () => void;
};

export const useCartBadge = create<CartBadgeState>()(
  persist(
    (set) => {
      const clamp = (n: number) => Math.max(0, n | 0);
      return {
        count: 0,
        set: (n) =>
          set((s) => {
            const v = clamp(n);
            return s.count === v ? s : { ...s, count: v };
          }),
        inc: (d = 1) =>
          set((s) => {
            const v = clamp(s.count + d);
            return s.count === v ? s : { ...s, count: v };
          }),
        dec: (d = 1) =>
          set((s) => {
            const v = clamp(s.count - d);
            return s.count === v ? s : { ...s, count: v };
          }),
        reset: () => set((s) => (s.count === 0 ? s : { ...s, count: 0 })),
      };
    },
    { name: "cart-badge" }
  )
);
