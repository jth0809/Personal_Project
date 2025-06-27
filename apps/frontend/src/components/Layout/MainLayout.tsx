"use client";

import { ReactNode } from "react";
import Header from "./Header";
import Footer from "./Footer";
import { useCartStore } from "@/store/cartStore";

interface MainLayoutProps {
  children: ReactNode;
}

export default function MainLayout({ children }: MainLayoutProps) {
  const cartItemCount = useCartStore((state) => state.getItemCount());
  const wishlistCount = 0; // 추후 wishlist 스토어에서 가져올 수 있음

  return (
    <div className="min-h-screen flex flex-col">
      <Header cartItemCount={cartItemCount} wishlistCount={wishlistCount} />
      <main className="flex-1">{children}</main>
      <Footer />
    </div>
  );
}
