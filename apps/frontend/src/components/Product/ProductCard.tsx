"use client";
import Image from "next/image";
import Link from "next/link";
import { Product } from "@/types/product";

export default function ProductCard({ p }: { p: Product }) {
  return (
    <div className="group rounded-2xl border bg-white p-4 shadow-sm transition hover:shadow-md">
      <Link href={`/products/${p.id}`} className="block">
        <div className="relative mb-3 aspect-[4/3] w-full overflow-hidden rounded-xl bg-gray-100">
          <Image
            src={p.image || "/placeholder.png"}
            alt={p.name}
            fill
            className="object-cover transition group-hover:scale-105"
          />
        </div>
        <div className="space-y-1">
          <h3 className="line-clamp-1 text-sm font-medium">{p.name}</h3>
          <div className="flex items-baseline gap-2">
            <span className="text-base font-semibold">
              {p.price.toLocaleString()}원
            </span>
            {p.originalPrice && p.originalPrice > p.price && (
              <span className="text-xs text-gray-400 line-through">
                {p.originalPrice.toLocaleString()}원
              </span>
            )}
          </div>
          <p className="line-clamp-2 h-10 text-xs text-gray-500">
            {p.description}
          </p>
        </div>
      </Link>
    </div>
  );
}
