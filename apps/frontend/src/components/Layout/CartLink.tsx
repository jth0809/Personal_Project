"use client";
import Link from "next/link";
import { useCartBadge } from "@/store/cartStore";
import { useHasHydrated } from "@/hooks/useHasHydrated";
import { ShoppingCartIcon } from "lucide-react";

export default function CartLink() {
  const hydrated = useHasHydrated();
  const count = useCartBadge((s) => s.count);

  return (
    <Link
      href="/cart"
      className="relative text-sm hover:underline flex items-center gap-2"
    >
      <ShoppingCartIcon className="w-6 h-6" />
      {hydrated && count > 0 && (
        <span
          className="inline-flex h-5 min-w-[1.25rem] items-center justify-center absolute top-0 right-0 translate-x-1/2 -translate-y-1/2
                     rounded-full bg-gray-900 px-1 text-xs font-semibold text-white"
          aria-label={`장바구니 수량 ${count}`}
        >
          {count}
        </span>
      )}
    </Link>
  );
}
