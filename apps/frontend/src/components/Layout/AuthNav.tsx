"use client";
import Link from "next/link";
import { useAuthStore } from "@/store/authStore";
import { useHasHydrated } from "@/hooks/useHasHydrated";
import { decodeJwtPayload } from "@/lib/authToken";
import CartLink from "@/components/Layout/CartLink";
import { useCartBadge } from "@/store/cartStore";
import { LogOutIcon, Settings, ShoppingBagIcon, UserIcon } from "lucide-react";
import { Button } from "../ui/button";

export default function AuthNav() {
  const hydrated = useHasHydrated();
  const token = useAuthStore((s) => s.token);
  const clear = useAuthStore((s) => s.clear);
  const resetCartBadge = useCartBadge((s) => s.reset);

  if (!hydrated)
    return <div className="hidden md:block w-40 h-5" aria-hidden />;

  const onLogout = () => {
    clear();
    resetCartBadge();
  };

  if (token) {
    const payload = decodeJwtPayload<{ email?: string; name?: string }>(token);
    const name = payload?.name || payload?.email || "사용자";
    const initial = (name[0] || "U").toUpperCase();

    return (
      <nav className="hidden items-center gap-8 md:flex">
        <CartLink />
        <Link href={"/orders"} className="text-sm hover:underline">
          <ShoppingBagIcon className="w-6 h-6" />
        </Link>
        <Link
          href="/admin/products"
          className="text-sm hover:underline"
          aria-label="상품 관리"
        >
          <Settings className="w-6 h-6" />
        </Link>
        <span className="inline-flex items-center gap-2 text-sm text-gray-700">
          <span className="grid h-6 w-6 place-items-center rounded-full bg-gray-900 text-white text-xs">
            {initial}
          </span>
        </span>
        <Button onClick={onLogout}>
          <LogOutIcon className="w-6 h-6" />
        </Button>
      </nav>
    );
  }

  return (
    <nav className="hidden items-center gap-5 md:flex">
      <CartLink />
      <Link href={"/orders"} className="text-sm hover:underline">
        <ShoppingBagIcon className="w-6 h-6" />
      </Link>
      <Link href={"/login"} className="text-sm hover:underline">
        <UserIcon className="w-6 h-6" />
      </Link>
    </nav>
  );
}
