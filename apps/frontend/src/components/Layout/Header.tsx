"use client";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import AuthNav from "@/components/Layout/AuthNav";
import { Button } from "../ui/button";

export default function Header() {
  const router = useRouter();
  const [q, setQ] = useState("");

  const onSearch = (e: React.FormEvent) => {
    e.preventDefault();
    const qs = new URLSearchParams();
    if (q) qs.set("keyword", q);
    router.push(`/${qs.toString() ? `?${qs.toString()}` : ""}`);
  };

  return (
    <header className="sticky top-0 z-30 border-b bg-white/90 backdrop-blur">
      <div className="container mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-3">
        <Link href={"/"} className="text-xl font-extrabold tracking-tight">
          Shop
        </Link>
        <form
          onSubmit={onSearch}
          className="flex w-full max-w-xl items-center gap-2"
        >
          <input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="상품을 검색하세요"
            className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-2 text-sm outline-none focus:border-gray-400"
          />
          <Button>검색</Button>
        </form>
        <AuthNav />
      </div>
    </header>
  );
}
