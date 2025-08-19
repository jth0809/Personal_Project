"use client";
import Link from "next/link";
import Image from "next/image";
import { useCart } from "@/hooks/useCart";
import type { CartItemResponse } from "@/types/backend";

/** 배열/문자열/널 모두를 안전한 문자열로 정규화 */
function pickImage(src?: string | string[] | null): string {
  if (Array.isArray(src)) {
    const first = src.find((s) => typeof s === "string" && s.trim().length > 0);
    return first ?? "/placeholder.png";
  }
  return src && src.trim().length > 0 ? src : "/placeholder.png";
}

export default function CartPage() {
  const { cart, remove } = useCart();

  if (cart.isLoading)
    return <div className="py-20 text-center text-gray-500">불러오는 중…</div>;
  if (cart.error)
    return (
      <div className="py-20 text-center text-red-600">
        장바구니를 불러오지 못했습니다.
      </div>
    );

  const items: CartItemResponse[] = cart.data?.items ?? [];
  const total =
    cart.data?.totalPrice ??
    items.reduce((sum, it) => sum + it.price * it.quantity, 0);

  return (
    <div className="grid gap-8 md:grid-cols-3">
      <div className="md:col-span-2 space-y-3">
        <h1 className="text-xl font-extrabold">장바구니</h1>

        {items.length === 0 ? (
          <div className="rounded-2xl border bg-white p-8 text-center text-gray-500">
            장바구니가 비어 있습니다.{" "}
            <Link href="/" className="underline">
              쇼핑 계속하기
            </Link>
          </div>
        ) : (
          <ul className="space-y-3">
            {items.map((it, idx) => {
              // 고유 key: 서버가 중복 id를 줄 가능성까지 방어
              const key = `${it.cartItemId}-${it.productId}-${idx}`;
              const imgSrc = pickImage(it.imageUrl);
              const alt = it.productName || "상품 이미지";
              return (
                <li
                  key={key}
                  className="flex gap-4 rounded-2xl border bg-white p-4"
                >
                  <div className="relative h-20 w-24 overflow-hidden rounded-lg bg-gray-100">
                    <Image
                      src={imgSrc}
                      alt={alt}
                      fill
                      sizes="96px"
                      className="object-cover"
                    />
                  </div>

                  <div className="flex flex-1 items-center justify-between">
                    <div>
                      <p className="font-medium">{it.productName}</p>
                      <p className="text-sm text-gray-500">
                        수량 {it.quantity}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="font-semibold">
                        {(it.price * it.quantity).toLocaleString()}원
                      </p>
                      <button
                        onClick={() => remove.mutate(it.cartItemId)}
                        className="mt-1 text-sm text-gray-500 underline"
                      >
                        삭제
                      </button>
                    </div>
                  </div>
                </li>
              );
            })}
          </ul>
        )}
      </div>

      <aside className="space-y-3">
        <div className="rounded-2xl border bg-white p-4">
          <h2 className="mb-2 font-semibold">주문 요약</h2>
          <div className="flex items-center justify-between text-sm">
            <span>상품 합계</span>
            <span className="font-semibold">{total.toLocaleString()}원</span>
          </div>
          <button className="mt-4 w-full rounded-xl bg-gray-900 px-6 py-3 text-white hover:bg-black">
            주문하기
          </button>
        </div>
      </aside>
    </div>
  );
}
